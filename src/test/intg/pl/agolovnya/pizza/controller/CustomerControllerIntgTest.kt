package pl.agolovnya.pizza.controller

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.util.UriComponentsBuilder
import pl.agolovnya.pizza.dto.CustomerDTO
import pl.agolovnya.pizza.entity.Customer
import pl.agolovnya.pizza.repository.CustomerRepository
import pl.agolovnya.pizza.repository.ToppingRepository
import pl.agolovnya.pizza.util.UuidUtils
import pl.agolovnya.pizza.util.WithPostgreSQLTestContainer
import pl.agolovnya.pizza.util.toppingsEntityList

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CustomerControllerIntgTest : WithPostgreSQLTestContainer() {

    @Autowired
    lateinit var webTestClient: WebTestClient

    @Autowired
    lateinit var customerRepository: CustomerRepository

    @Autowired
    lateinit var toppingRepository: ToppingRepository

    @BeforeEach
    fun setUp() {
        customerRepository.deleteAll()
        toppingRepository.deleteAll()
        val toppingList = toppingsEntityList()
        toppingRepository.saveAll(toppingList)
    }

    @Test
    fun save_valid_success() {
        val toppings = toppingRepository.findAll()
        val customerDTO = CustomerDTO("artemgolovnya@coherentsolutions.com", toppings.map { it.name })
        val savedCourseDTO = webTestClient
            .post()
            .uri("/v1/customer")
            .bodyValue(customerDTO)
            .exchange()
            .expectStatus().isCreated
            .expectBody(CustomerDTO::class.java)
            .returnResult()
            .responseBody

        assertEquals(savedCourseDTO, customerDTO)
    }

    @Test
    fun getCustomerByEmail_valid_success() {
        val email = "artemgolovnya@coherentsolutions.com"
        val uri = UriComponentsBuilder.fromUriString("/v1/customer/by/email")
            .queryParam("email", email)
            .toUriString()

        val toppings = toppingRepository.findAll()
        val customer = Customer(UuidUtils.getUuid(email), email, toppings.toList())
        customerRepository.save(customer)

        val savedCourseDTO = webTestClient
            .get()
            .uri(uri)
            .exchange()
            .expectStatus().is2xxSuccessful
            .expectBody(CustomerDTO::class.java)
            .returnResult()
            .responseBody

        assertEquals(customer.let { c ->
            CustomerDTO(c.email, c.toppings.map { it.name })
        }, savedCourseDTO)
    }
}