package ivar.http
package client

import akka.http.scaladsl.model.{HttpRequest, Uri}
import ivar.http.client.pool.QueueConnectionPool

import scala.util.{Failure, Success}

object MiWeatherClient extends App {
  val pool = QueueConnectionPool.httpsPool("weatherapi.market.xiaomi.com")
  pool
    .enqueue(
      HttpRequest(
        uri = Uri("/wtr-v3/weather/all")
          .withRawQueryString("latitude=110&longitude=112&locationKey=weathercn%3A101010100&days=15&appKey=weather20151024&sign=zUFJoAR2ZVrDy1vF3D07&isGlobal=false&locale=zh_cn")))
    .onComplete {
      case Success(resp) => resp.entity.dataBytes.map(_.utf8String).runForeach(println)
      case Failure(e) => throw e
    }

}
