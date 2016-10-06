package molsen.dw

import com.porch.dropwizard.configuration.modular.ModularConfigurationSourceProvider
import com.porch.dropwizard.configuration.modular.lua.LuaConfigurationGenerator
import com.porch.dropwizard.core.bundle.DefaultExceptionMappingBundle
import io.dropwizard.setup.{Bootstrap, Environment}
import io.dropwizard.{Application, Configuration}

class DWService extends Application[Configuration] {

  protected override def getName: String = "dw-1.0.x-scala-service"

  protected override def initialize(bootstrap: Bootstrap[Configuration]): Unit = {
    super.initialize(bootstrap)

    bootstrap.setConfigurationSourceProvider(new ModularConfigurationSourceProvider(getName, new LuaConfigurationGenerator()))
    bootstrap.addBundle(new DefaultExceptionMappingBundle())
  }

  protected override def run(config: Configuration, env: Environment): Unit = {

    env.jersey().register(new MyResource())
  }
}

object DWService {

  def main(args: Array[String]) = new DWService().run(args: _*)

}

