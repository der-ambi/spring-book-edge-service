package de.bashburg.springbook.edgeservice.user

import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
class UserController {
    @GetMapping("user")
    fun getUser(@AuthenticationPrincipal oidcUser: OidcUser): Mono<User> =
        Mono.just(
            User(
                username = oidcUser.preferredUsername,
                firstName = oidcUser.givenName,
                lastName = oidcUser.familyName,
                oidcUser.getClaimAsStringList("roles")
            )
        )
}