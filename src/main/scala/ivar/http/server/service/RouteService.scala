package ivar.http
package server.service

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import ivar.http.server.route.{HelloRoutes, UploadRoutes}

import scala.collection.mutable.ListBuffer

sealed case class RouteRegister(routeBuffer: ListBuffer[Route]) {
  def update(route: => Route): Unit = {
    routeBuffer += pathPrefix("api")(route)
  }

  def update(prefix: String, route: => Route): Unit = {
    Option(prefix).filter(_.trim.nonEmpty) match {
      case None => routeBuffer += pathPrefix("api")(route)
      case Some(x) => routeBuffer += pathPrefix(x)(route)
    }
  }
}

sealed trait RouteService
  extends HelloRoutes
    with UploadRoutes