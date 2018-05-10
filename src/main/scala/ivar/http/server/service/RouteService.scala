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
}

trait RouteService
  extends HelloRoutes
    with UploadRoutes