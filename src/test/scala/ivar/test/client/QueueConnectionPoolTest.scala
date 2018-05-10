package ivar.test.client

import java.util.concurrent.CountDownLatch

import akka.http.scaladsl.model.{HttpRequest, StatusCodes}
import akka.http.scaladsl.unmarshalling.Unmarshal
import ivar.http._
import ivar.http.client.pool.QueueConnectionPool
import ivar.http.serdes.{News, ZhihuSerdes}
import ivar.test.AbstractTest

import scala.util.{Failure, Success}

class QueueConnectionPoolTest extends AbstractTest(system) with ZhihuSerdes {

  trait Builder {
    val latch = new CountDownLatch(1)
    val pool: QueueConnectionPool = QueueConnectionPool.httpsPool("news-at.zhihu.com")
  }

  "Latest" should "be fetched" in new Builder {
    pool.enqueue(HttpRequest(uri = "/api/4/news/latest")).onComplete {
      case Success(resp) =>
        resp.status match {
          case StatusCodes.OK => Unmarshal(resp.entity).to[News].map(_.topStories.toList).foreach(println(_))
          case _ => println("error in response")
        }
        latch.countDown()
      case Failure(e) =>
        println(e.getMessage)
        latch.countDown()
    }
    latch.await()
  }
}
