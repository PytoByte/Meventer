package pachmp.meventer.data.repository

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.http.headers
import kotlinx.serialization.json.Json
import pachmp.meventer.data.DTO.Event
import pachmp.meventer.data.DTO.EventCreate
import pachmp.meventer.data.DTO.EventOrganizer
import pachmp.meventer.data.DTO.EventSelection
import pachmp.meventer.data.DTO.EventUpdate
import pachmp.meventer.data.DTO.EventsGet
import java.io.File
import javax.inject.Inject

class EventRepository @Inject constructor(
    encryptedSharedPreferences: SharedPreferences,
    @ApplicationContext appContext: Context
) : DefaultRepository(encryptedSharedPreferences, appContext) {
    private val repositoryURL = baseURL + "event/"

    suspend fun createEvent(eventCreate: EventCreate, images: List<File>) = withHttpClient {
        post("${repositoryURL}create") {
            bearerAuth(getToken())
            setBody(
                MultiPartFormDataContent(
                    parts = formData {
                        append(
                            key = "event",
                            value = Json.encodeToString(EventCreate.serializer(), eventCreate),
                            headers = headers {
                                append(
                                    HttpHeaders.ContentType,
                                    "application/json"
                                )
                            })

                        for ((index, image) in images.withIndex()) {
                            append(
                                key = "image$index",
                                value = image.readBytes(),
                                headers = Headers.build {
                                    append(HttpHeaders.ContentType, "image/${image.extension}")
                                    append(
                                        HttpHeaders.ContentDisposition,
                                        "filename=\"${image.name}\""
                                    )
                                }
                            )
                        }
                    }
                )
            )
        }.toResponse<Unit>()
    }

    suspend fun getUserEvents(eventsGet: EventsGet) = withHttpClient {
        post("${repositoryURL}user") {
            bearerAuth(getToken())
            contentType(ContentType.Application.Json)
            setBody(eventsGet)
        }.toResponse<List<Event>>()
    }

    suspend fun deteleEvent(eventID: Int) = withHttpClient {
        post("${repositoryURL}delete") {
            bearerAuth(getToken())
            contentType(ContentType.Application.Json)
            setBody(eventID)
        }.toResponse<Unit>()
    }

    suspend fun editEvent(eventUpdate: EventUpdate, images: List<File>) = withHttpClient {
        post("${repositoryURL}update") {
            bearerAuth(getToken())
            setBody(
                MultiPartFormDataContent(
                    parts = formData {
                        append(
                            key = "update",
                            value = Json.encodeToString(EventUpdate.serializer(), eventUpdate),
                            headers = headers {
                                append(
                                    HttpHeaders.ContentType,
                                    "application/json"
                                )
                            })

                        for ((index, image) in images.withIndex()) {
                            append(
                                key = "image$index",
                                value = image.readBytes(),
                                headers = Headers.build {
                                    append(HttpHeaders.ContentType, "image/${image.extension}")
                                    append(
                                        HttpHeaders.ContentDisposition,
                                        "filename=\"${image.name}\""
                                    )
                                }
                            )
                        }
                    }
                )
            )
        }.toResponse<Unit>()
    }

    suspend fun changeFavourite(eventID: Int) = withHttpClient {
        post("${repositoryURL}changeUsers/featured") {
            bearerAuth(getToken())
            contentType(ContentType.Application.Json)
            setBody(eventID)
        }.toResponse<Unit>()
    }

    suspend fun getEvent(eventID: Int) = withHttpClient {
        get("${repositoryURL}${eventID}") {
        }.toResponse<Event>()
    }

    suspend fun changeUserOrganizer(eventOrganizer: EventOrganizer) = withHttpClient {
        post("${repositoryURL}changeUsers/organizer") {
            bearerAuth(getToken())
            contentType(ContentType.Application.Json)
            setBody(eventOrganizer)
        }.toResponse<Unit>()
    }

    suspend fun changeUserParticipant(eventID: Int) = withHttpClient {
        post("${repositoryURL}changeUsers/participant") {
            bearerAuth(getToken())
            contentType(ContentType.Application.Json)
            setBody(eventID)
        }.toResponse<Unit>()
    }

    suspend fun getGlobalEvents(eventSelection: EventSelection) = withHttpClient {
        post("${repositoryURL}global") {
            bearerAuth(getToken())
            contentType(ContentType.Application.Json)
            setBody(eventSelection)
        }.toResponse<List<Event>>()
    }
}