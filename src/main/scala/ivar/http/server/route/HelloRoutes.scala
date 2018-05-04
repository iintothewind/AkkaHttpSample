package ivar.http
package server.route

import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.server.Directives._
import ivar.http.server.event.{HelloEvent, HelloMsg}

trait HelloRoutes extends HelloEvent {
  private val helloRoute = path("hello") {
    get {
      complete(ToResponseMarshallable("<h1>Say hello to akka-http</h1>"))
    }
  }
  register() = pathPrefix("name") {
    (get & path(Segment)) { name =>
      complete(ToResponseMarshallable(s"Your name is: $name"))
    }
  }

  register() = path("helloMessage") {
    (post & entity(as[HelloMsg])) { msg =>
      complete(msg)
    }
  }
}
