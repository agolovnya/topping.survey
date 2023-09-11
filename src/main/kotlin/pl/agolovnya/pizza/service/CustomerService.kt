package pl.agolovnya.pizza.service

import mu.KLogging
import org.springframework.stereotype.Service
import pl.agolovnya.pizza.dto.CustomerDTO
import pl.agolovnya.pizza.entity.Customer
import pl.agolovnya.pizza.repository.CustomerRepository
import pl.agolovnya.pizza.service.api.ICustomerService
import pl.agolovnya.pizza.service.api.IToppingService
import pl.agolovnya.pizza.util.UuidUtils.Companion.getUuid
import kotlin.jvm.optionals.getOrNull

@Service
class CustomerService(val customerRepository: CustomerRepository, val toppingService: IToppingService) :
    ICustomerService {
    companion object : KLogging()

    override fun save(customerDTO: CustomerDTO): CustomerDTO {
        logger.info("CustomerService.save customerDTO=$customerDTO")
        val existingToppings = toppingService.findToppings(customerDTO.toppings)
        logger.info("CustomerService.save existingToppings=$existingToppings")

        val dbCustomer = customerRepository.findById(getUuid(customerDTO.email)).getOrNull()
        val customer = dbCustomer?.copy(toppings = existingToppings)
            ?: Customer(getUuid(customerDTO.email), getSanitizedEmail(customerDTO.email), existingToppings)
        logger.info("CustomerService.save customer=$customer")
        customerRepository.save(customer)

        return CustomerDTO(customer.email, customer.toppings.map { it.name })
    }

    override fun individual(email: String): CustomerDTO? {
        val customer = customerRepository.findById(getUuid(email)).getOrNull()

        return customer?.let {
            CustomerDTO(customer.email, customer.toppings.map { it.name })
        }
    }

    private fun getSanitizedEmail(email: String) = email.lowercase()
}
