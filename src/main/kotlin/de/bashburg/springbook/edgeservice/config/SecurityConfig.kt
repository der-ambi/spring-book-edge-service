package de.bashburg.springbook.edgeservice.config

import org.springframework.context.annotation.Bean
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain

@EnableWebFluxSecurity
class SecurityConfig {
    @Bean
    fun springSecurityFilterChain(serverHttpSecurity: ServerHttpSecurity): SecurityWebFilterChain =
        serverHttpSecurity.authorizeExchange { exchange -> exchange.anyExchange().authenticated() }
            .oauth2Login(Customizer.withDefaults())
            .build()
}