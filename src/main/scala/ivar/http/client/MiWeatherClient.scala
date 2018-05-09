package ivar.http
package client

import akka.http.scaladsl.model.Uri.Query
import akka.http.scaladsl.model.{HttpRequest, Uri}
import ivar.http.client.pool.QueueConnectionPool

import scala.util.{Failure, Success}

object MiWeatherClient extends App {
  val pool = QueueConnectionPool.httpsPool("aider.meizu.com")
  pool
    .enqueue(
      HttpRequest(
        uri = Uri("/app/weather/listWeather")
          .withQuery(Query("cityIds" -> "101240101"))))
    .onComplete {
      case Success(resp) => resp.entity.dataBytes.map(_.utf8String).runForeach(println)
      case Failure(e) => throw e
    }

}
