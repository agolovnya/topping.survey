package pl.agolovnya.pizza.controller

import jakarta.validation.Valid
import jakarta.validation.constraints.Email
import org.springframework.http.HttpStatus
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import pl.agolovnya.pizza.dto.CustomerDTO
import pl.agolovnya.pizza.service.api.ICustomerService

@RestController
@RequestMapping("/v1/customer")
@Validated
class CustomerController(val customerService: ICustomerService) {

    /**
     * Allows to store customer with preferred toppings.
     * If topping list was not stored to database, this topping choice will be ignored,
     * because according to documentation:
     * After collaborating with multiple food supplier services in the area
     * i.e. we know toppings list
     *
     * @param customerDTO customer information
     * @return customerDTO stored in database
     */
    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    fun save(@RequestBody @Valid customerDTO: CustomerDTO): CustomerDTO {

        return customerService.save(customerDTO)
    }

    /**
     * Special endpoint to list toppings choice by email
     *
     * @param email customer email
     */
    @GetMapping("/email")
    fun individual(
        @RequestParam("email")
        @Valid
        @Email(
            message = "email is not valid",
            regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$"
        ) email: String
    ): CustomerDTO? {

        return customerService.individual(email)
    }
}
