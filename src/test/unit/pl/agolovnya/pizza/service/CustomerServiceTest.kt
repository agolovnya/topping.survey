package pl.agolovnya.pizza.service

import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import pl.agolovnya.pizza.dto.CustomerDTO
import pl.agolovnya.pizza.entity.Customer
import pl.agolovnya.pizza.entity.Topping
import pl.agolovnya.pizza.repository.CustomerRepository
import pl.agolovnya.pizza.service.api.IToppingService
import pl.agolovnya.pizza.util.UuidUtils.Companion.getUuid
import java.util.*

@ExtendWith(MockKExtension::class)
class CustomerServiceTest(
    @MockK var customerRepository: CustomerRepository,
    @MockK var toppingService: IToppingService,
    @InjectMockKs var subject: CustomerService,
) {
    @BeforeEach
    fun setUp() = MockKAnnotations.init(this)

    @Test
    fun save_valid_success() {
        val toppingName = "peperoni"
        val email = "ag@coh.com"
        val toppings = listOf(Topping(getUuid(toppingName), toppingName, listOf()))
        every {
            toppingService.findToppings(listOf(toppingName))
        } returns toppings
        val customer = Customer(getUuid(email), email, listOf())
        every {
            customerRepository.findById(getUuid(email))
        } returns Optional.of(customer)
        val newCustomer = Customer(getUuid(email), email, toppings)
        every {
            customerRepository.save(customer.copy(toppings = toppings))
        } returns newCustomer
        val customerDTO = CustomerDTO(email, listOf(toppingName))

        val result = subject.save(customerDTO)

        assertEquals(newCustomer.let {
            CustomerDTO(it.email, toppings.map { t -> t.name })
        }, result)
        verify { toppingService.findToppings(listOf(toppingName)) }
        verify { customerRepository.findById(getUuid(customerDTO.email)) }
        verify { customerRepository.save(newCustomer) }
    }

    @Test
    fun save_customerNotFound_success() {
        val toppingName = "peperoni"
        val email = "ag@coh.com"
        val toppings = listOf(Topping(getUuid(toppingName), toppingName, listOf()))
        every {
            toppingService.findToppings(listOf(toppingName))
        } returns toppings
        val customer = Customer(getUuid(email), email, listOf())
        every {
            customerRepository.findById(getUuid(email))
        } returns Optional.empty()
        val newCustomer = Customer(getUuid(email), email, toppings)
        every {
            customerRepository.save(newCustomer)
        } returns newCustomer
        val customerDTO = CustomerDTO(email, listOf(toppingName))

        val result = subject.save(customerDTO)

        assertEquals(newCustomer.let {
            CustomerDTO(it.email, toppings.map { t -> t.name })
        }, result)
        verify { toppingService.findToppings(listOf(toppingName)) }
        verify { customerRepository.findById(getUuid(customerDTO.email)) }
        verify { customerRepository.save(newCustomer) }
    }

    @Test
    fun individual_customerFound_success() {
        val email = "ag@coh.com"
        val customer = Customer(getUuid(email), email, listOf())
        every {
            customerRepository.findById(getUuid(email))
        } returns Optional.of(customer)

        val result = subject.individual(email)

        assertEquals(customer.let {
            CustomerDTO(it.email, listOf())
        }, result)
        verify { customerRepository.findById(getUuid(email)) }
    }

    @Test
    fun individual_customerNotFound_null() {
        val email = "ag@coh.com"
        val customer = Customer(getUuid(email), email, listOf())
        every {
            customerRepository.findById(getUuid(email))
        } returns Optional.empty()

        val result = subject.individual(email)

        assertEquals(null, result)
        verify { customerRepository.findById(getUuid(email)) }
    }
}