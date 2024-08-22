package de.bashburg.springbook.edgeservice.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus.UNAUTHORIZED
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.oauth2.client.oidc.web.server.logout.OidcClientInitiatedServerLogoutSuccessHandler
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository
import org.springframework.security.oauth2.client.web.server.WebSessionServerOAuth2AuthorizedClientRepository
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.authentication.HttpStatusServerEntryPoint
import org.springframework.security.web.server.authentication.logout.ServerLogoutSuccessHandler

@Configuration
@EnableWebFluxSecurity
class SecurityConfig {
    @Bean
    fun springSecurityFilterChain(
        serverHttpSecurity: ServerHttpSecurity,
        reactiveClientRegistrationRepository: ReactiveClientRegistrationRepository
    ): SecurityWebFilterChain =
        serverHttpSecurity.authorizeExchange { exchange ->
            exchange
                .pathMatchers("/", "/*.css", "/*.js", "/favicon.ico").permitAll()
                .pathMatchers(HttpMethod.GET, "/books/**").permitAll()
                .anyExchange().authenticated()
        }
            .exceptionHandling { exceptionHandling ->
                exceptionHandling.authenticationEntryPoint(
                    HttpStatusServerEntryPoint(
                        UNAUTHORIZED
                    )
                )
            }
            .oauth2Login(Customizer.withDefaults())
            .logout { logout ->
                logout.logoutSuccessHandler(
                    oidcLogoutSuccessHandler(
                        reactiveClientRegistrationRepository
                    )
                )
            }
            .csrf { csrf ->
                csrf.disable()
            }
            .build()

    private fun oidcLogoutSuccessHandler(reactiveClientRegistrationRepository: ReactiveClientRegistrationRepository): ServerLogoutSuccessHandler {
        val oidcClientInitiatedServerLogoutSuccessHandler =
            OidcClientInitiatedServerLogoutSuccessHandler(reactiveClientRegistrationRepository)
        oidcClientInitiatedServerLogoutSuccessHandler.setPostLogoutRedirectUri("{baseUrl}")
        return oidcClientInitiatedServerLogoutSuccessHandler
    }

    @Bean
    fun authorizedClientRepository() = WebSessionServerOAuth2AuthorizedClientRepository()

}