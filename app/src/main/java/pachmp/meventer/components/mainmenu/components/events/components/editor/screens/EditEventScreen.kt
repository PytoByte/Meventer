package pachmp.meventer.components.mainmenu.components.events.components.editor.screens

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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.datetime.time.timepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import pachmp.meventer.R
import pachmp.meventer.components.mainmenu.components.events.EventsNavGraph
import pachmp.meventer.components.mainmenu.components.events.EventsViewModel
import pachmp.meventer.components.mainmenu.components.events.components.editor.EventEditorViewModel
import pachmp.meventer.components.widgets.LoadingScreen
import pachmp.meventer.components.widgets.MaterialButton
import pachmp.meventer.data.enums.Ranks
import pachmp.meventer.data.validators.EventValidator
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
    val categories = stringArrayResource(R.array.event_tags)
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
            val visibleCategories = if (showAllCategories) categories else categories.take(4).toTypedArray()

            MaterialDialog(
                dialogState = dateDialogState,
                buttons = {
                    positiveButton(text = stringResource(R.string.ok))
                    //negativeButton(text = "Отмена")
                }
            ) {
                datepicker(
                    initialDate = pickedDate,
                    title = stringResource(R.string.choose_date),
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
                    positiveButton(text = stringResource(R.string.ok))
                    //negativeButton(text = "Отмена")
                }
            ) {
                timepicker(
                    initialTime = LocalTime.now(),
                    title = stringResource(R.string.choose_time),
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
                            Text(stringResource(R.string.event_delete_confirmation))
                            Row(horizontalArrangement = Arrangement.SpaceAround) {
                                Button(onClick = {eventsViewModel.deleteEvent(event!!.id); deleteDialogState.value=false}) {
                                    Text(stringResource(R.string.yes))
                                }
                                Button(onClick = {deleteDialogState.value = false}) {
                                    Text(stringResource(R.string.no))
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
                                    Text(stringResource(R.string.load_image))
                                }
                            }
                        }
                        if (selectedImageUris.isEmpty()) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(stringResource(R.string.no_images))
                            }
                        } else {
                            HorizontalPager(
                                modifier = Modifier
                                    .fillMaxSize(),
                                state = pagerState,
                                key = { selectedImageUris[it] },
                                beyondBoundsPageCount = 1
                            ) {
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
                                                    Text(stringResource(R.string.title_image))
                                                } else {
                                                    Text(stringResource(R.string.numbered_image, it))
                                                }
                                            }
                                        }
                                        Image(
                                            modifier = Modifier.fillMaxSize(),
                                            bitmap = getImageFromUri(selectedImageUris[it]).value,
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
                                    .height(50.dp), text = stringResource(R.string.save),
                                onClick = {
                                    eventsViewModel.editEvent(getEventUpdate(), filterImages())
                                }
                            )
                            if (Ranks.getUserRank(event!!, appUser!!)==Ranks.ORIGINATOR) {
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
                    val eventValidator = EventValidator()

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .padding(10.dp)
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(R.string.event_changing),
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier
                                .fillMaxWidth(),
                            textAlign = TextAlign.Start
                        )
                        Spacer(modifier = Modifier.padding(5.dp))

                        Text(
                            text = stringResource(R.string.categories),
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
                                    visible = showAllCategories || visibleCategories.contains(category),
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

                        Text(text = if (showAllCategories) stringResource(R.string.hide) else stringResource(
                            R.string.show_all
                        ),
                            modifier = Modifier
                                .clickable { showAllCategories = !showAllCategories })

                        var titleIsError by remember { mutableStateOf(false) }
                        OutlinedTextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = title,
                            onValueChange = {title = it; titleIsError = !eventValidator.nameValidator(it)},
                            label = { Text(stringResource(R.string.title)) },
                            supportingText = {
                                Column() {
                                    Text(stringResource(R.string.required_field))
                                    Text(stringResource(R.string.field_max_length, eventValidator.nameMaxLength))
                                    if (titleIsError) {
                                        Text(stringResource(R.string.field_filled_wrong))
                                    }
                                } },
                            isError = titleIsError,
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.padding(5.dp))
                        Text(
                            text = stringResource(R.string.start_date_time),
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier
                                .fillMaxWidth(),
                            textAlign = TextAlign.Start
                        )
                        Row(modifier = Modifier
                            .fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(5.dp)) {
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
                                label = { Text(stringResource(R.string.date)) },
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
                                label = { Text(stringResource(R.string.time)) },
                                singleLine = true,
                                enabled = true,
                                readOnly = true
                            )
                        }

                        Spacer(modifier = Modifier.padding(5.dp))
                        Text(
                            text = stringResource(R.string.age_limit),
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
                            var minAgeIsError by remember { mutableStateOf(false) }
                            OutlinedTextField(
                                modifier = Modifier.weight(0.5f),
                                value = minAge,
                                onValueChange = {minAge = it; minAgeIsError = !eventValidator.ageValidator(it)},
                                label = { Text(stringResource(R.string.from)) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                isError = minAgeIsError,
                                supportingText = {
                                    if (minAgeIsError) {
                                        Text(stringResource(R.string.field_filled_wrong))
                                    }
                                },
                                singleLine = true
                            )
                            var maxAgeIsError by remember { mutableStateOf(false) }
                            OutlinedTextField(
                                modifier = Modifier.weight(0.5f),
                                value = maxAge,
                                onValueChange = {maxAge = it; maxAgeIsError = !eventValidator.ageValidator(it)},
                                label = { Text(stringResource(R.string.to)) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                isError = maxAgeIsError,
                                supportingText = {
                                    if (maxAgeIsError) {
                                        Text(stringResource(R.string.field_filled_wrong))
                                    }
                                }
                            )
                        }

                        Spacer(modifier = Modifier.padding(5.dp))
                        var priceIsError by remember { mutableStateOf(false) }
                        OutlinedTextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = price,
                            onValueChange = {price = it; priceIsError = !eventValidator.priceValidator(it)},
                            isError = priceIsError,
                            supportingText = {
                                if (priceIsError) {
                                    Text(stringResource(R.string.field_filled_wrong))
                                }
                            },
                            label = { Text(stringResource(R.string.price)) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true
                        )
                        var descriptionIsError by remember { mutableStateOf(false) }
                        OutlinedTextField(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 200.dp),
                            value = description,
                            onValueChange = {description = it; descriptionIsError = eventValidator.descriptionValidator(it)},
                            isError = descriptionIsError,
                            label = { Text(stringResource(R.string.description)) },
                            supportingText = {
                                Text(stringResource(R.string.field_max_length, eventValidator.descriptionMaxLength))
                                if (descriptionIsError) {
                                    Text(stringResource(R.string.field_filled_wrong))
                                }
                            }
                        )
                    }
                }
            }
        } else {
            LoadingScreen(Modifier.fillMaxSize())
        }
    }
}