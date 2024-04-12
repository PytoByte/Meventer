package pachmp.meventer.components.mainmenu.components.chats.components.screens

import android.graphics.Rect
import android.net.Uri
import android.view.ViewTreeObserver
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.ramcosta.composedestinations.annotation.Destination
import pachmp.meventer.R
import pachmp.meventer.components.mainmenu.components.chats.ChatsViewModel
import pachmp.meventer.components.mainmenu.components.chats.components.ChatViewModel
import pachmp.meventer.components.mainmenu.components.chats.components.SpeechBubbleShape
import pachmp.meventer.components.mainmenu.components.chats.screens.ChatsNavGraph
import pachmp.meventer.components.mainmenu.components.events.components.editor.EventEditorViewModel
import pachmp.meventer.components.widgets.LoadingScreen
import pachmp.meventer.data.DTO.Message
import pachmp.meventer.ui.transitions.BottomTransition
import java.time.ZoneId
import java.time.format.DateTimeFormatter

var selectedImageUris by mutableStateOf(emptyList<Uri>())
    private set

fun extendSelectedImageUris(uris: List<Uri>) {
    selectedImageUris += uris
}

fun removeImage(uri: Uri) {
    selectedImageUris = selectedImageUris.filter { it != uri }
}

@OptIn(ExperimentalMaterial3Api::class)
@ChatsNavGraph
@Destination(style = BottomTransition::class)
@Composable
fun ChatScreen(chatsViewModel: ChatsViewModel, chatViewModel: ChatViewModel = hiltViewModel()) {
    chatViewModel.initFromChat(chatsViewModel.selectedChat!!)

    val multiplePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(),
        onResult = { uris ->
            extendSelectedImageUris(uris)
        }
    )

    // Здесь можно понять открыта ли клавиатура
    val isKeyBoardOpen = remember { mutableStateOf(false) }
    val view = LocalView.current
    DisposableEffect(view) {
        val onGlobalListener = ViewTreeObserver.OnGlobalLayoutListener {
            val rect = Rect()
            view.getWindowVisibleDisplayFrame(rect)
            val screenHeight = view.rootView.height
            val keypadHeight = screenHeight - rect.bottom
            isKeyBoardOpen.value = (keypadHeight > screenHeight * 0.15)
        }
        view.viewTreeObserver.addOnGlobalLayoutListener(onGlobalListener)
        onDispose {
            view.viewTreeObserver.removeOnGlobalLayoutListener(onGlobalListener)
        }
    }

    with(chatViewModel) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = chat!!.name) },
                    navigationIcon = {
                        IconButton(onClick = { chatsViewModel.navigateToAllChats() }) {
                            Icon(Icons.Default.ArrowBackIosNew, contentDescription = "Назад")
                        }
                    },
                    modifier = if (isKeyBoardOpen.value) Modifier.padding(top = 270.dp) else Modifier.padding(
                        0.dp
                    )
                )
            },
            bottomBar = {
                BottomAppBar(modifier = Modifier.heightIn(min = if (selectedImageUris.isEmpty()) 100.dp else 240.dp)) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {

                        /*if (selectedImageUris.isNotEmpty()) {
                            Card(
                                modifier = Modifier
                                    .height(150.dp)
                                    .fillMaxWidth()
                                    .padding(horizontal = 5.dp)
                            ) {
                                LazyRow(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    items(selectedImageUris) { uri ->
                                        Box(modifier = Modifier.padding(5.dp)) {
                                            AsyncImage(
                                                modifier = Modifier
                                                    .width(140.dp)
                                                    .fillMaxHeight()
                                                    .padding(vertical = 5.dp),
                                                model = uri,
                                                contentDescription = "image",
                                                contentScale = ContentScale.FillWidth
                                            )
                                            IconButton(
                                                onClick = { removeImage(uri) },
                                                modifier = Modifier
                                                    .align(Alignment.TopEnd)
                                                    .background(Color(0xcacbe0c4))
                                                    .size(20.dp)
                                            ) {
                                                Icon(
                                                    Icons.Default.Close,
                                                    contentDescription = "Удалить"
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = {
                                    multiplePhotoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                }, modifier = Modifier
                                    .rotate(30f)
                                    .weight(0.1f)
                            ) {
                                Icon(
                                    Icons.Default.AttachFile,
                                    contentDescription = "Прикрепить изображение"
                                )
                            }*/

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            OutlinedTextField(
                                modifier = Modifier.weight(0.8f),
                                value = textToSend,
                                shape = RoundedCornerShape(5.dp),
                                onValueChange = { textToSend = it },
                                placeholder = { Text("Сообщение") }
                            )


                            IconButton(
                                onClick = { send() },
                                modifier = Modifier
                                    .padding(start = 8.dp)
                                    .weight(0.1f),
                                enabled = selectedImageUris.isNotEmpty() or textToSend.isNotEmpty()
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Filled.Send,
                                    contentDescription = "Отправить"
                                )
                            }
                        }

                    }
                }
            }
        ) { paddingValue ->
            val state = rememberLazyListState()

            if (state.canScrollBackward.not()) {
                addMessages()
            }

            LazyColumn(
                modifier = Modifier
                    .padding(paddingValue),
                state = state,
                contentPadding = PaddingValues(8.dp),
                reverseLayout = true
            ) {
                items(messagesVisible.reversed()) { message ->
                    MessageCard(chatViewModel, message)
                }
            }
        }
    }
}

@Composable
fun MessageCard(chatViewModel: ChatViewModel, message: Message) {
    val isSentByUser = message.senderID == chatViewModel.appUser!!.id
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer { scaleX = if (isSentByUser) -1f else 1f }) {
        Card(
            modifier = Modifier
                .padding(8.dp)
                .align(Alignment.CenterStart)
                .clip(SpeechBubbleShape())
                .widthIn(max = 300.dp)
                .graphicsLayer { scaleX = if (isSentByUser) -1f else 1f },
            colors = CardDefaults.cardColors(
                containerColor = if (isSentByUser) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.primaryContainer.copy(
                    0.6f
                )
            )
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 13.dp, vertical = 8.dp)
            ) {
                if (isSentByUser) {
                    Text(text = message.body)
                } else {
                    Text(text = message.senderName, fontWeight = FontWeight.Bold)
                    Text(text = message.body)
                }

                Text(
                    text = message.timestamp.atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("hh:mm:ss")),
                    color = Color.Gray,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Right
                )
            }
        }
    }
}