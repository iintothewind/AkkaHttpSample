package ivar.http
package client


import java.net.InetSocketAddress

import akka.http.scaladsl.model._
import akka.http.scaladsl.settings.ConnectionPoolSettings
import akka.http.scaladsl.{ClientTransport, Http}

import scala.concurrent.Future
import scala.util.{Failure, Success}

object WebClient {
  def main(args: Array[String]): Unit = {
    val settings = ConnectionPoolSettings(system)
    //      .withTransport(ClientTransport.httpsProxy(InetSocketAddress.createUnresolved("3.20.128.6", 88)))
    val responseFuture: Future[HttpResponse] =
      Http().singleRequest(
        HttpRequest(uri = "https://psqlsimple.run.aws-eu-central-1-pr.ice.predix.io/student/all"), settings = settings)
    responseFuture.onComplete {
      case Success(res) => println(res)
      case Failure(_) => sys.error("wrong")
    }
  }
}