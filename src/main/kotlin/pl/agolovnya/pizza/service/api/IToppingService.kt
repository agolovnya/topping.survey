package pl.agolovnya.pizza.service.api

import pl.agolovnya.pizza.dto.ToppingDTO
import pl.agolovnya.pizza.dto.ToppingStatDTO
import pl.agolovnya.pizza.entity.Topping

interface IToppingService {
    /**
     * Allows to find toppings by topping names
     * @param toppings topping names
     * @return found toppings from DB
     */
    fun findToppings(toppings: List<String>): List<Topping>

    /**
     * Allows to save toppingDTO to DB
     * @param toppingDTO to save
     * @return saved toppingDTO
     */
    fun save(toppingDTO: ToppingDTO): ToppingDTO

    /**
     * Allows to retrieve all OR specific toppings
     * @param toppingName optional topping name to find
     * @return list of toppings
     */
    fun list(toppingName: String?): List<ToppingDTO>

    /**
     * Allows to retrieve topping name to vote statistic
     */
    fun toppingVotes(): List<ToppingStatDTO>
}