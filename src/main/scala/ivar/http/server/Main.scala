package ivar.http
package server

import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives
import ivar.http.server.service.RouteService

import scala.util.Properties

object Main extends App with RouteService {
  Http().bindAndHandle(Directives.concat(routes: _*), cfg.getString("akka.http.interface"), Properties.envOrElse("PORT", cfg.getString("akka.http" +
    ".port")).toInt)
}
