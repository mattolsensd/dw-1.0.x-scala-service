package molsen.dw

import java.net.URI
import javax.validation.Valid
import javax.validation.constraints.NotNull

import com.fasterxml.jackson.annotation.{JsonIgnoreProperties, JsonProperty}
import com.porch.dropwizard.configuration.modular.yaml.ConfigPath
import io.dropwizard.Configuration
import io.dropwizard.client.JerseyClientConfiguration

@ConfigPath("dw-1.0.x-scala-service")
@JsonIgnoreProperties(ignoreUnknown = true)
class MyConfig extends Configuration {

  @Valid
  @NotNull
  @JsonProperty("jerseyClient")
  var jerseyClient: JerseyClientConfiguration = _

  @Valid
  @NotNull
  @JsonProperty("partnerDataUrl")
  var partnerDataUrl: URI = _

}
