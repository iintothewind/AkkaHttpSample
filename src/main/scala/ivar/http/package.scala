package ivar

import java.net.InetSocketAddress
import java.security.cert.X509Certificate

import akka.actor.ActorSystem
import akka.event.{Logging, LoggingAdapter}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import akka.http.scaladsl.settings.ConnectionPoolSettings
import akka.http.scaladsl.{ClientTransport, Http, HttpsConnectionContext}
import akka.stream.ActorMaterializer
import com.typesafe.config.{Config, ConfigFactory}
import com.typesafe.sslconfig.akka.AkkaSSLConfig
import javax.net.ssl.{KeyManager, SSLContext, X509TrustManager}

import scala.collection.mutable.ListBuffer
import scala.concurrent.ExecutionContextExecutor
import scala.util.{Failure, Success, Try}

package object http {
  implicit val cfg: Config = ConfigFactory.load
  implicit val system: ActorSystem = ActorSystem("spider", cfg)
  implicit val executor: ExecutionContextExecutor = system.dispatcher
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val log: LoggingAdapter = Logging.getLogger(system, this)

  private val routeBuffer: ListBuffer[Route] = ListBuffer.empty[Route]

  sealed case class RouteRegister() {
    def update(route: => Route): Unit = {
      routeBuffer += pathPrefix("api")(route)
    }
  }

  implicit val routeBuf: RouteRegister = RouteRegister()

  def routes: List[Route] = routeBuffer.result()

  val connectionPoolSettings: ConnectionPoolSettings = Try(cfg.getConfig("akka.http.client.proxy"))
    .map(c => (c.getString("host"), c.getInt("port"))) match {
    case Success((host, port)) => ConnectionPoolSettings(system)
      .withTransport(ClientTransport.httpsProxy(InetSocketAddress.createUnresolved(host, port)))
    case Failure(_) => ConnectionPoolSettings(system)
  }

  private val looseSslContext: SSLContext = {
    object NoCheckX509TrustManager extends X509TrustManager {
      override def checkClientTrusted(chain: Array[X509Certificate], authType: String): Unit = ()

      override def checkServerTrusted(chain: Array[X509Certificate], authType: String): Unit = ()

      override def getAcceptedIssuers: Array[X509Certificate] = Array[X509Certificate]()
    }

    val context = SSLContext.getInstance("TLS")
    context.init(Array[KeyManager](), Array(NoCheckX509TrustManager), null)
    context
  }

  private val ctx: HttpsConnectionContext = Http().createClientHttpsContext(
    AkkaSSLConfig()
      .mapSettings(s =>
        s.withLoose(
          s.loose
            .withAcceptAnyCertificate(true)
            .withAllowWeakCiphers(true)
            .withDisableSNI(true)
            .withDisableHostnameVerification(true))))

  private val httpsConnectionContext = new HttpsConnectionContext(
    sslContext = looseSslContext,
    sslConfig = ctx.sslConfig,
    enabledCipherSuites = ctx.enabledCipherSuites,
    enabledProtocols = ctx.enabledProtocols,
    clientAuth = ctx.clientAuth,
    sslParameters = ctx.sslParameters
  )
  Http().setDefaultClientHttpsContext(httpsConnectionContext)
}
