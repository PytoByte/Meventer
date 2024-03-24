package pachmp.meventer.components.mainmenu.components.events.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.ramcosta.composedestinations.annotation.Destination
import pachmp.meventer.R
import pachmp.meventer.components.mainmenu.components.events.EventsViewModel
import pachmp.meventer.components.widgets.LoadingScreen
import pachmp.meventer.data.DTO.Event
import pachmp.meventer.ui.transitions.BottomTransition
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@EventsNavGraph(start = true)
@Destination(style = BottomTransition::class)
@Composable
fun AllEventsScreen(eventsViewModel: EventsViewModel) {
    var isSearchActive by rememberSaveable { mutableStateOf(false) }
    val showDialog = remember { mutableStateOf(false) }

    FilterDialog(showDialog) {
        eventsViewModel.eventSelection = it
    }

    Scaffold(
        snackbarHost = { SnackbarHost(eventsViewModel.snackBarHostState) },
        floatingActionButton = {
            FloatingButtonAdd {
                eventsViewModel.navigateToCreateEvent()
            }
        },
        topBar = {
            Column {
                EmbeddedSearchBar(
                    isSearchActive = isSearchActive,
                    onActiveChanged = { isSearchActive = it },
                    onSearch = {
                        isSearchActive = false
                        eventsViewModel.searchEvents()
                    },
                    onQueryChange = { eventsViewModel.query = it }
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
                    FilterChipExample(
                        onAdd = {
                            when(it) {
                                "Нравится" -> eventsViewModel.favoriteFilter = true
                                "Создатель" -> eventsViewModel.originatorFilter = true
                                "Организатор" -> eventsViewModel.organizerFilter = true
                                "Участник" -> eventsViewModel.participantFilter = true
                            }
                            eventsViewModel.filterByFastTags()
                        },
                        onRemove = {
                            when(it) {
                                "Нравится" -> eventsViewModel.favoriteFilter = false
                                "Создатель" -> eventsViewModel.originatorFilter = false
                                "Организатор" -> eventsViewModel.organizerFilter = false
                                "Участник" -> eventsViewModel.participantFilter = false
                            }
                            eventsViewModel.filterByFastTags()
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        if (eventsViewModel.eventsVisible == null || eventsViewModel.user == null) {
            LoadingScreen(Modifier.fillMaxSize())
        } else if (eventsViewModel.eventsVisible!!.size == 0) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Ваш список мероприятий пуст")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues), horizontalAlignment = Alignment.CenterHorizontally
            ) {
                items(eventsViewModel.eventsVisible!!, key = { it.id }, contentType = { Event }) { event ->
                    EventCard(event, eventsViewModel)
                }
                item {
                    Spacer(modifier = Modifier.size(65.dp))
                }
            }
        }
    }
}

@Composable
fun FloatingButtonAdd(onClick: () -> Unit) {
    FloatingActionButton(
        onClick = { onClick() },
        modifier = Modifier
            .size(60.dp)
    ) {
        Icon(Icons.Filled.Add, "Floating action button.")
    }
}

//Events
@Composable
fun EventCard(event: Event, eventsViewModel: EventsViewModel) {
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
            AsyncImage(
                modifier = Modifier.fillMaxSize(),
                model = if (event.images.isEmpty()) null else event.images[0],
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
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd | hh:mm:ss")), maxLines = 1
                )
                Text(
                    text = if (event.price != 0) "Price: ${event.price}₽" else "Price: Free",
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

//Searh
//Searh
@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun EmbeddedSearchBar(
    isSearchActive: Boolean,
    onActiveChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    onSearch: ((String) -> Unit)? = null,
    onQueryChange: (String) -> Unit,
) {
    var searchQuery by rememberSaveable { mutableStateOf("") }
    val searchHistory = remember { mutableStateListOf("") }
    val activeChanged: (Boolean) -> Unit = { active ->
        onActiveChanged(active)
    }
    SearchBar(
        query = searchQuery,
        onQueryChange = { query ->
            onQueryChange(query)
            searchQuery = query
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
                .padding(start = 12.dp, top = 2.dp, end = 12.dp)
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
                Row(
                    modifier = Modifier
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
            fontWeight = Bold,
            text = "отчистить историю"
        )
        // Search suggestions or results
    }
}

//Filter
@Composable
fun FilterChip(
    text: String,
    isSelected: Boolean,
    onSelectedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val backgroundColor =
        if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary.copy(
            alpha = 0.2f
        )
    val contentColor =
        if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary

    Surface(
        modifier = modifier.padding(4.dp),
        shape = CircleShape,
        color = backgroundColor,
        onClick = { onSelectedChange(!isSelected) }
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = contentColor,
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)
        )
    }
}

@Composable
fun FiltersRow(
    allFilters: List<String>,
    selectedFilters: MutableState<List<String>>,
    onFilterSelected: (String) -> Unit,
    onFilterDeselected: (String) -> Unit,
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 4.dp)
    ) {
        items(allFilters.size) { index ->
            val filter = allFilters[index]
            val isSelected = selectedFilters.value.contains(filter)
            FilterChip(
                text = filter,
                isSelected = isSelected,
                onSelectedChange = { isSelected ->
                    if (isSelected) {
                        onFilterSelected(filter)
                    } else {
                        onFilterDeselected(filter)
                    }
                }
            )
        }
    }
}

@Composable
fun FilterChipExample(
    onAdd: (String) -> Unit,
    onRemove: (String) -> Unit,
) {
    var selectedFilters by remember { mutableStateOf(emptyList<String>()) }
    val allFilters = remember {listOf("Нравится", "Создатель", "Организатор", "Участник")}

    Column(modifier = Modifier.padding(start = 10.dp, end = 10.dp, top = 6.dp)) {
        val filteredSelectedFilters = allFilters.filter { it in selectedFilters }
        val filteredUnselectedFilters = allFilters.filter { it !in selectedFilters }
        val mergedFilters = filteredSelectedFilters + filteredUnselectedFilters
        FiltersRow(
            allFilters = mergedFilters,
            selectedFilters = remember{mutableStateOf(selectedFilters)},
            onFilterSelected = { filter ->
                selectedFilters = selectedFilters.toMutableList().apply {
                    add(filter)
                    onAdd(filter)
                }
            },
            onFilterDeselected = { filter ->
                selectedFilters = selectedFilters.toMutableList().apply {
                    remove(filter)
                    onRemove(filter)
                }
            }
        )
    }
}