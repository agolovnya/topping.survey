package pl.agolovnya.pizza.service.api

import pl.agolovnya.pizza.dto.CustomerDTO

interface ICustomerService {
    /**
     * Allows to store CustomerDTO to DB
     * @param customerDTO customerDto to save
     * @return saved to DB customerDTO
     */
    fun save(customerDTO: CustomerDTO): CustomerDTO

    /**
     * Allows to find customer by email
     * @param email customer email
     * @return customerDTO if found
     */
    fun getCustomerByEmail(email: String): CustomerDTO?
}