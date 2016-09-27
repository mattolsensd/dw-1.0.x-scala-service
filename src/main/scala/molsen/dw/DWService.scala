package molsen.dw

import com.porch.dropwizard.configuration.modular.ModularConfigurationSourceProvider
import com.porch.dropwizard.configuration.modular.lua.LuaConfigurationGenerator
import com.porch.dropwizard.core.bundle.DefaultExceptionMappingBundle
import io.dropwizard.Application
import io.dropwizard.setup.{Bootstrap, Environment}

class DWService extends Application[MyConfig] {

  protected override def getName: String = "dw-1.0.x-scala-service"

  protected override def initialize(bootstrap: Bootstrap[MyConfig]): Unit = {
    super.initialize(bootstrap)

    bootstrap.setConfigurationSourceProvider(new ModularConfigurationSourceProvider(getName, new LuaConfigurationGenerator()))
    bootstrap.addBundle(new DefaultExceptionMappingBundle())
  }

  protected override def run(config: MyConfig, env: Environment): Unit = {

    //val jerseyClient: Client = new JerseyClientBuilder(env).using(config.jerseyClient).build(getName)

    env.jersey().register(new MyResource())
  }
}

object DWService {

  def main(args: Array[String]) = new DWService().run(args: _*)

}

