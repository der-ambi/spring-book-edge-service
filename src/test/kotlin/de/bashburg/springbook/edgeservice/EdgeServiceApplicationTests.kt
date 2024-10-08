package de.bashburg.springbook.edgeservice

import com.redis.testcontainers.RedisContainer
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EdgeServiceApplicationTests {

    @MockBean
    lateinit var clientRegistrationRepository: ReactiveClientRegistrationRepository

    @Test
    fun contextLoads() {
    }

    companion object {
        @Container
        @ServiceConnection
        val redis = RedisContainer(RedisContainer.DEFAULT_IMAGE_NAME.withTag("7.4"))
    }
}
