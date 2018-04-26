package ivar.http
package client

import java.net.InetSocketAddress

import akka.http.scaladsl.model._
import akka.http.scaladsl.settings.ConnectionPoolSettings
import akka.http.scaladsl.{ClientTransport, Http}

import scala.concurrent.Future
import scala.util.{Failure, Success}

object ZhihuClient extends App {
  val settings = ConnectionPoolSettings(system)
    .withTransport(ClientTransport.httpsProxy(InetSocketAddress.createUnresolved("3.20.128.6", 88)))
  val responseFuture: Future[HttpResponse] =
    Http().singleRequest(
      HttpRequest(uri = "https://news-at.zhihu.com/api/4/news/latest"),
      settings = settings)
  responseFuture.onComplete {
    case Success(res) => println(res)
    case Failure(e) => throw e
  }

}
