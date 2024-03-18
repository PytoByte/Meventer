package pachmp.meventer.data.repository

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import io.ktor.client.call.body
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
import io.ktor.util.InternalAPI
import kotlinx.serialization.json.Json
import pachmp.meventer.data.DTO.Event
import pachmp.meventer.data.DTO.EventCreate
import pachmp.meventer.data.DTO.EventOrganizer
import pachmp.meventer.data.DTO.EventSelection
import pachmp.meventer.data.DTO.EventUpdate
import pachmp.meventer.data.DTO.EventsGet
import pachmp.meventer.data.DTO.Response
import pachmp.meventer.data.DTO.ResultResponse
import pachmp.meventer.data.DTO.UserRegister
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

class EventRepository @Inject constructor(
    encryptedSharedPreferences: SharedPreferences,
    @ApplicationContext appContext: Context
) : DefaultRepository(encryptedSharedPreferences, appContext) {
    val repositoryURL = baseURL + "event/"

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
        }.body<ResultResponse>()
    }

    suspend fun getUserEvents(eventsGet: EventsGet) = withHttpClient {
        post("${repositoryURL}user") {
            bearerAuth(getToken())
            contentType(ContentType.Application.Json)
            setBody(eventsGet)
        }.body<Response<List<Event>>>()
    }

    suspend fun deteleEvent(eventID: Int) = withHttpClient {
        post("${repositoryURL}delete") {
            bearerAuth(getToken())
            contentType(ContentType.Application.Json)
            setBody(eventID)
        }.body<ResultResponse>()
    }

    suspend fun editEvent(eventUpdate: EventUpdate) = withHttpClient {
        post("${repositoryURL}update") {
            bearerAuth(getToken())
            contentType(ContentType.Application.Json)
            setBody(eventUpdate)
        }.body<ResultResponse>()
    }

    suspend fun changeFavourite(eventID: Int) = withHttpClient {
        post("${repositoryURL}changeUsers/featured") {
            bearerAuth(getToken())
            contentType(ContentType.Application.Json)
            setBody(eventID)
        }.body<ResultResponse>()
    }

    suspend fun getEvent(eventID: Int) = withHttpClient {
        get("${repositoryURL}${eventID}") {
        }.body<Response<Event>>()
    }

    suspend fun changeUserOrganizer(eventOrganizer: EventOrganizer) = withHttpClient {
        post("${repositoryURL}changeUsers/organizer") {
            bearerAuth(getToken())
            contentType(ContentType.Application.Json)
            setBody(eventOrganizer)
        }.body<ResultResponse>()
    }

    /*suspend fun changeUserParticipant(eventSelection: EventSelection) = withHttpClient {
        post("${repositoryURL}changeUsers/participant") {
            bearerAuth(getToken())
            contentType(ContentType.Application.Json)
            setBody(eventSelection)
        }.body<Response<List<Event>>>()
    }*/

    suspend fun getGlobalEvents(eventSelection: EventSelection) = withHttpClient {
        post("${repositoryURL}event/global") {
            bearerAuth(getToken())
            contentType(ContentType.Application.Json)
            setBody(eventSelection)
        }.body<Response<List<Event>>>()
    }
}