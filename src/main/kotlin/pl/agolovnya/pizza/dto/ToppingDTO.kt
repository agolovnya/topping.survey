package pl.agolovnya.pizza.dto

import jakarta.validation.constraints.NotEmpty

data class ToppingDTO(
    @get:NotEmpty(message = "toppingDTO.name must not be empty")
    val name: String
)
