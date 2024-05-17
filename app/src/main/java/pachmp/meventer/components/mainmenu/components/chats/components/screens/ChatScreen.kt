package pachmp.meventer.components.mainmenu.components.chats.components.screens

import android.graphics.Rect
import android.net.Uri
import android.view.ViewTreeObserver
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.CreateDocument
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
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
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.ramcosta.composedestinations.annotation.Destination
import pachmp.meventer.R
import pachmp.meventer.components.mainmenu.components.chats.ChatsViewModel
import pachmp.meventer.components.mainmenu.components.chats.components.ChatViewModel
import pachmp.meventer.components.mainmenu.components.chats.components.SpeechBubbleShape
import pachmp.meventer.components.mainmenu.components.chats.ChatsNavGraph
import pachmp.meventer.components.widgets.LoadingScreen
import pachmp.meventer.data.DTO.Message
import pachmp.meventer.data.DTO.MessageSend
import pachmp.meventer.data.enums.FileType
import pachmp.meventer.ui.transitions.BottomTransition
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@ChatsNavGraph
@Destination(style = BottomTransition::class)
@Composable
fun ChatScreen(chatsViewModel: ChatsViewModel, chatViewModel: ChatViewModel = hiltViewModel()) {
    remember {
        if (chatsViewModel.selectedUserID != null) {
            chatViewModel.initFromUserID(chatsViewModel.selectedUserID!!)
        } else {
            chatViewModel.initFromChat(chatsViewModel.selectedChat!!)
        }
    }

    var openFile by remember { mutableStateOf(false) }

    val multiplePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(),
        onResult = { uris ->
            chatViewModel.extendSelectedFileUris(uris)
            openFile = false
        }
    )

    val multipleFilePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments(),
        onResult = { uris ->
            chatViewModel.extendSelectedFileUris(uris)
            openFile = false
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

    if (chatViewModel.chat == null || chatViewModel.appUser == null) {
        LoadingScreen(Modifier.fillMaxSize())
    } else {
        with(chatViewModel) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text(text = chat!!.name) },
                        navigationIcon = {
                            IconButton(onClick = { chatsViewModel.navigateToAllChats() }) {
                                Icon(Icons.Default.ArrowBackIosNew, contentDescription = "Back")
                            }
                        },
                        modifier = if (isKeyBoardOpen.value) Modifier.padding(top = 270.dp) else Modifier.padding(
                            0.dp
                        )
                    )
                },
                bottomBar = {
                    val bottombarheight = if (selectedFilesUris.isNotEmpty() && openFile) {
                        300.dp
                    } else if (selectedFilesUris.isNotEmpty()) {
                        240.dp
                    } else if (openFile) {
                        160.dp
                    } else if (chatViewModel.selectedMessage != null) {
                        160.dp
                    } else {
                        100.dp
                    }

                    BottomAppBar(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = bottombarheight)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            if (selectedFilesUris.isNotEmpty()) {
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
                                        items(selectedFilesUris) { pair ->
                                            if (pair.first == FileType.IMAGE) {
                                                Box(modifier = Modifier.padding(5.dp)) {
                                                    AsyncImage(
                                                        modifier = Modifier
                                                            .width(140.dp)
                                                            .fillMaxHeight()
                                                            .padding(vertical = 5.dp),
                                                        model = pair.second,
                                                        contentDescription = "image",
                                                        contentScale = ContentScale.FillWidth
                                                    )
                                                    IconButton(
                                                        onClick = { removeFile(pair.second) },
                                                        modifier = Modifier
                                                            .align(Alignment.TopEnd)
                                                            .background(Color(0xcacbe0c4))
                                                            .size(20.dp)
                                                    ) {
                                                        Icon(
                                                            Icons.Default.Close,
                                                            contentDescription = "delete"
                                                        )
                                                    }
                                                }
                                            } else {
                                                Box(
                                                    modifier = Modifier
                                                        .padding(5.dp)
                                                        .size(110.dp)
                                                ) {
                                                    Column(
                                                        Modifier.fillMaxSize(),
                                                        horizontalAlignment = Alignment.CenterHorizontally,
                                                        verticalArrangement = Arrangement.Center
                                                    ) {
                                                        Box(
                                                            modifier = Modifier
                                                                .size(80.dp)
                                                                .background(color = Color.Gray),
                                                            contentAlignment = Alignment.Center
                                                        ) {
                                                            Text(chatViewModel.getFileExtension(pair.second))
                                                        }
                                                        Text(chatViewModel.getUriFileName(pair.second))
                                                    }
                                                    IconButton(
                                                        onClick = { removeFile(pair.second) },
                                                        modifier = Modifier
                                                            .align(Alignment.TopEnd)
                                                            .background(Color(0xcacbe0c4))
                                                            .size(20.dp)
                                                    ) {
                                                        Icon(
                                                            Icons.Default.Close,
                                                            contentDescription = "delete"
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            selectedMessage?.let {
                                Card(shape = RoundedCornerShape(5.dp)) {
                                    HorizontalDivider()
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(140.dp)
                                            .padding(5.dp)
                                    ) {
                                        Column {
                                            selectedMessage?.let {
                                                Text(text = stringResource(R.string.editing))
                                                Text(text = it.body, maxLines = 1)
                                            }
                                        }

                                        Spacer(modifier = Modifier.weight(1f))

                                        IconButton(
                                            onClick = { unselectMessage() },
                                            modifier = Modifier.padding(start = 8.dp),
                                        ) {
                                            Icon(
                                                modifier = Modifier.size(25.dp),
                                                imageVector = Icons.Default.Cancel,
                                                contentDescription = "Cancel"
                                            )
                                        }
                                    }
                                }
                            }

                            if (openFile) {
                                Row(
                                    modifier = Modifier
                                        .height(60.dp)
                                        .fillMaxWidth()
                                        .padding(horizontal = 5.dp),
                                    horizontalArrangement = Arrangement.spacedBy(
                                        10.dp,
                                        Alignment.CenterHorizontally
                                    ),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Button(onClick = {
                                        multiplePhotoPickerLauncher.launch(
                                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                        )
                                    }) {
                                        Text(stringResource(R.string.image))
                                    }
                                    Button(onClick = {
                                        multipleFilePickerLauncher.launch(
                                            arrayOf("*/*")
                                        )
                                    }) {
                                        Text(stringResource(R.string.file))
                                    }
                                }
                            }


                            Row(
                                modifier = Modifier.height(100.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (selectedMessage == null) {
                                    IconButton(
                                        onClick = {
                                            openFile = !openFile
                                        }, modifier = Modifier
                                            .rotate(30f)
                                            .weight(0.1f)
                                    ) {
                                        Icon(
                                            Icons.Default.AttachFile,
                                            contentDescription = "load image"
                                        )
                                    }
                                }

                                OutlinedTextField(
                                    modifier = Modifier.weight(0.8f),
                                    value = textToSend,
                                    shape = RoundedCornerShape(5.dp),
                                    onValueChange = { textToSend = it },
                                    placeholder = { Text(stringResource(R.string.message)) },
                                    maxLines = 2
                                )


                                IconButton(
                                    onClick = { selectedMessage?.let { update() } ?: send() },
                                    modifier = Modifier
                                        .padding(start = 8.dp)
                                        .weight(0.1f),
                                    enabled = selectedFilesUris.isNotEmpty() or textToSend.isNotEmpty()
                                ) {
                                    Icon(
                                        Icons.AutoMirrored.Filled.Send,
                                        contentDescription = "send"
                                    )
                                }
                            }

                        }
                    }
                }
            ) { paddingValue ->
                val state = rememberLazyListState()
                val firstIndex = remember { derivedStateOf { state.canScrollForward } }

                if (firstIndex.value.not()) {
                    addMessages()
                }

                LazyColumn(
                    modifier = Modifier
                        .padding(paddingValue)
                        .fillMaxSize(),
                    state = state,
                    contentPadding = PaddingValues(8.dp),
                    reverseLayout = true,
                    verticalArrangement = Arrangement.Bottom
                ) {
                    if (messagesTemp.isNotEmpty()) {
                        items(messagesTemp.reversed()) { messageTemp ->
                            MessageTempCard(chatViewModel, messageTemp)
                        }
                    }

                    items(messagesVisible.reversed()) { message ->
                        MessageCard(chatViewModel, chatsViewModel, message)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MessageCard(chatViewModel: ChatViewModel, chatsViewModel: ChatsViewModel, message: Message) {
    val isSentByUser = message.senderID == chatViewModel.appUser!!.id
    var dropdownMenuExpanded by remember { mutableStateOf(false) }

    val writeDataPermission = rememberLauncherForActivityResult(
        contract = CreateDocument(
            chatViewModel.getMimeType(message.attachment?.toUri() ?: Uri.EMPTY) ?: "*/*"
        )
    ) { uri ->
        if (uri != null && message.attachment != null) {
            chatViewModel.customDownload(uri, message.attachment)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer { scaleX = if (isSentByUser) -1f else 1f }
            .combinedClickable(onClick = {}, onLongClick = {
                dropdownMenuExpanded = true
            })
    ) {
        DropdownMenu(
            expanded = dropdownMenuExpanded,
            onDismissRequest = { dropdownMenuExpanded = false }) {
            if (message.senderID == chatViewModel.appUser!!.id) {
                DropdownMenuItem(text = { Text(stringResource(R.string.edit)) }, onClick = {
                    chatViewModel.selectMessage(message)
                    dropdownMenuExpanded = false
                })
                DropdownMenuItem(text = { Text(stringResource(R.string.delete)) }, onClick = {
                    chatViewModel.delete(message)
                    dropdownMenuExpanded = false
                })
            } else {
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.go_to_dialog, message.senderName)) },
                    onClick = {
                        dropdownMenuExpanded = false
                        chatsViewModel.navigateToDialog(message.senderID)
                    })
            }
        }
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
            ),
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 13.dp, vertical = 8.dp), //13
                horizontalAlignment = if (isSentByUser) Alignment.End else Alignment.Start
            ) {
                if (isSentByUser) {
                    if (message.body.isNotBlank()) {
                        Text(text = message.body)
                    }
                } else {
                    Text(text = message.senderName, fontWeight = FontWeight.Bold)
                    if (message.body.isNotBlank()) {
                        Text(text = message.body)
                    }
                }

                message.attachment?.let { attachment ->
                    val type = FileType.getFileType(attachment)
                    if (type == FileType.IMAGE) {
                        val bitmap = chatViewModel.getImageFromName(attachment).value
                        val showDialog = remember { mutableStateOf(false) }
                        imageDialog(showDialog, bitmap)
                        Image(
                            modifier = Modifier
                                .heightIn(max = 300.dp)
                                .clickable { showDialog.value = true },
                            bitmap = chatViewModel.getImageFromName(attachment).value,
                            contentDescription = "messageAttachment",
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Column(Modifier.width(80.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(80.dp)
                                    .background(color = Color.Gray)
                                    .clickable {
                                        writeDataPermission.launch(attachment)
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(chatViewModel.getFileExtension(attachment))
                            }
                            Text(chatViewModel.getFileName(attachment), fontSize = 15.sp)
                        }
                    }

                }

                Text(
                    text = message.timestamp.atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("hh:mm:ss")),
                    color = Color.Gray,
                    fontSize = 12.sp,
                    textAlign = if (isSentByUser) TextAlign.Right else TextAlign.Start
                )
            }
        }
    }
}

