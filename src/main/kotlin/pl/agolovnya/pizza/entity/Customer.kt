package pl.agolovnya.pizza.entity

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "customer")
data class Customer(
    @Id
    val id: UUID,
    val email: String,
    @ManyToMany
    @JoinTable(
        name = "customer_topping",
        joinColumns = [JoinColumn(name = "customer_id")],
        inverseJoinColumns = [JoinColumn(name = "topping_id")]
    )
    val toppings: List<Topping>
) {
    override fun toString(): String {
        return "Customer(id=$id, email=$email, toppings=[${toppings.map { it.name }}])"
    }
}
