package molsen.dw

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.porch.dropwizard.configuration.modular.yaml.ConfigPath
import io.dropwizard.Configuration

@ConfigPath("dw-1.0.x-scala-service")
@JsonIgnoreProperties(ignoreUnknown = true)
class MyConfig extends Configuration {

}
