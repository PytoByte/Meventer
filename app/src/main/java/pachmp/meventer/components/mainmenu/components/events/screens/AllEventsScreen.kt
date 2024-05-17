package pachmp.meventer.components.mainmenu.components.events.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ramcosta.composedestinations.annotation.Destination
import pachmp.meventer.R
import pachmp.meventer.components.mainmenu.components.events.EventsNavGraph
import pachmp.meventer.components.mainmenu.components.events.EventsViewModel
import pachmp.meventer.components.widgets.EmbeddedSearchBar
import pachmp.meventer.components.widgets.LoadingScreen
import pachmp.meventer.data.DTO.Event
import pachmp.meventer.data.enums.FastTags
import pachmp.meventer.ui.transitions.BottomTransition
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@EventsNavGraph(start = true)
@Destination(style = BottomTransition::class)
@Composable
fun AllEventsScreen(eventsViewModel: EventsViewModel) {
    val showDialog = remember { mutableStateOf(false) }

    val fastTags =
        FastTags.values().associate { Pair(it.nameResourceID, stringResource(it.nameResourceID)) }

    with(eventsViewModel) {
        FilterDialog(showDialog) {
            eventsViewModel.eventSelection = it
        }

        Scaffold(
            snackbarHost = { SnackbarHost(eventsViewModel.snackbarHostState) },
            floatingActionButton = {
                FloatingButtonAdd {
                    eventsViewModel.navigateToCreateEvent()
                }
            },
            topBar = {
                Column {
                    EmbeddedSearchBar(
                        query = eventsViewModel.query,
                        onQueryChange = { eventsViewModel.query = it },
                        onSearch = { eventsViewModel.searchEvents(it) },
                        globalFlag = true,
                        onClearSearch = { clearSearch() }
                    )
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { showDialog.value = true }) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.FilterAlt,
                                    contentDescription = "AllFilters",
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                        TopFilterRow(
                            filters = fastTags.values.toList(),
                            onUpdate = { filter, state ->
                                when (filter) {
                                    fastTags[R.string.fast_tag_like] -> eventsViewModel.favoriteFilter =
                                        state

                                    fastTags[R.string.fast_tag_creator] -> eventsViewModel.originatorFilter =
                                        state

                                    fastTags[R.string.fast_tag_organizer] -> eventsViewModel.organizerFilter =
                                        state

                                    fastTags[R.string.fast_tag_participant] -> eventsViewModel.participantFilter =
                                        state
                                }
                                eventsViewModel.filterByFastTags()
                            }
                        )
                    }
                }
            }
        ) { paddingValues ->
            if (eventsVisible == null || user == null) {
                LoadingScreen(Modifier.fillMaxSize())
            } else if (eventsVisible!!.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(stringResource(R.string.events_list_empty))
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues), horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    items(
                        eventsViewModel.eventsVisible!!,
                        key = { it.id },
                        contentType = { Event }) { event ->

                        EventCard(event, getImageFromName(if (event.images.isEmpty()) null else event.images[0]).value, eventsViewModel)
                    }
                    item {
                        Spacer(modifier = Modifier.size(65.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun FloatingButtonAdd(onClick: () -> Unit) {
    FloatingActionButton(
        onClick = onClick,
        modifier = Modifier
            .size(60.dp)
    ) {
        Icon(Icons.Filled.Add, "Floating action button.")
    }
}

//Events
@Composable
fun EventCard(event: Event, imageBitmap: ImageBitmap, eventsViewModel: EventsViewModel) {
    Card(
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth()
            .heightIn(min = 300.dp, max = 400.dp)
            .clickable {
                eventsViewModel.navigateToEvent(event)
            },
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height((400 * 0.6f).dp)
        ) {
            Image(
                modifier = Modifier.fillMaxSize(),
                bitmap = imageBitmap,
                contentDescription = "title",
                contentScale = ContentScale.Crop
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.padding(top = 6.dp, bottom = 10.dp),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(
                    text = event.name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight(1000),
                    maxLines = 2
                )
                Text(
                    text = event.startTime.atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd | hh:mm")), maxLines = 1
                )
                Text(
                    text =
                    if (event.price != 0)
                        stringResource(R.string.event_price, "${event.price}₽")
                    else
                        stringResource(R.string.event_price, stringResource(R.string.free)),
                    maxLines = 1
                )
            }

            Image(
                painter = if (eventsViewModel.user!!.id in event.inFavourites) painterResource(id = R.drawable.fullheart2) else painterResource(
                    id = R.drawable.emptyheart2
                ),
                contentDescription = "Favorite",
                modifier = Modifier
                    .size(40.dp)
                    .clickable {
                        eventsViewModel.changeLike(event)
                    }
            )
        }
    }
}

@Composable
fun TopFilterRow(filters: List<String>, onUpdate: (String, Boolean) -> Unit) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        items(filters) {
            var selected by rememberSaveable { mutableStateOf(false) }

            val background =
                if (selected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)

            val contentColor =
                if (selected) MaterialTheme.colorScheme.onPrimary
                else MaterialTheme.colorScheme.primary

            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = background,
                    contentColor = contentColor
                ),
                onClick = {
                    selected = !selected
                    onUpdate(it, selected)
                }) {
                Text(it)
            }
        }
    }
}