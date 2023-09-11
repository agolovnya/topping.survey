package pl.agolovnya.pizza.service

import mu.KLogging
import org.springframework.stereotype.Service
import pl.agolovnya.pizza.dto.ToppingDTO
import pl.agolovnya.pizza.dto.ToppingStatDTO
import pl.agolovnya.pizza.entity.Topping
import pl.agolovnya.pizza.repository.ToppingRepository
import pl.agolovnya.pizza.service.api.IToppingService
import pl.agolovnya.pizza.util.UuidUtils.Companion.getUuid
import kotlin.jvm.optionals.getOrNull

@Service
class ToppingService(val toppingRepository: ToppingRepository) : IToppingService {
    companion object : KLogging()

    override fun findToppings(toppings: List<String>): List<Topping> {
        val uuids = toppings.map { getUuid(it) }

        return toppingRepository.findAllById(uuids).toList()
    }

    override fun save(toppingDTO: ToppingDTO): ToppingDTO {
        logger.info("ToppingService.save toppingDTO=$toppingDTO")
        val findToppings = findToppings(listOf(toppingDTO.name))
        if (findToppings.isNotEmpty()) {
            logger.info("ToppingService.save toppingDTO=$toppingDTO already exist")
            return toppingDTO
        }

        //It is a new topping. no customer should be assigned
        val topping = Topping(getUuid(toppingDTO.name), toppingDTO.name.lowercase(), listOf())
        toppingRepository.save(topping)
        logger.info("ToppingService.save topping=$topping saved to DB")

        return ToppingDTO(topping.name)
    }

    override fun list(toppingName: String?): List<ToppingDTO> {
        val toppings = toppingName?.let {
            val dbTopping = toppingRepository.findById(getUuid(toppingName)).getOrNull()
            dbTopping?.let { listOf(it) } ?: listOf()
        } ?: toppingRepository.findAll()

        return toppings.map {
            ToppingDTO(it.name)
        }
    }

    override fun toppingVotes(): List<ToppingStatDTO> {
        return toppingRepository.toppingVotes().map {
            ToppingStatDTO(it.topping, it.vote)
        }
    }
}
