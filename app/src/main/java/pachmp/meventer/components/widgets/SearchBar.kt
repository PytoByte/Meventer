package pachmp.meventer.components.widgets

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmbeddedSearchBar(
    modifier: Modifier = Modifier,
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: (Boolean) -> Unit,
    onClearSearch: () -> Unit = {},
    globalFlag: Boolean = false
) {
    var isSearchActive by rememberSaveable { mutableStateOf(false) }
    val searchHistory = remember { mutableStateListOf("") }
    val containerColor =
        if (isSearchActive) MaterialTheme.colorScheme.background
        else MaterialTheme.colorScheme.surfaceContainerLow
    var globalSearch by rememberSaveable { mutableStateOf(false) }
    var searchState by remember { mutableStateOf(false) }

    SearchBar(
        query = query,
        onQueryChange = onQueryChange,
        onSearch = {onSearch(globalSearch); searchHistory.add(it); isSearchActive=false; searchState=true},
        active = isSearchActive,
        onActiveChange = { isSearchActive = it },
        modifier = if (isSearchActive) {
            modifier
                .fillMaxWidth()
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
                IconButton(onClick = { isSearchActive = false } ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = "back",
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
            } else {
                Icon(
                    imageVector = Icons.Rounded.Search,
                    contentDescription = "search",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        },
        trailingIcon = {
            if (query.isNotEmpty() || searchState) {
                IconButton(onClick = { onQueryChange(""); onClearSearch(); searchState=false }) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = "clearField",
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        },
        colors = SearchBarDefaults.colors(containerColor = containerColor),
        tonalElevation = 0.dp,
        windowInsets =
        if (isSearchActive) SearchBarDefaults.windowInsets
        else WindowInsets(0.dp)
    ) {
        if (globalFlag) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = globalSearch, onCheckedChange = {globalSearch=!globalSearch})
                Text("Глобальный поиск")
            }
        }
        searchHistory.forEach {
            if (it.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .padding(all = 14.dp)
                        .fillMaxWidth()
                        .clickable { onQueryChange("") },
                    horizontalArrangement = Arrangement.Start
                ) {
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
            text = "Очистить историю"
        )

    }
}