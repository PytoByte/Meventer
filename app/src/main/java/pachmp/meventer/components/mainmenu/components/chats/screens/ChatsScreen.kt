package pachmp.meventer.components.mainmenu.components.chats.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination
import pachmp.meventer.components.mainmenu.components.chats.ChatsViewModel
import pachmp.meventer.components.widgets.Background

@ChatsNavGraph(start = true)
@Destination
@Composable
fun ChatsScreen(chatsViewModel: ChatsViewModel = hiltViewModel()) {
    Background()
    Scaffold() { paddingValues ->
        Row(modifier = Modifier.padding(paddingValues), horizontalArrangement = Arrangement.SpaceAround) {
            LazyColumn(modifier = Modifier.weight(0.5f)) {

            }
            Column(modifier = Modifier.weight(0.5f)) {

            }
        }
    }
    // TODO: chats
}