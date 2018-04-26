package ivar.http
package server.service

import ivar.http.server.route.{HelloRoutes, UploadRoutes}

trait InitService
  extends HelloRoutes
    with UploadRoutes