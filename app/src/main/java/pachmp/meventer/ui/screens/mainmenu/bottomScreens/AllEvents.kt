package pachmp.meventer.ui.screens.mainmenu.bottomScreens

import android.annotation.SuppressLint
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pachmp.meventer.R
import pachmp.meventer.repository.Events
import pachmp.meventer.ui.Background

@Composable
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Preview
fun AllEventsScreen() {
    Background()

    Scaffold(
        backgroundColor = Color.Transparent,
        topBar = {
            Column {
                SearchBarWidget()
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(contentAlignment = Alignment.BottomCenter, modifier = Modifier
                        .padding(start = 14.dp, top = 4.dp)
                        .clickable {}) {
                        Icon(
                            imageVector = Icons.Default.FilterAlt,
                            contentDescription = "AllFilters",
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    FilterChipExample()
                }

            }

        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            itemsIndexed(getEvents()) { index, item ->
                EventCard(item.name, item.start, item.price, item.like)
            }
        }
    }
}

//Events
fun getEvents(): List<Events> {
    val eventsList = arrayListOf<Events>().apply {
        add(
            Events(
                organizers = arrayOf(1341U),
                name = "Битва хоров в лицее",
                start = 18114107UL,
                end = 18404107UL,
                description = "Битва хоров - формат музыкального соревнования, в котором состязаются хоровые коллективы",
                picture = "https://cdn-icons-png.flaticon.com/128/149/149452.png",
                price = 42u,
                like = false
            )
        )
        add(
            Events(
                organizers = arrayOf(1341U),
                name = "Битва хоров в лицее",
                start = 18404107UL,
                end = 18404107UL,
                description = "Битва хоров - формат музыкального соревнования, в котором состязаются хоровые коллективы",
                picture = "https://cdn-icons-png.flaticon.com/128/149/149452.png",
                price = 0u,
                like = true
            )
        )
        add(
            Events(
                organizers = arrayOf(1341U),
                name = "Битва хоров в лицее",
                start = 18404107UL,
                end = 18404107UL,
                description = "Битва хоров - формат музыкального соревнования, в котором состязаются хоровые коллективы",
                picture = "https://cdn-icons-png.flaticon.com/128/149/149452.png",
                price = 1234u,
                like = false
            )
        )
        add(
            Events(
                organizers = arrayOf(1341U),
                name = "Битва хоров в лицее",
                start = 18404107UL,
                end = 18404107UL,
                description = "Битва хоров - формат музыкального соревнования, в котором состязаются хоровые коллективы",
                picture = "https://cdn-icons-png.flaticon.com/128/149/149452.png",
                price = 12u,
                like = true
            )
        )

    }

    return eventsList
}

@Composable
fun EventCard(title: String, start: ULong, price: UInt, like: Boolean) {
    var favorite by remember { mutableStateOf(like) }
    Card(
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth()
            .heightIn(min = 300.dp, max = 400.dp)
        /*.clickable(onClick = EventCardNavigation)*/,
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height((400 * 0.6f).dp)
        ) {
            Image(
                modifier = Modifier.fillMaxSize(),
                painter = painterResource(id = R.drawable.logo),
                contentDescription = null,
                contentScale = ContentScale.Crop
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.padding(top = 6.dp, bottom = 10.dp),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(text = title, fontSize = 20.sp, fontWeight = FontWeight(1000), maxLines = 2)
                Text(text = "$start", maxLines = 1)
                Text(text = if (price != 0u) "Price: $price" else "Price: Free", maxLines = 1)
            }

            Image(
                painter = if (favorite) painterResource(id = R.drawable.fullheart2) else painterResource(
                    id = R.drawable.emptyheart2
                ),
                contentDescription = "Favorite",
                modifier = Modifier
                    .size(40.dp)
                    .clickable {
                        favorite = !favorite
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

        Divider()
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
        if (isSelected) MaterialTheme.colors.primary else MaterialTheme.colors.primary.copy(alpha = 0.2f)
    val contentColor =
        if (isSelected) MaterialTheme.colors.onPrimary else MaterialTheme.colors.primary

    Surface(
        modifier = modifier.padding(4.dp),
        shape = CircleShape,
        color = backgroundColor,
        onClick = { onSelectedChange(!isSelected) }
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.body2,
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
    val appliedFilters = listOf("Today", "Free", "Sport", "Learning", "Favorite", "Games")

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