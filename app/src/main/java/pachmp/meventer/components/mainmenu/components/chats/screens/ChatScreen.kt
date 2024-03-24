package pachmp.meventer.components.mainmenu.components.chats.screens

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
import androidx.compose.foundation.shape.CircleShape
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
import coil.compose.AsyncImage
import pachmp.meventer.R
import pachmp.meventer.components.mainmenu.components.chats.SpeechBubbleShape

data class Message(val sender: String, val text: String, val timestamp: String)

var selectedImageUris by mutableStateOf(emptyList<Uri>())
    private set

fun extendSelectedImageUris(uris: List<Uri>) {
    selectedImageUris += uris
}

fun removeImage(uri: Uri) {
    selectedImageUris = selectedImageUris.filter { it != uri }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(messages: List<Message>) {

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
            isKeyBoardOpen.value = if (keypadHeight > screenHeight * 0.15) true
            else false
        }
        view.viewTreeObserver.addOnGlobalLayoutListener(onGlobalListener)
        onDispose {
            view.viewTreeObserver.removeOnGlobalLayoutListener(onGlobalListener)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Название чата") },
                navigationIcon = {
                    IconButton(onClick = { /* TODO Обработка нажатия кнопки назад */ }) {
                        Icon(Icons.Default.ArrowBackIosNew, contentDescription = "Назад")
                    }
                },
                actions = {
                    Image(
                        painter = painterResource(id = R.drawable.avatar),
                        contentDescription = "Изображение чата",
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                    )
                },
                modifier = if (isKeyBoardOpen.value) Modifier.padding(top = 270.dp) else Modifier.padding(
                    0.dp
                )
            )
        },
        bottomBar = {
            BottomAppBar(modifier = Modifier.heightIn(min = if (selectedImageUris.isEmpty()) 100.dp else 240.dp)) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {

                    if (selectedImageUris.isNotEmpty()) {
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
                        IconButton(onClick = {
                            multiplePhotoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        }, modifier = Modifier.rotate(30f)) {
                            Icon(
                                Icons.Default.AttachFile,
                                contentDescription = "Прикрепить изображение"
                            )
                        }


                        OutlinedTextField(
                            value = "",
                            shape = CircleShape,
                            onValueChange = { /* TODO Обработка изменения текста */ },
                            placeholder = { Text("Сообщение") }
                        )


                        if (selectedImageUris.isNotEmpty()) {
                            IconButton(
                                onClick = { /* TODO Обработка нажатия кнопки "Отправить" */ },
                                modifier = Modifier.padding(start = 8.dp)
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
        }
    ) { paddingValue ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValue),
            contentPadding = PaddingValues(8.dp),
            reverseLayout = true
        ) {
            items(messages.reversed()) { message ->
                MessageCard(message = message)
            }
        }
    }
}

@Composable
fun MessageCard(message: Message) {
    val isSentByUser = message.sender == "Вы"
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
                    Text(text = message.text)
                } else {
                    Text(text = message.sender, fontWeight = FontWeight.Bold)
                    Text(text = message.text)
                }
                Text(
                    text = message.timestamp,
                    color = Color.Gray,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Right
                )
            }
        }
    }
}


@Preview
@Composable
fun PreviewChatScreen() {
    val messages = listOf(
        Message("Вы", "Привет!", "10:00 AM"),
        Message("Мистер Бист", "Hi", "10:05 AM"),
        Message("Вы", "Еда будет?", "10:10 AM"),
        Message("Мистер Бист", "Only FEASTABLES", "10:15 AM"),
        Message("Вы", "Я не приду", "10:20 AM"),
        Message(
            "Мистер Бист",
            "With a market like this, soon you won’t really be able to walk",
            "10:25 AM"
        ),
        Message("Мистер Бист", "Chert", "10:25 AM"),
        Message("Мистер Бист", "Chert", "10:25 AM"),
        Message("Мистер Бист", "Chert", "10:25 AM"),
        Message("Мистер Бист", "Chert", "10:25 AM"),
        Message("Мистер Бист", "Chert", "10:25 AM"),
        Message("Мистер Бист", "Chert", "10:25 AM")
    )

    ChatScreen(messages = messages)
}