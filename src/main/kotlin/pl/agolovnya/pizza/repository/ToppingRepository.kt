package pl.agolovnya.pizza.repository

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import pl.agolovnya.pizza.entity.IToppingStat
import pl.agolovnya.pizza.entity.Topping
import java.util.*

interface ToppingRepository : CrudRepository<Topping, UUID> {
    @Query(
        value = "SELECT t.name AS topping, COUNT(DISTINCT(c.customer_id)) AS vote " +
                "FROM topping AS t LEFT JOIN customer_topping AS c ON c.topping_id = t.id " +
                "GROUP BY t.name ORDER BY vote DESC, topping DESC", nativeQuery = true
    )
    fun toppingVotes(): List<IToppingStat>
}
