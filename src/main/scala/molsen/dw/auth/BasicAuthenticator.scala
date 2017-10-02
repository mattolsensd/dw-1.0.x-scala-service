package molsen.dw.auth

import java.util
import java.util.{Collections, Optional}

import io.dropwizard.auth.Authenticator
import io.dropwizard.auth.basic.BasicCredentials

object BasicAuthenticator {
  private val TEST_USER: String = "test"
  private val TEST_PASS: String = "test"
  private val TEST_ROLES: util.Set[String] = Collections.singleton("TestRole1")
}

class BasicAuthenticator extends Authenticator[BasicCredentials, BasicUser] {
  override def authenticate(credentials: BasicCredentials): Optional[BasicUser] = {
    if (BasicAuthenticator.TEST_USER == credentials.getUsername
      && BasicAuthenticator.TEST_PASS == credentials.getPassword)
      Optional.of(new BasicUser(credentials.getUsername, BasicAuthenticator.TEST_ROLES))
    else Optional.empty[BasicUser]
  }
}