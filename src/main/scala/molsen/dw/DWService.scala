package molsen.dw

import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.porch.dropwizard.configuration.modular.ModularConfigurationSourceProvider
import com.porch.dropwizard.configuration.modular.lua.LuaConfigurationGenerator
import com.porch.dropwizard.core.bundle.DefaultExceptionMappingBundle
import io.dropwizard.Application
import io.dropwizard.auth.AuthDynamicFeature
import io.dropwizard.auth.basic.BasicCredentialAuthFilter
import io.dropwizard.setup.{Bootstrap, Environment}
import molsen.dw.auth.{BasicAuthenticator, BasicAuthorizer, BasicUser}
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature

class DWService extends Application[MyConfig] {

  protected override def getName: String = "dw-1.0.x-scala-service"

  protected override def initialize(bootstrap: Bootstrap[MyConfig]): Unit = {
    super.initialize(bootstrap)

    bootstrap.setConfigurationSourceProvider(new ModularConfigurationSourceProvider(getName, new LuaConfigurationGenerator()))
    bootstrap.addBundle(new DefaultExceptionMappingBundle())
  }

  protected override def run(config: MyConfig, env: Environment): Unit = {
    env.getObjectMapper.registerModule(new DefaultScalaModule)
    env.jersey().register(new MyResource)
    env.jersey().register(new BasicAuthTestResource)

    env.jersey.register(new AuthDynamicFeature(
      new BasicCredentialAuthFilter.Builder[BasicUser]()
        .setAuthenticator(new BasicAuthenticator)
        .setAuthorizer(new BasicAuthorizer)
        .setRealm("DW Test Scala Service")
        .buildAuthFilter))

    env.jersey.register(classOf[RolesAllowedDynamicFeature])
  }
}

object DWService {

  def main(args: Array[String]) = new DWService().run(args: _*)

}

