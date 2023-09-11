package pl.agolovnya.pizza.service

import io.mockk.MockKAnnotations
import io.mockk.called
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import pl.agolovnya.pizza.dto.ToppingDTO
import pl.agolovnya.pizza.dto.ToppingStatDTO
import pl.agolovnya.pizza.entity.IToppingStat
import pl.agolovnya.pizza.entity.Topping
import pl.agolovnya.pizza.repository.ToppingRepository
import pl.agolovnya.pizza.util.UuidUtils.Companion.getUuid
import java.util.*

@ExtendWith(MockKExtension::class)
class ToppingServiceTest(
    @MockK var toppingRepository: ToppingRepository,
    @InjectMockKs var subject: ToppingService,
) {
    @BeforeEach
    fun setUp() = MockKAnnotations.init(this)

    @Test
    fun save_alreadyExist_success() {
        val toppingName = "peperoni"
        val toppingDTO = ToppingDTO(toppingName)
        val toppingIds = listOf(getUuid(toppingName))
        every {
            toppingRepository.findAllById(toppingIds)
        } returns listOf(Topping(getUuid(toppingName), toppingName, listOf()))


        val result = subject.save(toppingDTO)

        assertEquals(toppingDTO, result)
        verify { toppingRepository.findAllById(toppingIds) }
        verify { toppingRepository.save(any()) wasNot called }
    }

    @Test
    fun save_newTopping_success() {
        val toppingName = "peperoni"
        val toppingDTO = ToppingDTO(toppingName)
        val toppingId = getUuid(toppingName)
        val toppingIds = listOf(toppingId)
        every {
            toppingRepository.findAllById(toppingIds)
        } returns listOf()
        val toppingToSave = Topping(toppingId, toppingDTO.name.lowercase(), listOf())
        every {
            toppingRepository.save(toppingToSave)
        } returns toppingToSave

        val result = subject.save(toppingDTO)

        assertEquals(toppingDTO, result)
        verify { toppingRepository.findAllById(toppingIds) }
        verify(exactly = 1) { toppingRepository.save(toppingToSave) }
    }

    @Test
    fun retrieveToppings_noParam_success() {
        val toppingName = "peperoni"
        val toppingId = getUuid(toppingName)
        val dbTopping = Topping(toppingId, toppingName.lowercase(), listOf())
        every {
            toppingRepository.findAll()
        } returns listOf(dbTopping)

        val result = subject.list(null)

        assertEquals(listOf(ToppingDTO(toppingName)), result)
        verify { toppingRepository.findAll() }
    }

    @Test
    fun retrieveToppings_toppingNameProvidedAndExistInDB_success() {
        val toppingName = "peperoni"
        val toppingId = getUuid(toppingName)
        val dbTopping = Topping(toppingId, toppingName.lowercase(), listOf())
        every {
            toppingRepository.findById(toppingId)
        } returns Optional.of(dbTopping)

        val result = subject.list(toppingName)

        assertEquals(listOf(ToppingDTO(toppingName)), result)
        verify { toppingRepository.findById(toppingId) }
    }

    @Test
    fun retrieveToppings_toppingNameProvidedAndNotExistInDB_success() {
        val toppingName = "peperoni"
        val toppingId = getUuid(toppingName)
        every {
            toppingRepository.findById(toppingId)
        } returns Optional.empty()

        val result = subject.list(toppingName)

        assertEquals(listOf<ToppingDTO>(), result)
        verify { toppingRepository.findById(toppingId) }
    }

    @Test
    fun toppingVotes_valid_success() {
        val toppingName = "peperoni"
        val toppingStat = TestToppingStat(toppingName, 2)
        every {
            toppingRepository.toppingVotes()
        } returns listOf(toppingStat)

        val result = subject.toppingVotes()

        assertEquals(listOf(ToppingStatDTO(toppingStat.topping, toppingStat.vote)), result)
        verify { toppingRepository.toppingVotes() }
    }

    data class TestToppingStat(override val topping: String?, override val vote: Int?) : IToppingStat
}