package pl.agolovnya.pizza.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotEmpty

data class CustomerDTO(
    @get:Email(message = "customerDTO.email is not valid", regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$")
    @get:NotEmpty(message = "customerDTO.email must not be empty")
    val email: String,
    val toppings: List<String>
)
