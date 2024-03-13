package pachmp.meventer.components.mainmenu.components.events.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.ramcosta.composedestinations.annotation.Destination
import pachmp.meventer.R
import pachmp.meventer.components.mainmenu.components.events.EventsViewModel
import pachmp.meventer.components.widgets.Background
import pachmp.meventer.data.DTO.Event
import pachmp.meventer.ui.transitions.BottomTransition
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@EventsNavGraph(start = true)
@Destination(style = BottomTransition::class)
@Composable
fun AllEventsScreen(eventsViewModel: EventsViewModel) {
    Log.d("VIEWMODEL", eventsViewModel.toString())
    Scaffold(
        topBar = {
            Column {
                SearchBarWidget()
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { /*TODO*/ }) {
                        Box(contentAlignment = Alignment.BottomCenter, modifier = Modifier
                            .padding(start = 14.dp, top = 4.dp)) {
                            Icon(
                                imageVector = Icons.Default.FilterAlt,
                                contentDescription = "AllFilters",
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                    FilterChipExample()
                }

            }

        }
    ) { paddingValues ->
        Background()
        if (eventsViewModel.events==null) {

        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues), horizontalAlignment = Alignment.CenterHorizontally
            ) {
                items(eventsViewModel.events!!, key={it.id}, contentType = { Event }) { event ->
                    EventCard(event, eventsViewModel)
                }
            }
        }
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
                model = event.images[0],
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
                Text(text = event.name, fontSize = 20.sp, fontWeight = FontWeight(1000), maxLines = 2)
                Text(text = event.startTime.atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd | hh:mm:ss")), maxLines = 1)
                Text(text = if (event.price != 0) "Price: ${event.price}₽" else "Price: Free", maxLines = 1)
            }

            Image(
                // TODO: FAVORITES ARE GONE
                painter = if (false) painterResource(id = R.drawable.fullheart2) else painterResource(
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
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBarWidget() {
    var text by remember { mutableStateOf("") } // Query for SearchBar
    var active by remember { mutableStateOf(false) } // Active state for SearchBar
    val searchHistory = remember { mutableStateListOf("") }

    SearchBar(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 10.dp),
        query = text,
        onQueryChange = {
            text = it
        },
        onSearch = {
            searchHistory.add(text)
            active = false
        },
        active = active,
        onActiveChange = {
            active = it
        },
        placeholder = {
            Text(text = "Enter your query")
        },
        leadingIcon = {
            Icon(imageVector = Icons.Default.Search, contentDescription = "Search icon")
        },
        trailingIcon = {
            if (active) {
                Icon(
                    modifier = Modifier.clickable {
                        if (text.isNotEmpty()) {
                            text = ""
                        } else {
                            active = false
                        }
                    },
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close icon"
                )
            }
        }
    ) {
        searchHistory.forEach {
            if (it.isNotEmpty()) {
                Row(modifier = Modifier.padding(all = 14.dp)) {
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
            fontWeight = FontWeight.Bold,
            text = "clear all history"
        )
    }
}

//Filter
@Composable
fun FilterChip(
    text: String,
    isSelected: Boolean,
    onSelectedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor =
        if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
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
    onFilterDeselected: (String) -> Unit
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
fun FilterChipExample() {
    val appliedFilters = listOf("Создатель", "Организатор", "Участник")

    var selectedFilters by remember { mutableStateOf(emptyList<String>()) }
    val allFilters = appliedFilters

    Column(modifier = Modifier.padding(start = 10.dp, end = 10.dp, top = 6.dp)) {
        val filteredSelectedFilters = allFilters.filter { it in selectedFilters }
        val filteredUnselectedFilters = allFilters.filter { it !in selectedFilters }
        val mergedFilters = filteredSelectedFilters + filteredUnselectedFilters
        FiltersRow(
            allFilters = mergedFilters,
            selectedFilters = mutableStateOf(selectedFilters),
            onFilterSelected = { filter ->
                selectedFilters = selectedFilters.toMutableList().apply { add(filter) }
            },
            onFilterDeselected = { filter ->
                selectedFilters = selectedFilters.toMutableList().apply { remove(filter) }
            }
        )
    }
}