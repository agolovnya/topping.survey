package pl.agolovnya.pizza.util

import pl.agolovnya.pizza.entity.Customer
import pl.agolovnya.pizza.entity.Topping
import java.util.*

@Suppress("unused")
enum class BasicToppings {
    PEPERONI,
    ONION,
    PEPPER,
    BASILIUS,
    CHEESE
}

fun toppingsEntityList(customer: Customer? = null): List<Topping> {
    val customerList = customer?.let { listOf(it) }.orEmpty()
    return BasicToppings.values().map {
        Topping(
            UUID.nameUUIDFromBytes(it.name.lowercase().toByteArray(Charsets.UTF_8)),
            it.name.lowercase(),
            customerList
        )
    }
}
