package pachmp.meventer.components.mainmenu.components.events.components.eventScreen.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.basicMarquee
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddModerator
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.PeopleAlt
import androidx.compose.material.icons.filled.PersonRemove
import androidx.compose.material.icons.filled.RemoveModerator
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.ramcosta.composedestinations.annotation.Destination
import pachmp.meventer.components.mainmenu.components.events.EventsViewModel
import pachmp.meventer.components.mainmenu.components.events.screens.EventsNavGraph
import pachmp.meventer.components.mainmenu.components.events.components.eventScreen.EventScreenViewModel
import pachmp.meventer.components.widgets.CustomText
import pachmp.meventer.components.widgets.MaterialButton
import pachmp.meventer.data.DTO.Event
import pachmp.meventer.data.DTO.User
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
    eventScreenViewModel.init(eventsViewModel.selected!!, eventsViewModel.user!!)
    with(eventScreenViewModel) {
        if (ready == null) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Загрузка")
            }
        } else if (ready!!) {
            val pagerState = rememberPagerState {
                event!!.images.size
            }

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
                        key = { event!!.images[it] }
                    ) {
                        AsyncImage(
                            modifier = Modifier.fillMaxSize(),
                            model = event!!.images[it],
                            contentDescription = "image",
                            contentScale = ContentScale.Crop
                        )
                    }
                }
                Scaffold(
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
                                text = "Присоединиться",
                                onClick = { }
                            )
                            IconButton(
                                onClick = { /*TODO back to screen participants*/ },
                                modifier = Modifier
                                    .padding(end = 5.dp)
                                    .weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PeopleAlt,
                                    contentDescription = "Participants",
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                    }
                ) { paddingValues ->
                    Column(
                        modifier = Modifier
                            .padding(paddingValues)
                            .fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp, start = 15.dp, end = 15.dp)
                                .verticalScroll(rememberScrollState()),
                            horizontalAlignment = Alignment.Start,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            MarqueeText(text = event!!.name)
                            CustomText(
                                text = "**Начало: **" + event!!.startTime.atZone(ZoneId.systemDefault())
                                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd | hh:mm:ss"))
                            )
                            CustomText(
                                text = if (event!!.maximalAge != null) "**Возростное ограничение:** от ${event!!.minimalAge} до ${event!!.maximalAge} лет" else "**Возростное ограничение:** ${event!!.minimalAge}+ лет"
                            )
                            CustomText(
                                text = "**Стоимость: **" + if (event!!.price == 0) "Бесплатно" else "${event!!.price}₽"
                            )
                            CustomText(
                                text = "**Описание: **" + event!!.description
                            )
                            Spacer(modifier = Modifier.padding(5.dp))
                            Text(
                                text = "Участники мероприятия",
                                style = MaterialTheme.typography.titleMedium
                            )
                            LazyColumn(modifier = Modifier.heightIn(max = 200.dp)) {
                                item {
                                    UserItem(
                                        event = event!!,
                                        user = originator!!,
                                        appUser = appUser!!,
                                        userRank = Rank.ORIGINATOR,
                                        appUserRank = getUserRank(event!!, appUser!!)
                                    )
                                    HorizontalDivider()
                                }

                                items(organizers!!) {
                                    UserItem(
                                        event = event!!,
                                        user = originator!!,
                                        appUser = appUser!!,
                                        userRank = Rank.ORGANIZER,
                                        appUserRank = getUserRank(event!!, appUser!!)
                                    )
                                }
                                /*items(participants!!) {
                                    UserItem(event = event!!, user = it, appUser = appUser!!)
                                }*/
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
fun UserItem(event: Event, user: User?, appUser: User, userRank: Rank, appUserRank: Rank) {
    if (user == null) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(text = "Не удалось загрузить пользователя")
        }

    } else {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            AsyncImage(
                model = user.avatar, contentDescription = "avatar",
                Modifier
                    .clip(
                        CircleShape
                    )
                    .size(60.dp), contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(4.dp), verticalArrangement = Arrangement.Center) {
                if (user.id == appUser.id) Text("${user.name} (Вы)") else Text(user.name)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Text(text = userRank.title)
                    if (appUserRank.value == Rank.ORIGINATOR.value && user.id != appUser.id) {
                        IconButton(onClick = { /*TODO назначить организатором*/ }) {
                            Icon(
                                imageVector = if (user.id in event.organizers) Icons.Default.RemoveModerator else Icons.Default.AddModerator,
                                contentDescription = "organizer"
                            )
                        }
                    }
                    if (appUserRank.value > userRank.value) {
                        IconButton(onClick = { /*TODO кик человека*/ }) {
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

enum class Rank(val value: Byte, val title: String) {
    PARTICIPANT(0.toByte(), "Участник"),
    ORGANIZER(1.toByte(), "Организатор"),
    ORIGINATOR(2.toByte(), "Основатель")
}

fun getUserRank(event: Event, user: User) = when (user.id) {
    event.originator -> Rank.ORIGINATOR
    in event.organizers -> Rank.ORGANIZER
    else -> Rank.PARTICIPANT
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
