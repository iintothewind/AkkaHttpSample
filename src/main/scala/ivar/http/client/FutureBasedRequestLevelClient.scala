package ivar.http
package client


import akka.http.scaladsl.Http
import akka.http.scaladsl.model.Uri.Query
import akka.http.scaladsl.model._

import scala.concurrent.Future
import scala.util.{Failure, Success}

object FutureBasedRequestLevelClient {
  def main(args: Array[String]): Unit = {
    val responseFuture: Future[HttpResponse] =
      Http().singleRequest(
        HttpRequest(uri = Uri("https://aider.meizu.com/app/weather/listWeather").withQuery(Query("cityIds" -> "101240101"))),
        settings = connectionPoolSettings)
    responseFuture.onComplete {
      case Success(resp) => resp.entity.dataBytes.map(_.utf8String).runForeach(println)
      case Failure(_) => sys.error("wrong")
    }
  }
}