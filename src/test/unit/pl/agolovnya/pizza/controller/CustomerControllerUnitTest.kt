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
import pl.agolovnya.pizza.dto.CustomerDTO
import pl.agolovnya.pizza.service.api.ICustomerService

@WebMvcTest(controllers = [CustomerController::class])
@AutoConfigureWebTestClient
class CustomerControllerUnitTest {
    @MockkBean
    lateinit var customerServiceMokk: ICustomerService

    @Autowired
    lateinit var webTestClient: WebTestClient

    @Test
    fun save_valid_success() {
        val customerDTO = CustomerDTO("ag@coh.com", listOf("PEPERONI", "KJLMK"))

        every {
            customerServiceMokk.save(any())
        } returns CustomerDTO("ag@coh.com", listOf("peperoni"))

        val savedCustomerDTO = webTestClient
            .post()
            .uri("/v1/customer")
            .bodyValue(customerDTO)
            .exchange()
            .expectStatus().isCreated
            .expectBody(CustomerDTO::class.java)
            .returnResult()
            .responseBody

        assertNotNull(savedCustomerDTO)
        assertEquals("ag@coh.com", savedCustomerDTO?.email)
        assertEquals(listOf("peperoni"), savedCustomerDTO?.toppings)
    }

    @Test
    fun save_blankEmail_validationException() {
        val customerDTO = CustomerDTO("", listOf("PEPERONI", "KJLMK"))

        val response = webTestClient
            .post()
            .uri("/v1/customer")
            .bodyValue(customerDTO)
            .exchange()
            .expectStatus().isBadRequest
            .expectBody(String::class.java)
            .returnResult()
            .responseBody

        assertEquals("customerDTO.email is not valid, customerDTO.email must not be empty", response)
    }

    @Test
    fun save_notValidEmail_validationException() {
        val customerDTO = CustomerDTO("artem@.com", listOf("PEPERONI", "KJLMK"))

        val response = webTestClient
            .post()
            .uri("/v1/customer")
            .bodyValue(customerDTO)
            .exchange()
            .expectStatus().isBadRequest
            .expectBody(String::class.java)
            .returnResult()
            .responseBody

        assertEquals("customerDTO.email is not valid", response)
    }

    @Test
    fun getCustomerByEmail_valid_success() {
        val email = "ag@coh.com"
        val uri = UriComponentsBuilder.fromUriString("/v1/customer/by/email")
            .queryParam("email", email)
            .toUriString()

        val customerDTO = CustomerDTO("ag@coh.com", listOf("peperoni"))
        every {
            customerServiceMokk.getCustomerByEmail(email)
        } returns customerDTO

        val responseCourseDTO = webTestClient
            .get()
            .uri(uri)
            .exchange()
            .expectStatus().is2xxSuccessful
            .expectBody(CustomerDTO::class.java)
            .returnResult()
            .responseBody

        assertEquals(customerDTO, responseCourseDTO)
    }

    @Test
    fun getCustomerByEmail_notValidEmail_validationException() {
        val email = "artemgolovnya.com"
        val uri = UriComponentsBuilder.fromUriString("/v1/customer/by/email")
            .queryParam("email", email)
            .toUriString()

        val response = webTestClient
            .get()
            .uri(uri)
            .exchange()
            .expectStatus().isBadRequest
            .expectBody(String::class.java)
            .returnResult()
            .responseBody

        assertEquals("getCustomerByEmail.email: email is not valid", response)
    }
}