package ivar.http
package server.route

import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.server.Directives._
import ivar.http.server.protocol.{HelloMessage, HelloProtocol}

trait HelloRoutes extends HelloProtocol {
  register() = path("hello") {
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
    (post & entity(as[HelloMessage])) { msg =>
      complete(msg)
    }
  }
}
