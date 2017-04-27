package molsen.dw

import java.net.URI
import javax.ws.rs.client.{Client, WebTarget}
import javax.ws.rs.core.GenericType

import com.porch.commons.response.ApiResponse

import scala.concurrent.{ExecutionContext, Future}

class MyApiResponseClient(override val baseUri: URI, override val client: Client) extends MyClient(baseUri, client) {

  private val STRING_RESPONSE = new GenericType[ApiResponse[String]]() {}
  private val MY_DTO_RESPONSE = new GenericType[ApiResponse[MyDTO]]() {}

  override def getWebTarget: WebTarget = baseTarget.path("test")

  override def respondWithString()(implicit ec: ExecutionContext): Future[ApiResponse[String]] =
    requestApiResponse(STRING_RESPONSE).from(getWebTarget.path("success/string")).get

  override def respondWithDto()(implicit ec: ExecutionContext): Future[ApiResponse[MyDTO]] =
    requestApiResponse(MY_DTO_RESPONSE).from(getWebTarget.path("success/dto")).get

  override def respondWithFutureDto()(implicit ec: ExecutionContext): Future[ApiResponse[MyDTO]] =
    requestApiResponse(MY_DTO_RESPONSE).from(getWebTarget.path("success/future/dto/sync")).get

  override def respondWithAsyncFutureDto()(implicit ec: ExecutionContext): Future[ApiResponse[MyDTO]] =
    requestApiResponse(MY_DTO_RESPONSE).from(getWebTarget.path("success/future/dto/async")).get

  // FAIL

  override def respondBadRequest()(implicit ec: ExecutionContext): Future[ApiResponse[MyDTO]] =
    requestApiResponse(MY_DTO_RESPONSE).from(getWebTarget.path("failure/bad-request")).get

  override def respondConflict()(implicit ec: ExecutionContext): Future[ApiResponse[MyDTO]] =
    requestApiResponse(MY_DTO_RESPONSE).from(getWebTarget.path("failure/conflict")).get

  override def respondForbidden()(implicit ec: ExecutionContext): Future[ApiResponse[MyDTO]] =
    requestApiResponse(MY_DTO_RESPONSE).from(getWebTarget.path("failure/forbidden")).get

  override def respondNotFound()(implicit ec: ExecutionContext): Future[ApiResponse[MyDTO]] =
    requestApiResponse(MY_DTO_RESPONSE).from(getWebTarget.path("failure/not-found")).get

  override def respondUnauthorized()(implicit ec: ExecutionContext): Future[ApiResponse[MyDTO]] =
    requestApiResponse(MY_DTO_RESPONSE).from(getWebTarget.path("failure/unauthorized")).get

  override def respondValidationError()(implicit ec: ExecutionContext): Future[ApiResponse[MyDTO]] =
    requestApiResponse(MY_DTO_RESPONSE).from(getWebTarget.path("failure/validation-error")).get

  override def respondValidationErrorWithCustomMessage()(implicit ec: ExecutionContext): Future[ApiResponse[MyDTO]] =
    requestApiResponse(MY_DTO_RESPONSE).from(getWebTarget.path("failure/validation-error-with-custom-message")).get

  override def respondISE()(implicit ec: ExecutionContext): Future[ApiResponse[MyDTO]] =
    requestApiResponse(MY_DTO_RESPONSE).from(getWebTarget.path("failure/ise")).get

  // THROW

  override def throwBadRequest()(implicit ec: ExecutionContext): Future[ApiResponse[MyDTO]] =
    requestApiResponse(MY_DTO_RESPONSE).from(getWebTarget.path("throw/bad-request")).get

  override def throwConflict()(implicit ec: ExecutionContext): Future[ApiResponse[MyDTO]] =
    requestApiResponse(MY_DTO_RESPONSE).from(getWebTarget.path("throw/conflict")).get

  override def throwForbidden()(implicit ec: ExecutionContext): Future[ApiResponse[MyDTO]] =
    requestApiResponse(MY_DTO_RESPONSE).from(getWebTarget.path("throw/forbidden")).get

  override def throwNotFound()(implicit ec: ExecutionContext): Future[ApiResponse[MyDTO]] =
    requestApiResponse(MY_DTO_RESPONSE).from(getWebTarget.path("throw/not-found")).get

  override def throwUnauthorized()(implicit ec: ExecutionContext): Future[ApiResponse[MyDTO]] =
    requestApiResponse(MY_DTO_RESPONSE).from(getWebTarget.path("throw/unauthroized")).get

  override def throwValidationError()(implicit ec: ExecutionContext): Future[ApiResponse[MyDTO]] =
    requestApiResponse(MY_DTO_RESPONSE).from(getWebTarget.path("throw/validation-error")).get

  override def throwISE()(implicit ec: ExecutionContext): Future[ApiResponse[MyDTO]] =
    requestApiResponse(MY_DTO_RESPONSE).from(getWebTarget.path("throw/ise")).get

  override def throwWAE()(implicit ec: ExecutionContext): Future[ApiResponse[MyDTO]] =
    requestApiResponse(MY_DTO_RESPONSE).from(getWebTarget.path("throw/wae")).get

  // BREAK

  override def npe()(implicit ec: ExecutionContext): Future[ApiResponse[MyDTO]] = {
    requestApiResponse(MY_DTO_RESPONSE).from(getWebTarget.path("npe")).get
  }

}

