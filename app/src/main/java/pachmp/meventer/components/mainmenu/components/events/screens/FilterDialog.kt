package pachmp.meventer.components.mainmenu.components.events.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import pachmp.meventer.R
import pachmp.meventer.data.DTO.EventSelection

@OptIn(
    ExperimentalLayoutApi::class, ExperimentalAnimationApi::class,
    ExperimentalMaterial3Api::class
)
@Composable
fun FilterDialog(
    visible: MutableState<Boolean>,
    onFilterApply: (EventSelection) -> Unit = {},
) {
    var isCategories by remember { mutableStateOf(false) }
    val categories = stringArrayResource(id = R.array.event_tags)

    var isPaid by remember { mutableStateOf(false) }
    var minPrice by remember { mutableStateOf("") }
    var maxPrice by remember { mutableStateOf("") }

    var isAge by remember { mutableStateOf(false) }
    var minAge by remember { mutableStateOf("") }

    var showAllCategories by remember { mutableStateOf(false) }
    var selectedCategories by remember { mutableStateOf(setOf<String>()) }

    var isSort by remember { mutableStateOf(false) }
    var dropdownMenuOpen by remember { mutableStateOf(false) }
    var sort by remember { mutableStateOf(EventSelection.SortingState.NEAREST_ONES_FIRST) }

    if (visible.value) {
        Dialog(
            onDismissRequest = {
                selectedCategories = setOf()
                showAllCategories = false
                isPaid = false
                isAge = false
                minAge = ""
                minPrice = ""
                maxPrice = ""
                sort = EventSelection.SortingState.NEAREST_ONES_FIRST
                visible.value = false
            }
        ) {
            Surface(
                modifier = Modifier
                    .width(340.dp)
                    .height(440.dp)
                    .clip(RoundedCornerShape(10.dp))
            ) {
                Scaffold(
                    bottomBar = {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(onClick = {
                                onFilterApply(
                                    EventSelection(
                                        tags = selectedCategories.toList(),
                                        age = minAge.toShortOrNull(),
                                        minimalPrice = minPrice.toIntOrNull(),
                                        maximalPrice = maxPrice.toIntOrNull(),
                                        sortBy = sort.state
                                    )
                                )
                                visible.value = false
                            }) {
                                Text(stringResource(R.string.drop))
                            }
                            Button(onClick = {
                                onFilterApply(
                                    EventSelection(
                                        tags = selectedCategories.toList(),
                                        age = minAge.toShortOrNull(),
                                        minimalPrice = minPrice.toIntOrNull(),
                                        maximalPrice = maxPrice.toIntOrNull(),
                                        sortBy = sort.state
                                    )
                                )
                                visible.value = false
                            }) {
                                Text(stringResource(R.string.apply))
                            }
                        }
                    }
                ) { paddingValues ->
                    Box(modifier = Modifier.padding(paddingValues)) {
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(text = stringResource(R.string.filters), fontSize = 24.sp, fontWeight = FontWeight.Bold)
                            // Параметр "Категории"

                            Row(
                                modifier = Modifier.clickable { isCategories = !isCategories },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = stringResource(R.string.categories), fontSize = 24.sp)
                                Icon(
                                    modifier = Modifier.size(30.dp),
                                    imageVector = if (isCategories) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                                    contentDescription = "dropdown"
                                )
                            }

                            AnimatedVisibility(visible = isCategories) {
                                Column {
                                    FlowRow(
                                        Modifier
                                            .wrapContentHeight()
                                            .fillMaxWidth(1f),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        verticalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        categories.forEach { category ->
                                            OutlinedButton(
                                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
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
                                                        alpha = 0f
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
                            }

                            Row(
                                modifier = Modifier.clickable { isAge = !isAge },
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(text = stringResource(R.string.age), fontSize = 24.sp)
                                Icon(
                                    modifier = Modifier.size(30.dp),
                                    imageVector = if (isAge) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                                    contentDescription = "dropdown"
                                )
                            }

                            AnimatedVisibility(visible = isAge) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                )
                                {
                                    OutlinedTextField(
                                        value = minAge,
                                        onValueChange = { minAge = it },
                                        label = { Text(stringResource(R.string.min)) },
                                        modifier = Modifier.width(134.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(10.dp))

                            }

                            Row(
                                modifier = Modifier.clickable { isPaid = !isPaid },
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(text = stringResource(R.string.price), fontSize = 24.sp)
                                Icon(
                                    modifier = Modifier.size(30.dp),
                                    imageVector = if (isPaid) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                                    contentDescription = "dropdown"
                                )
                            }

                            AnimatedVisibility(visible = isPaid) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                )
                                {
                                    OutlinedTextField(
                                        value = minPrice,
                                        onValueChange = { minPrice = it },
                                        label = { Text(stringResource(R.string.min)) },
                                        modifier = Modifier.width(134.dp)
                                    )

                                    OutlinedTextField(
                                        value = maxPrice,
                                        onValueChange = { maxPrice = it },
                                        label = { Text(stringResource(R.string.max)) },
                                        modifier = Modifier.width(134.dp)
                                    )
                                }
                            }

                            Row(
                                modifier = Modifier.clickable { isSort = !isSort },
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(text = stringResource(R.string.sort), fontSize = 24.sp)
                                Icon(
                                    modifier = Modifier.size(30.dp),
                                    imageVector = if (isSort) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                                    contentDescription = "dropdown"
                                )
                            }

                            AnimatedVisibility(visible = isSort) {
                                ExposedDropdownMenuBox(
                                    expanded = dropdownMenuOpen,
                                    onExpandedChange = { dropdownMenuOpen = it }) {
                                    OutlinedTextField(
                                        modifier = Modifier.menuAnchor(),
                                        value = stringResource(sort.nameResourceID),
                                        onValueChange = {},
                                        readOnly = true,
                                        trailingIcon = {
                                            ExposedDropdownMenuDefaults.TrailingIcon(
                                                expanded = dropdownMenuOpen
                                            )
                                        },
                                        singleLine = true,
                                        label = { Text(stringResource(R.string.by_start_date)) }
                                    )

                                    ExposedDropdownMenu(
                                        expanded = dropdownMenuOpen,
                                        onDismissRequest = { dropdownMenuOpen = false }) {
                                        EventSelection.SortingState.values().forEach {
                                            DropdownMenuItem(
                                                text = { Text(text = stringResource(it.nameResourceID)) },
                                                onClick = { sort = it; dropdownMenuOpen = false },
                                                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}