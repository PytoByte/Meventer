package pachmp.meventer.components.mainmenu.components.chats.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ramcosta.composedestinations.annotation.Destination
import io.ktor.websocket.Frame
import pachmp.meventer.R
import pachmp.meventer.components.mainmenu.components.chats.ChatsViewModel
import pachmp.meventer.components.widgets.EmbeddedSearchBar
import pachmp.meventer.components.widgets.LoadingScreen
import pachmp.meventer.data.DTO.Chat
import pachmp.meventer.data.DTO.UserShort
import pachmp.meventer.ui.transitions.BottomTransition


@OptIn(ExperimentalMaterial3Api::class)
@ChatsNavGraph(start = true)
@Destination(style = BottomTransition::class)
@Composable
fun ChatsScreen(chatsViewModel: ChatsViewModel) {
    with(chatsViewModel) {
        Scaffold(
            topBar = {
                EmbeddedSearchBar(
                    query = chatsViewModel.query,
                    onSearch = { chatsViewModel.findChats(it) },
                    onQueryChange = { chatsViewModel.query = it },
                    globalFlag = true,
                    onClearSearch = { clearSearch() }
                )
                TopAppBar(
                    title = { Frame.Text(text = "Chats") },
                    Modifier.background(Color.Blue),
                    //colors = Color.Blue
                )
            },
        ) { paddingValues ->
            visibleChats?.let { chats ->
                if (chats.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(stringResource(R.string.chat_list_empty))
                    }
                } else {
                    Box(modifier = Modifier.padding(paddingValues)) {
                        MessageScreenContent(chatsViewModel, chats, users)
                    }
                }

            } ?: LoadingScreen(Modifier.fillMaxSize())
        }
    }
}

@Composable
fun MessageScreenContent(chatsViewModel: ChatsViewModel, chats: List<Chat>, users: List<UserShort>?) {
    LazyColumn(
        modifier = Modifier.padding(8.dp)
    ) {
        if (users==null) {
            items(chats) { chat ->
                ChatItem(chatsViewModel, chat)
                HorizontalDivider(color = Color.LightGray, thickness = 1.dp)
            }
        } else {
            items(users) { user ->
                UserItem(chatsViewModel, user)
                HorizontalDivider(color = Color.LightGray, thickness = 1.dp)
            }
        }

    }
}

@Composable
fun ChatItem(chatsViewModel: ChatsViewModel, chat: Chat) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { chatsViewModel.navigateToChat(chat) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (chat.originator == null) {
            Icon(
                imageVector = Icons.Default.ChatBubbleOutline,
                contentDescription = "Dialog Icon",
                modifier = Modifier.size(52.dp)
            )
        } else {
            Icon(
                imageVector = Icons.Default.ChatBubbleOutline,
                contentDescription = "Chat Icon",
                modifier = Modifier.size(52.dp)
            )
        }

        Column(modifier = Modifier.padding(start = 16.dp)) {
            Text(
                text = chat.name,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )
            if (chat.lastMessages.isNotEmpty()) {
                Text(
                    text = if (chat.lastMessages.first().body.isNotBlank()) chat.lastMessages.first().body else "Файл",
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun UserItem(chatsViewModel: ChatsViewModel, userShort: UserShort) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { chatsViewModel.navigateToDialog(userShort.id) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Card(modifier = Modifier.size(55.dp), shape = CircleShape) {
            Image(
                modifier = Modifier.fillMaxSize(),
                bitmap = chatsViewModel.getImageFromName(userShort.avatar).value,
                contentDescription = "Dialog Icon",
                contentScale = ContentScale.Crop
            )
        }

        Column(modifier = Modifier.padding(start = 16.dp)) {
            Text(
                text = userShort.nickname,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}