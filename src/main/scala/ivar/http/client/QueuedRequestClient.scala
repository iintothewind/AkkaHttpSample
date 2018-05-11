package ivar.http
package client

import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpRequest, HttpResponse, StatusCodes}
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.scaladsl.{Keep, Sink, Source}
import akka.stream.{OverflowStrategy, QueueOfferResult}
import ivar.http.serdes.{News, Story, ZhihuSerdes}

import scala.concurrent.{Future, Promise}
import scala.util.{Failure, Success}


object QueuedRequestClient extends App with ZhihuSerdes {
  val pool = Http()
    .cachedHostConnectionPoolHttps[Promise[HttpResponse]](
    host = "news-at.zhihu.com",
    settings = connectionPoolSettings)

  val queue = Source
    .queue[(HttpRequest, Promise[HttpResponse])](10, OverflowStrategy.dropNew)
    .via(pool)
    .toMat(Sink.foreach {
      case (Success(response), promise) => promise.success(response)
      case (Failure(e), promise) => promise.failure(e)
    })(Keep.left)
    .run()

  def enqueue(request: HttpRequest): Future[HttpResponse] = {
    val responsePromise = Promise[HttpResponse]()
    queue.offer(request -> responsePromise).flatMap {
      case QueueOfferResult.Enqueued => responsePromise.future
      case QueueOfferResult.Failure(e) => Future.failed(e)
      case QueueOfferResult.Dropped => Future.failed(new RuntimeException("Queue overflowed, Try again later."))
      case QueueOfferResult.QueueClosed => Future.failed(new RuntimeException("Queue was closed (pool shut down)"))
    }
  }


  def listNews(): Future[News] = {
    enqueue(HttpRequest(uri = "/api/4/news/latest")).flatMap { response =>
      response.status match {
        case StatusCodes.OK => Unmarshal(response.entity).to[News]
        case _ => Future.failed(new RuntimeException(s"unexpected response: $response"))
      }
    }
  }

  def fetchStory(id: Int): Future[Story] = {
    enqueue(HttpRequest(uri = s"/api/4/news/$id")).flatMap { response =>
      response.status match {
        case StatusCodes.OK => Unmarshal(response.entity).to[Story]
        case _ => Future.failed(new RuntimeException(s"unexpected response: $response"))
      }
    }
  }

  def listStories(news: Future[News]): Future[List[Story]] = news
    .map(_.stories)
    .flatMap { stories =>
      Future.sequence(
        stories.map(story => fetchStory(story.id)))
    }

  val news = listNews()
  val stories = listStories(news)
  stories.onComplete {
    case Success(s) => s.foreach(println(_))
    case Failure(e) => println(e)
  }
}
