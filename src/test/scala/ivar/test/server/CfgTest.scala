package ivar.test.server

import ivar.test.AbstractTest

class CfgTest extends AbstractTest {
  "Cfg" should "be like" in {
    println(cfg.getConfig("akka.http.client.proxy"))

  }

}
