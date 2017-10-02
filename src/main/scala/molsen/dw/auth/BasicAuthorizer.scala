package molsen.dw.auth

import io.dropwizard.auth.Authorizer

class BasicAuthorizer extends Authorizer[BasicUser] {
  override def authorize(user: BasicUser, role: String): Boolean = user.getRoles != null && user.getRoles.contains(role)
}