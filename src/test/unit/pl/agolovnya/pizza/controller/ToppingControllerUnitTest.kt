package pl.agolovnya.pizza.controller

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.web.reactive.server.WebTestClient
import pl.agolovnya.pizza.dto.CustomerDTO
import pl.agolovnya.pizza.dto.ToppingDTO
import pl.agolovnya.pizza.service.ToppingService

@WebMvcTest(controllers = [ToppingController::class])
@AutoConfigureWebTestClient
class ToppingControllerUnitTest {
    @MockkBean
    lateinit var toppingServiceMokk: ToppingService

    @Autowired
    lateinit var webTestClient: WebTestClient

    @Test
    fun save_valid_success() {
        val toppingDTO = ToppingDTO("PEPERONI")

        every {
            toppingServiceMokk.save(any())
        } returns ToppingDTO("peperoni")

        val savedToppingDTO = webTestClient
            .post()
            .uri("/v1/topping")
            .bodyValue(toppingDTO)
            .exchange()
            .expectStatus().isCreated
            .expectBody(CustomerDTO::class.java)
            .returnResult()
            .responseBody

        Assertions.assertNotNull(savedToppingDTO)
        Assertions.assertEquals("artemgolovnya@coherentsolutions.com", savedToppingDTO?.email)
        Assertions.assertEquals(listOf("peperoni"), savedToppingDTO?.toppings)
    }

}