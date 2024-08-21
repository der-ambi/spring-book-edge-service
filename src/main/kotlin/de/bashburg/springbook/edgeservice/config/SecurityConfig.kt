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
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.authentication.HttpStatusServerEntryPoint
import org.springframework.security.web.server.authentication.logout.ServerLogoutSuccessHandler
import org.springframework.security.web.server.csrf.CsrfToken
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

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
                csrf.csrfTokenRepository(CookieServerCsrfTokenRepository.withHttpOnlyFalse())
            }
            .build()

    @Bean
    fun csrfWebFilter(): WebFilter {
        // Required because of https://github.com/spring-projects/spring-security/issues/5766
        return WebFilter { exchange: ServerWebExchange, chain: WebFilterChain ->
            exchange.response.beforeCommit {
                Mono.defer {
                    val csrfToken = exchange.getAttribute<Mono<CsrfToken>>(CsrfToken::class.java.name)
                    csrfToken?.then() ?: Mono.empty()
                }
            }
            chain.filter(exchange)
        }
    }

    private fun oidcLogoutSuccessHandler(reactiveClientRegistrationRepository: ReactiveClientRegistrationRepository): ServerLogoutSuccessHandler {
        val oidcClientInitiatedServerLogoutSuccessHandler =
            OidcClientInitiatedServerLogoutSuccessHandler(reactiveClientRegistrationRepository)
        oidcClientInitiatedServerLogoutSuccessHandler.setPostLogoutRedirectUri("{baseUrl}")
        return oidcClientInitiatedServerLogoutSuccessHandler
    }
}