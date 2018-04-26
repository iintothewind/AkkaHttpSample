package ivar.http
package server.route

import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.server.Directives._
import ivar.http.server.protocol.{HelloMessage, HelloProtocol}

trait HelloRoutes extends AbstractRoutes with HelloProtocol {
  private val helloRoute = path("hello") {
    get {
      complete(ToResponseMarshallable("<h1>Say hello to akka-http</h1>"))
    }
  }
  private val nameRoute = pathPrefix("name") {
    (get & path(Segment)) { name =>
      complete(ToResponseMarshallable(s"Your name is: $name"))
    }
  }

  private val msgRoute = path("helloMessage") {
    (post & entity(as[HelloMessage])) { msg =>
      complete(msg)
    }
  }

  register(helloRoute, nameRoute, msgRoute)
}
