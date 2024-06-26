package pachmp.meventer.components.mainmenu.components.events.components.display.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddModerator
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.PersonRemove
import androidx.compose.material.icons.filled.RemoveModerator
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.gowtham.ratingbar.RatingBar
import com.gowtham.ratingbar.RatingBarConfig
import com.gowtham.ratingbar.RatingBarStyle
import com.ramcosta.composedestinations.annotation.Destination
import pachmp.meventer.R
import pachmp.meventer.components.mainmenu.components.events.EventsNavGraph
import pachmp.meventer.components.mainmenu.components.events.EventsViewModel
import pachmp.meventer.components.mainmenu.components.events.components.display.EventScreenViewModel
import pachmp.meventer.components.mainmenu.components.events.components.display.model.UserModel
import pachmp.meventer.components.widgets.CommentsList
import pachmp.meventer.components.widgets.LoadingScreen
import pachmp.meventer.components.widgets.MaterialButton
import pachmp.meventer.components.widgets.models.FeedbackModel
import pachmp.meventer.data.enums.Ranks
import pachmp.meventer.ui.transitions.FadeTransition
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalFoundationApi::class)
@EventsNavGraph
@Destination(style = FadeTransition::class)
@Composable
fun EventScreen(
    eventsViewModel: EventsViewModel,
    eventScreenViewModel: EventScreenViewModel = hiltViewModel(),
) {
    remember{eventScreenViewModel.init(eventsViewModel.selected!!.id, eventsViewModel.user!!.id)}
    with(eventScreenViewModel) {
        parentSnackbarHostState = eventsViewModel.snackbarHostState
        if (ready == null) {
            LoadingScreen(Modifier.fillMaxSize())
        } else if (ready!!) {
            val showRatingDialog = remember { mutableStateOf(false) }

            val pagerState = rememberPagerState {
                event!!.images.size
            }

            OriginarorFeedbacksDialog(showRatingDialog, originatorFeedbacks, eventScreenViewModel)

            Card(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)

            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.2f)
                ) {
                    HorizontalPager(
                        modifier = Modifier.fillMaxSize(),
                        state = pagerState,
                        key = { event!!.images[it] },
                        beyondBoundsPageCount = 1
                    ) {
                        Image(
                            modifier = Modifier.fillMaxSize(),
                            bitmap = getImageFromName(event!!.images[it]).value,
                            contentDescription = "image",
                            contentScale = ContentScale.Crop
                        )
                    }
                }
                Scaffold(
                    snackbarHost = { SnackbarHost(snackbarHostState) },
                    bottomBar = {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp)
                                .defaultMinSize(276.dp, 50.dp)
                                .height(50.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = { eventsViewModel.navigateToAllEvents() },
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
                                    .padding(top = 4.dp, bottom = 6.dp)
                                    .heightIn(40.dp)
                                    .height(50.dp)
                                    .weight(3f),
                                enabled = appUser!!.ranks != Ranks.ORIGINATOR,
                                text =
                                if ( remember{derivedStateOf{allMembers!!.find { it.id==appUser!!.id }}}.value!=null )
                                    stringResource(R.string.leave)
                                else
                                    stringResource(R.string.join),
                                onClick = { changeUserParticipant() }
                            )
                            if (appUser!!.ranks.value >= Ranks.ORGANIZER.value) {
                                IconButton(
                                    onClick = { eventsViewModel.navigateToEditEvent() },
                                    modifier = Modifier
                                        .padding(end = 5.dp)
                                        .weight(1f)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Settings,
                                        contentDescription = "Settings",
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                            }
                        }
                    }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(it)
                            .padding(top = 8.dp, start = 15.dp, end = 15.dp)
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        MarqueeText(text = event!!.name)

                        LazyRow(modifier = Modifier.fillMaxWidth()) {
                            items(event!!.tags) {
                                Text("#${it}")
                                Spacer(modifier = Modifier.size(5.dp))
                            }
                        }

                        RatingBar(
                            value = originatorRating,
                            config = RatingBarConfig().numStars(5)
                                .style(RatingBarStyle.HighLighted)
                                .size(30.dp),
                            onValueChange = {},
                            onRatingChanged = { showRatingDialog.value = true })

                        EventParam(
                            name = stringResource(R.string.start),
                            value = event!!.startTime.atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd | hh:mm:ss"))
                        )

                        EventParam(
                            name = stringResource(R.string.age_limit),
                            value =
                            if (event!!.maximalAge == null || event!!.maximalAge == 999.toShort())
                                "${event!!.minimalAge}+"
                            else
                                "${event!!.minimalAge} - ${event!!.maximalAge}"
                        )

                        EventParam(
                            name = stringResource(R.string.price),
                            value =
                            if (event!!.price == 0)
                                stringResource(R.string.free)
                            else
                                "${event!!.price}₽"
                        )

                        EventParam(
                            name = stringResource(R.string.description),
                            value = event!!.description
                        )

                        Spacer(modifier = Modifier.padding(5.dp))
                        Text(
                            text = stringResource(R.string.event_participants),
                            style = MaterialTheme.typography.titleMedium
                        )
                        LazyColumn(modifier = Modifier.heightIn(max = 200.dp)) {
                            item {
                                if (originator!!.image==null) {
                                    originator!!.image = getImageFromName(originator!!.avatar)
                                }
                                UserItem(
                                    user = originator!!,
                                    appUser = appUser!!,
                                    imageBitmap = originator!!.image ?: getDefaultImageBitmap(),
                                    eventScreenViewModel
                                )
                                HorizontalDivider()
                            }

                            itemsIndexed(organizers!!) { index, item ->
                                if (item.image==null) {
                                    organizers!![index].image = getImageFromName(item.avatar)
                                }
                                UserItem(
                                    user = item,
                                    appUser = appUser!!,
                                    imageBitmap = item.image ?: getDefaultImageBitmap(),
                                    eventScreenViewModel
                                )
                                HorizontalDivider()
                            }
                            itemsIndexed(participants!!) { index, item ->
                                if (item.image==null) {
                                    participants!![index].image = getImageFromName(item.avatar)
                                }

                                UserItem(
                                    user = item,
                                    appUser = appUser!!,
                                    imageBitmap = item.image ?: getDefaultImageBitmap(),
                                    eventScreenViewModel
                                )
                                HorizontalDivider()
                            }
                        }
                    }
                }
            }
        } else {
            eventsViewModel.navigateToAllEvents()
        }
    }
}

