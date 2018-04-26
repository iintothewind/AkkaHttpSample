package ivar

import java.security.cert.X509Certificate
import akka.actor.ActorSystem
import akka.event.{Logging, LoggingAdapter}
import akka.http.scaladsl.server._
import akka.http.scaladsl.{Http, HttpsConnectionContext}
import akka.stream.ActorMaterializer
import com.typesafe.config.{Config, ConfigFactory}
import com.typesafe.sslconfig.akka.AkkaSSLConfig
import javax.net.ssl.{KeyManager, SSLContext, X509TrustManager}

import scala.collection.mutable.ListBuffer
import scala.concurrent.ExecutionContextExecutor

package object http {
  implicit val cfg: Config = ConfigFactory.load
  implicit val system: ActorSystem = ActorSystem("spider", cfg)
  implicit val executor: ExecutionContextExecutor = system.dispatcher
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val log: LoggingAdapter = Logging.getLogger(system, this)
  implicit val routes: ListBuffer[Route] = ListBuffer.empty[Route]

  private val trustfulSslContext: SSLContext = {
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
    sslContext = trustfulSslContext,
    sslConfig = ctx.sslConfig,
    enabledCipherSuites = ctx.enabledCipherSuites,
    enabledProtocols = ctx.enabledProtocols,
    clientAuth = ctx.clientAuth,
    sslParameters = ctx.sslParameters
  )
  Http().setDefaultClientHttpsContext(httpsConnectionContext)
}
