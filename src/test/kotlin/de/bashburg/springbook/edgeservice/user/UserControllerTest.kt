package de.bashburg.springbook.edgeservice.user

import de.bashburg.springbook.edgeservice.config.SecurityConfig
import org.assertj.core.api.Assertions.assertThat
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository
import org.springframework.security.oauth2.core.oidc.StandardClaimNames
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers
import org.springframework.test.web.reactive.server.WebTestClient
import kotlin.test.Test

@WebFluxTest(UserController::class)
@Import(SecurityConfig::class)
class UserControllerTest {
    @Autowired
    lateinit var webClient: WebTestClient

    @MockBean
    lateinit var clientRegistrationRepository: ReactiveClientRegistrationRepository

    @Test
    fun `when Not Authenticated Then 401`() {
        webClient
            .get()
            .uri("/user")
            .exchange()
            .expectStatus().isUnauthorized
    }

    @Test
    fun `when authenticated then return User`() {
        val expectedUser = User("jon.snow", "Jon", "Snow", listOf("employee", "customer"))
        webClient
            .mutateWith(configureMockOidcLogin(expectedUser))
            .get()
            .uri("/user")
            .exchange()
            .expectStatus().is2xxSuccessful
            .expectBody(User::class.java)
            .value { user -> assertThat(user).isEqualTo(expectedUser) }
    }

    fun configureMockOidcLogin(expectedUser: User) = SecurityMockServerConfigurers.mockOidcLogin().idToken { builder ->
        builder.claim(StandardClaimNames.PREFERRED_USERNAME, expectedUser.username)
        builder.claim(StandardClaimNames.GIVEN_NAME, expectedUser.firstName)
        builder.claim(StandardClaimNames.FAMILY_NAME, expectedUser.lastName)
    }
}