package pl.agolovnya.pizza.repository

import org.springframework.data.repository.CrudRepository
import pl.agolovnya.pizza.entity.Customer
import java.util.*

interface CustomerRepository : CrudRepository<Customer, UUID>