@Composable
fun MessageTempCard(chatViewModel: ChatViewModel, message: MessageSend) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer(scaleX = -1f)
    ) {
        Card(
            modifier = Modifier
                .padding(8.dp)
                .align(Alignment.CenterStart)
                .clip(SpeechBubbleShape())
                .widthIn(max = 300.dp)
                .graphicsLayer(scaleX = -1f),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 13.dp, vertical = 8.dp), //13
                horizontalAlignment = Alignment.End
            ) {
                if (message.body.isNotBlank()) {
                    Text(text = message.body)
                }

                message.attachment?.let { attachment ->
                    val type = FileType.getFileType(attachment)
                    if (type == FileType.IMAGE) {
                        Image(
                            modifier = Modifier.heightIn(max = 300.dp),
                            bitmap = chatViewModel.getImageFromName(attachment).value,
                            contentDescription = "messageAttachment",
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Column(Modifier.width(80.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(80.dp)
                                    .background(color = Color.Gray),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(chatViewModel.getFileExtension(attachment))
                            }
                            Text(chatViewModel.getFileName(attachment), fontSize = 15.sp)
                        }
                    }

                }

                Text(
                    text = stringResource(R.string.sending),
                    color = Color.Gray,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Right
                )
            }
        }
    }
}

@Composable
fun imageDialog(state: MutableState<Boolean>, attachment: ImageBitmap) {
    if (state.value) {
        Dialog(onDismissRequest = {state.value = false}, properties = DialogProperties(usePlatformDefaultWidth = false, dismissOnClickOutside = true, dismissOnBackPress = true)) {
            Box(Modifier.fillMaxSize()) {
                Image(
                    modifier = Modifier.fillMaxSize(),
                    bitmap = attachment,
                    contentDescription = "messageAttachment",
                    contentScale = ContentScale.Fit
                )
                IconButton(modifier = Modifier
                    .align(Alignment.TopEnd).padding(10.dp)
                    .size(50.dp), onClick = { state.value = false }) {
                    Icon(modifier=Modifier.fillMaxSize(), imageVector = Icons.Default.Close, contentDescription = "close", tint = Color.LightGray)
                }
            }
        }
    }

}