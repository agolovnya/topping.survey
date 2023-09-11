package pl.agolovnya.pizza.util

import java.util.*

class UuidUtils {
    companion object {
        fun getUuid(name: String): UUID = UUID.nameUUIDFromBytes(name.lowercase().toByteArray(Charsets.UTF_8))
    }
}
