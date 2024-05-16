package pachmp.meventer.data.validators

import androidx.core.text.isDigitsOnly
import pachmp.meventer.data.DTO.EventCreate
import pachmp.meventer.data.DTO.EventUpdate
import java.time.Instant

class EventValidator {
    val ageMax = 1000
    val nameMaxLength = 50
    val descriptionMaxLength = 600
    val priceMax = 1000000

    fun nameValidator(name: String): Boolean {
        return name.isNotBlank() && name.length <= nameMaxLength
    }

    fun descriptionValidator(description: String): Boolean {
        return description.length <= descriptionMaxLength
    }

    fun ageValidator(age: String): Boolean {
        return (age.toIntOrNull() ?: 0) >= 0 && (age.toIntOrNull() ?: 0) <= ageMax && age.isDigitsOnly()
    }

    fun ageValidator(age: Short?): Boolean {
        return (age ?: 0) >= 0 && (age ?: 0) <= ageMax
    }

    fun priceValidator(price: String): Boolean {
        return (price.toIntOrNull() ?: 0) >= 0 && (price.toIntOrNull() ?: 0)<=priceMax && price.isDigitsOnly()
    }

    fun priceValidator(price: Int?): Boolean {
        return (price ?: 0) >= 0 && (price ?: 0)<=priceMax
    }

    fun instantValidator(instant: Instant): Boolean {
        return instant > Instant.now()
    }

    fun eventEntryDataValidate(name: String? =null, minimalAge: String?=null, maximalAge: String? =null, description: String?=null, price: String?=null, startTime: Instant?=null): Boolean {
        return name?.let { nameValidator(it) } ?: true &&
                minimalAge?.let { ageValidator(it) } ?: true &&
                maximalAge?.let { ageValidator(it) } ?: true &&
                description?.let { descriptionValidator(it) } ?: true &&
                price?.let { priceValidator(it) } ?: true &&
                startTime?.let { instantValidator(it) } ?: true
    }

    fun eventDataValidate(name: String? =null, minimalAge: Short?=null, maximalAge: Short? =null, description: String?=null, price: Int?=null, startTime: Instant?=null): Boolean {
        return name?.let { nameValidator(it) } ?: true &&
                minimalAge?.let { ageValidator(it) } ?: true &&
                maximalAge?.let { ageValidator(it) } ?: true &&
                description?.let { descriptionValidator(it) } ?: true &&
                price?.let { priceValidator(it) } ?: true &&
                startTime?.let { instantValidator(it) } ?: true
    }

    fun eventCreateValidate(eventCreate: EventCreate): Boolean {
        return eventDataValidate(
            eventCreate.name,
            eventCreate.minimalAge,
            eventCreate.maximalAge,
            eventCreate.description,
            eventCreate.price,
            eventCreate.startTime
        )
    }

    fun evenUpdateValidate(eventUpdate: EventUpdate): Boolean {
        return eventDataValidate(
            eventUpdate.name,
            eventUpdate.minimalAge,
            eventUpdate.maximalAge,
            eventUpdate.description,
            eventUpdate.price,
            eventUpdate.startTime
        )
    }
}