@Composable
fun EventParam(name: String, value: String, modifier:Modifier = Modifier) {
    Row(modifier, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
        Text(
            text = "${name}:",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            value,
            fontSize = 18.sp
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun UserItem(
    user: UserModel?,
    appUser: UserModel,
    imageBitmap: MutableState<ImageBitmap>,
    eventScreenViewModel: EventScreenViewModel,
) {
    with(eventScreenViewModel) {
        if (user == null) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(text = stringResource(R.string.user_loading_failed))
            }

        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp)
                    .combinedClickable(onClick = {}, onLongClick = {

                    }),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Image(
                    bitmap = imageBitmap.value,
                    contentDescription = "avatar",
                    Modifier
                        .clip(CircleShape)
                        .size(60.dp),
                    contentScale = ContentScale.Crop
                )
                Column(modifier = Modifier.padding(4.dp), verticalArrangement = Arrangement.Center) {
                    if (user.id == appUser.id) Text("${user.name!!} (${stringResource(R.string.you)})") else Text(user.name!!)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Text(text = stringResource(id = user.ranks.titleResourceID))
                        if (appUser.ranks == Ranks.ORIGINATOR && user.id != appUser.id) {
                            IconButton(onClick = { eventScreenViewModel.changeUserOrganizer(user) }) {
                                Icon(
                                    imageVector = if (remember{derivedStateOf{organizers!!.find { it.id==user.id }}}.value!=null) Icons.Default.RemoveModerator else Icons.Default.AddModerator,
                                    contentDescription = "organizer"
                                )
                            }
                        }
                        if (appUser.ranks.value > user.ranks.value) {
                            IconButton(onClick = { kickUser(user) }) {
                                Icon(
                                    imageVector = Icons.Default.PersonRemove,
                                    contentDescription = "kick"
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MarqueeText(text: String) {
    var marqueeEnabled by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        if (marqueeEnabled) {
            Text(
                text = text,
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp,
                maxLines = 2,
                modifier = Modifier
                    .basicMarquee(
                        iterations = Int.MAX_VALUE,
                        delayMillis = 1000,
                        initialDelayMillis = 1000,
                        velocity = 60.dp
                    )
            )
        } else {
            Text(
                text = text,
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp,
                maxLines = 2,
                onTextLayout = { textLayoutResult ->
                    if (textLayoutResult.hasVisualOverflow) {
                        marqueeEnabled = true
                    }
                }
            )
        }
    }
}


@Composable
fun OriginarorFeedbacksDialog(
    visible: MutableState<Boolean>,
    feedbackModels: List<FeedbackModel>?,
    eventScreenViewModel: EventScreenViewModel,
) {
    if (visible.value) {
        with(eventScreenViewModel) {
            Dialog(onDismissRequest = { visible.value = false }) {
                Surface {
                    if (feedbackModels != null) {
                        Column(
                            Modifier
                                .padding(10.dp)
                                .verticalScroll(rememberScrollState()),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            if (appUser!!.id != originator!!.id) {
                                val feedback = feedbackModels.find { it.author?.id == appUser!!.id }

                                Text(stringResource(R.string.leave_feedback))
                                RatingBar(
                                    value = rating,
                                    config = RatingBarConfig().numStars(5)
                                        .style(RatingBarStyle.HighLighted)
                                        .size(30.dp),
                                    onValueChange = {},
                                    onRatingChanged = { rating = it })
                                OutlinedTextField(value = comment, onValueChange = { comment = it })
                                if (feedback == null) {
                                    Button(onClick = { eventScreenViewModel.createFeedback() }) {
                                        Text(stringResource(R.string.send))
                                    }
                                } else {
                                    Row(horizontalArrangement = Arrangement.SpaceAround) {
                                        Button(onClick = { updateFeedback() }) {
                                            Text(stringResource(R.string.edit))
                                        }
                                        Button(onClick = { deleteFeedback() }) {
                                            Text(stringResource(R.string.delete))
                                        }
                                    }
                                }
                            }
                            Text(stringResource(R.string.feedbacks))
                            if (feedbackModels.isEmpty()) {
                                Text(stringResource(R.string.empty_feedback_list))
                            } else {
                                Box(
                                    Modifier
                                        .fillMaxWidth()
                                        .heightIn(max = 200.dp)
                                ) {
                                    CommentsList(eventScreenViewModel, feedbackModels)
                                }
                            }
                            Button(onClick = { visible.value = false }) {
                                Text(stringResource(R.string.close))
                            }
                        }
                    } else {
                        LoadingScreen(Modifier.fillMaxWidth())
                    }
                }
            }
        }
    }
}