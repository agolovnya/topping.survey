package pl.agolovnya.pizza.controller

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.util.UriComponentsBuilder
import pl.agolovnya.pizza.dto.ToppingDTO
import pl.agolovnya.pizza.dto.ToppingStatDTO
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
            .expectBody(ToppingDTO::class.java)
            .returnResult()
            .responseBody

        assertNotNull(savedToppingDTO)
        assertEquals("peperoni", savedToppingDTO?.name)
    }

    @Test
    fun save_blankName_validationException() {
        val toppingDTO = ToppingDTO("")

        val response = webTestClient
            .post()
            .uri("/v1/topping")
            .bodyValue(toppingDTO)
            .exchange()
            .expectStatus().isBadRequest
            .expectBody(String::class.java)
            .returnResult()
            .responseBody

        assertEquals("toppingDTO.name must not be empty", response)
    }

    @Test
    fun retrieveAllToppings_noParam_success() {
        every {
            toppingServiceMokk.list(any())
        } returns listOf(ToppingDTO("peperoni"))

        val savedToppings = webTestClient
            .get()
            .uri("/v1/topping")
            .exchange()
            .expectStatus().is2xxSuccessful
            .expectBodyList(ToppingDTO::class.java)
            .returnResult()
            .responseBody

        assertNotNull(savedToppings)
        assertEquals(1, savedToppings!!.size)
        assertEquals("peperoni", savedToppings[0].name)
    }

    @Test
    fun retrieveAllToppings_withParam_success() {
        val toppingName = "peperoni"
        val uri = UriComponentsBuilder.fromUriString("/v1/topping")
            .queryParam("toppingName", toppingName)
            .toUriString()
        every {
            toppingServiceMokk.list(toppingName)
        } returns listOf(ToppingDTO(toppingName))

        val savedToppings = webTestClient
            .get()
            .uri(uri)
            .exchange()
            .expectStatus().is2xxSuccessful
            .expectBodyList(ToppingDTO::class.java)
            .returnResult()
            .responseBody

        assertNotNull(savedToppings)
        assertEquals(1, savedToppings!!.size)
        assertEquals(toppingName, savedToppings[0].name)
    }

    @Test
    fun toppingVotes_valid_success() {
        val toppingName = "peperoni"
        val toppingStatDTOList = listOf(
            ToppingStatDTO(toppingName, 10),
            ToppingStatDTO("onion", 8),
            ToppingStatDTO("cucumber", 0)
        )
        every {
            toppingServiceMokk.toppingVotes()
        } returns toppingStatDTOList

        val toppingStats = webTestClient
            .get()
            .uri("/v1/topping/stats")
            .exchange()
            .expectStatus().is2xxSuccessful
            .expectBodyList(ToppingStatDTO::class.java)
            .returnResult()
            .responseBody

        assertNotNull(toppingStats)
        assertEquals(toppingStatDTOList, toppingStats)
    }
}