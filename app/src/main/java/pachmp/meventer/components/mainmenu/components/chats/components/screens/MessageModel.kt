package pachmp.meventer.components.mainmenu.components.chats.components.screens

import pachmp.meventer.data.DTO.User
import java.time.Instant

data class MessageModel(
    val id: Long,
    val chatID: Long,
    val body: String,
    val timestamp: Instant,
    val senderName: User?,
    val attachments: List<String>?
)