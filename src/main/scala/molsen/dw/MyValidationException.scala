package molsen.dw

import javax.ws.rs.WebApplicationException
import javax.ws.rs.core.Response

import com.porch.commons.response.{ApiError, ApiResponse, ValidationError}

import scala.collection.JavaConversions._

class MyValidationException(validationErrors: Seq[ValidationError], message: Option[String] = None) extends WebApplicationException {

  val MESSAGE = "Request validation failed"

  override def getResponse: Response = Response.status(Response.Status.BAD_REQUEST)
    .entity(ApiResponse.failed(new ApiError(ApiError.VALIDATION_ERROR, getMessage, validationErrors))).build()

  override def getMessage: String = message.getOrElse(MESSAGE)

  def getValidationErrors: Seq[ValidationError] = validationErrors

}
