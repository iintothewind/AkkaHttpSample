package ivar.http
package client.pool

import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.stream.scaladsl.{Flow, Keep, Sink, Source, SourceQueueWithComplete}
import akka.stream.{OverflowStrategy, QueueOfferResult}

import scala.concurrent.{Future, Promise}
import scala.util.{Failure, Success, Try}

class QueueConnectionPool(pool: Flow[(HttpRequest, Promise[HttpResponse]), (Try[HttpResponse], Promise[HttpResponse]), Http.HostConnectionPool],
                          bufferSize: Int = 10,
                          overflowStrategy: OverflowStrategy = OverflowStrategy.dropNew
                         ) {
  val queue: SourceQueueWithComplete[(HttpRequest, Promise[HttpResponse])] = Source
    .queue[(HttpRequest, Promise[HttpResponse])](bufferSize, overflowStrategy)
    .via(pool)
    .toMat(Sink.foreach {
      case (Success(response), _) => Future.successful(response)
      case (Failure(e), _) => Future.failed(e)
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
}

object QueueConnectionPool {
  def httpPool(host: String,
               port: Int = 80,
               bufferSize: Int = 10,
               overflowStrategy: OverflowStrategy = OverflowStrategy.dropNew): QueueConnectionPool =
    new QueueConnectionPool(
      Http().cachedHostConnectionPool[Promise[HttpResponse]](host, port, settings = connectionPoolSettings),
      bufferSize,
      overflowStrategy)

  def httpsPool(host: String,
                port: Int = 443,
                bufferSize: Int = 10,
                overflowStrategy: OverflowStrategy = OverflowStrategy.dropNew): QueueConnectionPool =
    new QueueConnectionPool(
      Http().cachedHostConnectionPoolHttps[Promise[HttpResponse]](host, port, settings = connectionPoolSettings),
      bufferSize,
      overflowStrategy)
}
