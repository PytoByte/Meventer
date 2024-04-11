package pachmp.meventer.components.mainmenu.components.chats.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.ramcosta.composedestinations.annotation.Destination
import io.ktor.websocket.Frame
import pachmp.meventer.components.mainmenu.components.chats.ChatsViewModel
import pachmp.meventer.components.widgets.LoadingScreen
import pachmp.meventer.data.DTO.Chat
import pachmp.meventer.ui.transitions.BottomTransition


@OptIn(ExperimentalMaterial3Api::class)
@ChatsNavGraph(start = true)
@Destination(style = BottomTransition::class)
@Composable
fun ChatsScreen(chatsViewModel: ChatsViewModel) {
    var isSearchActive by rememberSaveable { mutableStateOf(false) }

    with(chatsViewModel) {
        Scaffold(
            topBar = {
                EmbeddedSearchBar(
                    //onQueryChange = onQueryChange,
                    isSearchActive = isSearchActive,
                    onActiveChanged = { isSearchActive = it }
                )
                TopAppBar(
                    title = { Frame.Text(text = "Chats") },
                    Modifier.background(Color.Blue),
                    //colors = Color.Blue
                )
            },
        ) { paddingValues ->
            chats?.let { chats ->
                Box(modifier = Modifier.padding(paddingValues)) {
                    MessageScreenContent(chatsViewModel, chats)
                }


            } ?: LoadingScreen(Modifier.fillMaxSize())
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MessageScreenContent(chatsViewModel: ChatsViewModel, chats: List<Chat>) {
    LazyColumn(
        modifier = Modifier.padding(8.dp)
    ) {
        items(chats) { chat ->
            ChatItem(chatsViewModel, chat)
            Divider(color = Color.LightGray, thickness = 1.dp)
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
        if (chat.originator==null) {
            Icon(
                imageVector = Icons.Default.BrokenImage,
                contentDescription = "Dialog Icon",
                modifier = Modifier.size(52.dp)
            )
        } else {
            Icon(
                imageVector = Icons.Default.BrokenImage,
                contentDescription = "Chat Icon",
                modifier = Modifier.size(52.dp)
            )
        }

        Column(modifier = Modifier.padding(start = 16.dp)) {
            Text(text = chat.name, style = MaterialTheme.typography.labelMedium)
            if (chat.lastMessages.isNullOrEmpty().not()) {
                Text(text = chat.lastMessages.last().body, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun EmbeddedSearchBar(
    isSearchActive: Boolean,
    onActiveChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    onSearch: ((String) -> Unit)? = null
) {
    var searchQuery by rememberSaveable { mutableStateOf("") }
    val searchHistory = remember { mutableStateListOf("") }
    val activeChanged: (Boolean) -> Unit = { active ->
        //searchQuery = ""
//        onQueryChange("")
        onActiveChanged(active)
    }
    SearchBar(
        query = searchQuery,
        onQueryChange = { query ->
            searchQuery = query
//            onQueryChange(query)
        },
        onSearch = onSearch ?: {
            if (searchHistory.contains(searchQuery)) {
                searchHistory.remove(searchQuery)
            }
            searchHistory.add(0, searchQuery)
            activeChanged(false)
        },
        active = isSearchActive,
        onActiveChange = activeChanged,
        modifier = if (isSearchActive) {
            modifier
                .animateContentSize(spring(stiffness = Spring.StiffnessHigh))
        } else {
            modifier
                .padding(start = 12.dp, top = 2.dp, end = 12.dp, bottom = 12.dp)
                .fillMaxWidth()
                .animateContentSize(spring(stiffness = Spring.StiffnessHigh))
        },
        placeholder = { Text("Поиск") },
        leadingIcon = {
            if (isSearchActive) {
                IconButton(
                    onClick = { activeChanged(false) },
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = "back",
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
            } else {
                Icon(
                    imageVector = Icons.Rounded.Search,
                    contentDescription = "lupa",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        },
        trailingIcon = if (isSearchActive && searchQuery.isNotEmpty()) {
            {
                IconButton(
                    onClick = {
                        searchQuery = ""
//                        onQueryChange("")
                    },
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = "clearField",
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        } else {
            null
        },
        colors = SearchBarDefaults.colors(
            containerColor = if (isSearchActive) {
                MaterialTheme.colorScheme.background
            } else {
                MaterialTheme.colorScheme.surfaceContainerLow
            },
        ),
        tonalElevation = 0.dp,
        windowInsets = if (isSearchActive) {
            SearchBarDefaults.windowInsets
        } else {
            WindowInsets(0.dp)
        }
    ) {
        searchHistory.forEach {
            if (it.isNotEmpty()) {
                Row(modifier = Modifier
                    .padding(all = 14.dp)
                    .fillMaxWidth()
                    .clickable { searchQuery = it },
                    horizontalArrangement = Arrangement.Start
                )
                {
                    Icon(imageVector = Icons.Default.History, contentDescription = null)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(text = it)
                }
            }
        }
        HorizontalDivider()
        Text(
            modifier = Modifier
                .padding(all = 14.dp)
                .fillMaxWidth()
                .clickable {
                    searchHistory.clear()
                },
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            text = "Очистить историю"
        )
        // Search suggestions or results
    }
}