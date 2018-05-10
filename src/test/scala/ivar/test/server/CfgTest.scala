package ivar.test.server

import ivar.http._
import ivar.test.AbstractTest

class CfgTest extends AbstractTest(system) {
  "Cfg" should "be like" in {
    println(cfg.getConfig("akka.http.client.proxy"))

  }

}
