package ivar.http
package server.service

import ivar.http.server.route.{HelloRoutes, UploadRoutes}

trait RouteService
  extends HelloRoutes
    with UploadRoutes