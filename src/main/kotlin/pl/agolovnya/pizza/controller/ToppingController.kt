package pl.agolovnya.pizza.controller

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import pl.agolovnya.pizza.dto.ToppingDTO
import pl.agolovnya.pizza.dto.ToppingStatDTO
import pl.agolovnya.pizza.service.api.IToppingService

@RestController
@RequestMapping("/v1/topping")
@Validated
class ToppingController(val toppingService: IToppingService) {

    /**
     * Allows to store toppings to DB
     *
     * @param toppingDTO topping information
     * @return toppingDTO topping stored in database
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun save(@RequestBody @Valid toppingDTO: ToppingDTO): ToppingDTO = toppingService.save(toppingDTO)

    /**
     * Allows to retrieve all OR specific topping from DB
     *
     * @param toppingName optional parameter. if toppingName was not provided - the whole topping list will be returned
     * @return list of toppings stored in database
     */
    @GetMapping
    fun retrieveAllToppings(
        @RequestParam(
            "toppingName",
            required = false
        ) toppingName: String?
    ): List<ToppingDTO> = toppingService.list(toppingName)

    /**
     * Allows to retrieve toppings statistic, i.e. votes for a given topping
     * @return list of toppingName -> vote
     */
    @GetMapping("/stats")
    fun toppingVotes(): List<ToppingStatDTO> = toppingService.toppingVotes()
}
