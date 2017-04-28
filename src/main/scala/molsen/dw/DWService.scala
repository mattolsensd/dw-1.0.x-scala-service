package molsen.dw

import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.porch.dropwizard.client.ClientSupplierBundle
import com.porch.dropwizard.configuration.modular.ModularConfigurationSourceProvider
import com.porch.dropwizard.configuration.modular.lua.LuaConfigurationGenerator
import com.porch.dropwizard.core.bundle.DefaultExceptionMappingBundle
import com.porch.partner.auth.{HeaderAuthenticatorBundle, HeaderAuthenticatorBundleConfiguration}
import io.dropwizard.Application
import io.dropwizard.client.JerseyClientConfiguration
import io.dropwizard.setup.{Bootstrap, Environment}

class DWService extends Application[MyConfig] {

  private val clientSupplierBundle: ClientSupplierBundle[MyConfig] = new ClientSupplierBundle[MyConfig](getName) {
    override def getClientConfiguration(config: MyConfig): JerseyClientConfiguration = config.jerseyClient
  }
  private val headerAuthenticatorBundle = new HeaderAuthenticatorBundle[MyConfig](clientSupplierBundle) {
    override def getConfigInternal(config: MyConfig): HeaderAuthenticatorBundleConfiguration = new HeaderAuthenticatorBundleConfiguration(config.partnerDataUrl)
  }

  protected override def getName: String = "dw-1.0.x-scala-service"

  protected override def initialize(bootstrap: Bootstrap[MyConfig]): Unit = {
    super.initialize(bootstrap)

    bootstrap.setConfigurationSourceProvider(new ModularConfigurationSourceProvider(getName, new LuaConfigurationGenerator()))
    bootstrap.addBundle(new DefaultExceptionMappingBundle())
    bootstrap.addBundle(clientSupplierBundle)
    bootstrap.addBundle(headerAuthenticatorBundle)
  }

  protected override def run(config: MyConfig, env: Environment): Unit = {
    env.getObjectMapper.registerModule(new DefaultScalaModule)
    env.jersey().register(new MyResource)
  }
}

object DWService {

  def main(args: Array[String]) = new DWService().run(args: _*)

}

