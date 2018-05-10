package ivar.http
package client

import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.stream.scaladsl.Source
import ivar.http.serdes.ZhihuSerdes

import scala.util.{Failure, Success}


object FlowBasedRequestLevelClient extends App with ZhihuSerdes {
  val flow = Http().superPool[Unit](settings = connectionPoolSettings)
  Source
    .single((HttpRequest(uri = "https://news-at.zhihu.com/api/4/news/latest"), ()))
    .via(flow)
    .runForeach {
      case (Success(resp), _) => resp.entity.dataBytes.map(_.utf8String).runForeach(println(_))
      case (Failure(e), _) => println(s"resp: $e")
    }


}
