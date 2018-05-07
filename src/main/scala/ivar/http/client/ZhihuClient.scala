package ivar.http
package client

import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{StatusCodes, _}
import akka.http.scaladsl.settings.ConnectionPoolSettings
import akka.http.scaladsl.unmarshalling.Unmarshal
import ivar.http.serdes.{News, ZhihuSerdes}

import scala.concurrent.Future

object ZhihuClient extends App with ZhihuSerdes {
  val settings: ConnectionPoolSettings = ConnectionPoolSettings(system)
  //    .withTransport(ClientTransport.httpsProxy(InetSocketAddress.createUnresolved("3.20.128.6", 88)))
  val responseFuture: Future[HttpResponse] =
    Http().singleRequest(
      HttpRequest(uri = "https://news-at.zhihu.com/api/4/news/latest"),
      settings = settings)
  responseFuture.flatMap { response =>
    response.status match {
      case StatusCodes.OK => Unmarshal(response.entity).to[News]
      case _ => Future.failed(new RuntimeException("unexpected response"))
    }
  }.onComplete(println(_))

}
