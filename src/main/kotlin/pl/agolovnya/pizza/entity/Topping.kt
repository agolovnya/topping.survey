package pl.agolovnya.pizza.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.ManyToMany
import jakarta.persistence.Table
import java.util.*

@Entity
@Table(name = "topping")
data class Topping(
    @Id
    val id: UUID,
    val name: String,
    @ManyToMany(mappedBy = "toppings")
    val customers: List<Customer>
) {
    override fun toString(): String {
        return "Topping(id=$id, name=$name, customers=[${customers.map { it.email }}])"
    }
}
