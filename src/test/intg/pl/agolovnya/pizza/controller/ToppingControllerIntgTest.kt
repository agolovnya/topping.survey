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
import pl.agolovnya.pizza.dto.ToppingDTO
import pl.agolovnya.pizza.dto.ToppingStatDTO
import pl.agolovnya.pizza.entity.Customer
import pl.agolovnya.pizza.entity.Topping
import pl.agolovnya.pizza.repository.CustomerRepository
import pl.agolovnya.pizza.repository.ToppingRepository
import pl.agolovnya.pizza.util.UuidUtils
import pl.agolovnya.pizza.util.WithPostgreSQLTestContainer
import pl.agolovnya.pizza.util.toppingsEntityList
import java.util.*

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ToppingControllerIntgTest : WithPostgreSQLTestContainer() {
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
        val toppingName = "test"
        val newTopping = Topping(
            UUID.nameUUIDFromBytes(toppingName.lowercase().toByteArray(Charsets.UTF_8)),
            toppingName.lowercase(),
            listOf()
        )
        val savedToppingDTO = webTestClient
            .post()
            .uri("/v1/topping")
            .bodyValue(newTopping)
            .exchange()
            .expectStatus().isCreated
            .expectBody(ToppingDTO::class.java)
            .returnResult()
            .responseBody

        assertEquals(newTopping.let { ToppingDTO(it.name) }, savedToppingDTO)
    }

    @Test
    fun retrieveAllToppings_noParams_success() {
        val toppingList = webTestClient
            .get()
            .uri("/v1/topping")
            .exchange()
            .expectStatus().is2xxSuccessful
            .expectBodyList(ToppingDTO::class.java)
            .returnResult()
            .responseBody

        val expectedToppingList = toppingsEntityList().map { ToppingDTO(it.name) }

        assertEquals(expectedToppingList, toppingList)
    }

    @Test
    fun retrieveAllToppings_byName_success() {
        val uri = UriComponentsBuilder.fromUriString("/v1/topping")
            .queryParam("toppingName", "peperoni")
            .toUriString()

        val toppingList = webTestClient
            .get()
            .uri(uri)
            .exchange()
            .expectStatus().is2xxSuccessful
            .expectBodyList(ToppingDTO::class.java)
            .returnResult()
            .responseBody

        val expectedToppingList = listOf(ToppingDTO("peperoni"))

        assertEquals(expectedToppingList, toppingList)
    }

    @Test
    fun toppingVotes_valid_success() {
        val email = "ag@coh.com"
        val toppingsEntityList = toppingsEntityList()
        val customer = Customer(UuidUtils.getUuid(email), email, toppingsEntityList.filter { it.name == "peperoni" })
        customerRepository.save(customer)

        val resultToppingStatList = webTestClient
            .get()
            .uri("/v1/topping/stats")
            .exchange()
            .expectStatus().is2xxSuccessful
            .expectBodyList(ToppingStatDTO::class.java)
            .returnResult()
            .responseBody

        assertEquals(toppingsEntityList.size, resultToppingStatList!!.size)

        assertEquals(listOf(ToppingStatDTO("peperoni", 1)), resultToppingStatList.filter { it!!.vote!! > 0 })
    }
}