package pachmp.meventer.components.mainmenu.components.events.components.editor.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ramcosta.composedestinations.annotation.Destination
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Surface
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.times
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.datetime.time.timepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import pachmp.meventer.components.mainmenu.components.events.EventsViewModel
import pachmp.meventer.components.mainmenu.components.events.components.Rank
import pachmp.meventer.components.mainmenu.components.events.screens.EventsNavGraph
import pachmp.meventer.components.mainmenu.components.events.components.editor.EventEditorViewModel
import pachmp.meventer.components.mainmenu.components.events.components.getUserRank
import pachmp.meventer.components.widgets.LoadingScreen
import pachmp.meventer.components.widgets.MaterialButton
import pachmp.meventer.data.categories
import pachmp.meventer.ui.transitions.FadeTransition
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalFoundationApi::class, ExperimentalLayoutApi::class)
@EventsNavGraph
@Destination(style = FadeTransition::class)
@Composable
fun EditEventScreen(
    eventsViewModel: EventsViewModel,
    eventEditorViewModel: EventEditorViewModel = hiltViewModel(),
) {
    remember{eventEditorViewModel.initEditor(eventsViewModel.selected!!.id, eventsViewModel.user!!.id)}

    with(eventEditorViewModel) {
        if (event != null && appUser != null) {
            val formattedDate by derivedStateOf {
                    DateTimeFormatter
                        .ofPattern("dd MMM yyyy")
                        .withLocale(Locale.getDefault())
                        .format(pickedDate)
                }
            val formattedTime by remember {
                derivedStateOf {
                    DateTimeFormatter
                        .ofPattern("HH:mm")
                        .withLocale(Locale.getDefault())
                        .format(pickedTime)
                }
            }

            val dateDialogState = rememberMaterialDialogState()
            val timeDialogState = rememberMaterialDialogState()
            val deleteDialogState = remember { mutableStateOf(false) }
            val focusManager = LocalFocusManager.current
            var imageEditor by remember { mutableStateOf(false) }
            val animateImageEditor by animateFloatAsState(
                targetValue = if (imageEditor) 1f else 0.2f,
                animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing),
                label = ""
            )
            val pagerState = rememberPagerState {
                derivedStateOf {
                    selectedImageUris.size
                }.value
            }
            val multiplePhotoPickerLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.PickMultipleVisualMedia(),
                onResult = { uris ->
                    extendSelectedImageUris(uris)
                }
            )
            var showAllCategories by remember { mutableStateOf(false) }
            val visibleCategories = if (showAllCategories) categories else categories.take(4)

            MaterialDialog(
                dialogState = dateDialogState,
                buttons = {
                    positiveButton(text = "Ок")
                    //negativeButton(text = "Отмена")
                }
            ) {
                datepicker(
                    initialDate = LocalDate.now(),
                    title = "Выберите дату",
                    allowedDateValidator = {
                        (it.dayOfMonth >= LocalDate.now().dayOfMonth && it.month == LocalDate.now().month && it.year == LocalDate.now().year) || it.year > LocalDate.now().year || it.month > LocalDate.now().month
                    },
                    locale = Locale.getDefault()
                ) {
                    pickedDate = it
                }
            }
            MaterialDialog(
                dialogState = timeDialogState,
                buttons = {
                    positiveButton(text = "Ок")
                    //negativeButton(text = "Отмена")
                }
            ) {
                timepicker(
                    initialTime = LocalTime.now(),
                    title = "Выберите время",
                    timeRange = if (pickedDate == LocalDate.now()) LocalTime.now()..LocalTime.MAX else LocalTime.MIN..LocalTime.MAX,
                    is24HourClock = true
                ) {
                    pickedTime = it
                }
            }

            if (deleteDialogState.value) {
                Dialog(onDismissRequest = {deleteDialogState.value = false}) {
                    Surface {
                        Column(Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(5.dp)) {
                            Text("Вы уверенны что хотите удалить мероприятие?")
                            Row(horizontalArrangement = Arrangement.SpaceAround) {
                                Button(onClick = {eventsViewModel.deleteEvent(event!!.id); deleteDialogState.value=false}) {
                                    Text("Да")
                                }
                                Button(onClick = {deleteDialogState.value = false}) {
                                    Text("Нет")
                                }
                            }
                        }
                    }
                }
            }

            Card(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp), colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(animateImageEditor),
                    contentAlignment = Alignment.BottomEnd

                ) {
                    Column {
                        if (imageEditor) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(max = (animateImageEditor - 0.2f) * 100.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Button(onClick = {
                                    multiplePhotoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                }) {
                                    Text("Добавить изображения")
                                }
                            }
                        }
                        if (selectedImageUris.isEmpty()) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("Картинки не установлены")
                            }
                        } else {
                            HorizontalPager(
                                modifier = Modifier
                                    .fillMaxSize(),
                                state = pagerState,
                                key = { selectedImageUris[it] },
                                beyondBoundsPageCount = 1
                            ) {
                                val imageBitmap = remember { getDefaultImageBitmap() }
                                if (isServerFile(selectedImageUris[it].toString())) {
                                    remember {getImageUri(imageBitmap, selectedImageUris[it])}
                                } else {
                                    remember {getLocalImageUri(imageBitmap, selectedImageUris[it])}
                                }
                                Box() {
                                    Column {
                                        if (imageEditor) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.Center,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                IconButton(
                                                    onClick = { removeSelectedImageUris(it) },
                                                    modifier = Modifier.padding(5.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Filled.Cancel,
                                                        contentDescription = null,
                                                        modifier = Modifier.size(20.dp)
                                                    )
                                                }
                                                if (it == 0) {
                                                    Text("Титульное изображение")
                                                } else {
                                                    Text("Изображение №${it}")
                                                }
                                            }
                                        }
                                        Image(
                                            modifier = Modifier.fillMaxSize(),
                                            bitmap = imageBitmap.value,
                                            contentDescription = "image",
                                            contentScale = ContentScale.FillWidth
                                        )
                                    }
                                }
                            }
                        }
                    }
                    IconButton(
                        onClick = { imageEditor = imageEditor.not() },
                        modifier = Modifier
                            .padding(start = 3.dp)
                            .background(Color.White)
                    ) {
                        Icon(
                            imageVector = if (imageEditor) Icons.Default.Check else Icons.Default.AddPhotoAlternate,
                            contentDescription = "EditPicture",
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
                Scaffold(
                    snackbarHost = { SnackbarHost(parentSnackbarHostState) },
                    bottomBar = {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp)
                                .defaultMinSize(276.dp, 50.dp)
                                .height(50.dp),
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = { eventsViewModel.navigateToEvent(event!!) },
                                modifier = Modifier
                                    .padding(start = 3.dp)
                                    .weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ArrowBackIosNew,
                                    contentDescription = "Back",
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                            MaterialButton(
                                modifier = Modifier
                                    .padding(start = 5.dp, end = 5.dp)
                                    .weight(3f)
                                    .heightIn(40.dp)
                                    .height(50.dp), text = "Сохранить",
                                onClick = {
                                    eventsViewModel.editEvent(getEventUpdate(), filterImages())
                                }
                            )
                            if (getUserRank(event!!, appUser!!)==Rank.ORIGINATOR) {
                                IconButton(
                                    onClick = { deleteDialogState.value=true },
                                    modifier = Modifier
                                        .padding(start = 3.dp)
                                        .weight(1f)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.DeleteForever,
                                        contentDescription = "delete",
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                            }
                        }
                    }
                ) { paddingValues ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .padding(10.dp)
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Создание мероприятия",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier
                                .fillMaxWidth(),
                            textAlign = TextAlign.Start
                        )
                        Spacer(modifier = Modifier.padding(5.dp))

                        Text(
                            text = "Категории",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier
                                .fillMaxWidth(),
                            textAlign = TextAlign.Start
                        )
                        Spacer(modifier = Modifier.padding(5.dp))
                        FlowRow(
                            Modifier
                                .wrapContentHeight()
                                .fillMaxWidth(1f),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            categories.forEach { category ->
                                AnimatedVisibility(
                                    visible = showAllCategories || visibleCategories.contains(
                                        category
                                    ),
                                    enter = fadeIn() + expandVertically(),
                                    exit = fadeOut() + shrinkVertically()
                                ) {
                                    Button(
                                        onClick = {
                                            selectedCategories =
                                                if (selectedCategories.contains(category)) {
                                                    selectedCategories - category
                                                } else {
                                                    selectedCategories + category
                                                }
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (selectedCategories.contains(
                                                    category
                                                )
                                            ) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary.copy(
                                                alpha = 0.2f
                                            ),
                                            contentColor = if (selectedCategories.contains(
                                                    category
                                                )
                                            ) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary
                                        )
                                    ) {
                                        Text(category)
                                    }
                                }
                            }
                        }

                        Text(text = if (showAllCategories) "Скрыть" else "Показать все",
                            modifier = Modifier
                                .clickable { showAllCategories = !showAllCategories })

                        OutlinedTextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = title,
                            onValueChange = { if (it.length <= 50) title = it },
                            label = { Text("Залоговок") },
                            supportingText = { Text("${title.length}/50 *Обязательное поле") },
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.padding(5.dp))
                        Text(
                            text = "Дата и время начала",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier
                                .fillMaxWidth(),
                            textAlign = TextAlign.Start
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            OutlinedTextField(
                                modifier = Modifier
                                    .weight(0.5f)
                                    .onFocusEvent { state ->
                                        if (state.isFocused) {
                                            dateDialogState.show()
                                            focusManager.clearFocus()
                                        }
                                    },
                                value = formattedDate,
                                onValueChange = {},
                                label = { Text("Дата") },
                                singleLine = true,
                                enabled = true,
                                readOnly = true
                            )
                            OutlinedTextField(
                                modifier = Modifier
                                    .weight(0.5f)
                                    .onFocusEvent { state ->
                                        if (state.isFocused) {
                                            timeDialogState.show()
                                            focusManager.clearFocus()
                                        }
                                    },
                                value = formattedTime,
                                onValueChange = {},
                                label = { Text("Время") },
                                singleLine = true,
                                enabled = true,
                                readOnly = true
                            )
                        }

                        Spacer(modifier = Modifier.padding(5.dp))
                        Text(
                            text = "Возрастное ограничение",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier
                                .fillMaxWidth(),
                            textAlign = TextAlign.Start
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            OutlinedTextField(
                                modifier = Modifier.weight(0.5f),
                                value = minAge,
                                onValueChange = { if (it.length <= 3) minAge = it },
                                label = { Text("От") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true
                            )
                            OutlinedTextField(
                                modifier = Modifier.weight(0.5f),
                                value = maxAge,
                                onValueChange = { if (it.length <= 3) maxAge = it },
                                label = { Text("До") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true
                            )
                        }

                        Spacer(modifier = Modifier.padding(5.dp))
                        OutlinedTextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = price,
                            onValueChange = { if (it.length <= 100) price = it },
                            label = { Text("Стоимость") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true
                        )
                        OutlinedTextField(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 200.dp),
                            value = description,
                            onValueChange = { if (it.length <= 600) description = it },
                            label = { Text("Описание") },
                            supportingText = { Text("${description.length}/600") }
                        )
                    }
                }
            }
        } else {
            LoadingScreen(Modifier.fillMaxSize())
        }
    }
}