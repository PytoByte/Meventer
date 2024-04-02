package pachmp.meventer.components.mainmenu.components.events.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.animation.with
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import pachmp.meventer.data.DTO.EventSelection
import pachmp.meventer.data.categories

@OptIn(ExperimentalLayoutApi::class, ExperimentalAnimationApi::class)
@Composable
fun FilterDialog(
    visible: MutableState<Boolean>,
    onFilterApply: (EventSelection) -> Unit = {}
) {

    var isPaid by remember { mutableStateOf(false) }
    var minPrice by remember { mutableStateOf("") }
    var maxPrice by remember { mutableStateOf("") }

    var isAge by remember { mutableStateOf(false) }
    var minAge by remember { mutableStateOf("") }

    var showAllCategories by remember { mutableStateOf(false) }
    var selectedCategories by remember { mutableStateOf(setOf<String>()) }

    var sort by remember { mutableStateOf(false) }

    if (visible.value) {
        Dialog(
            onDismissRequest = {
                selectedCategories = setOf<String>()
                showAllCategories = false
                isPaid = false
                isAge = false
                minAge = ""
                minPrice = ""
                maxPrice = ""
                sort = false
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
                    topBar = {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Сбросить",
                                modifier = Modifier.clickable {
                                    selectedCategories = setOf<String>()
                                    showAllCategories = false
                                    isPaid = false
                                    isAge = false
                                    minAge = ""
                                    minPrice = ""
                                    maxPrice = ""
                                    sort = false
                                })
                            Text(text = "Применить",
                                modifier = Modifier.clickable {
                                    onFilterApply(EventSelection(
                                        tags = selectedCategories.toList(),
                                        age = minAge.toShortOrNull(),
                                        minimalPrice = minPrice.toIntOrNull(),
                                        maximalPrice = maxPrice.toIntOrNull(),
                                        sortBy = if (sort) EventSelection.SortingStates.NEAREST_ONES_FIRST.state else EventSelection.SortingStates.FURTHER_ONES_FIRST.state
                                    ))
                                    visible.value = false
                                })
                        }
                    }
                ) { paddingValues ->
                    Box(modifier = Modifier.padding(paddingValues)) {
                        Column(
                            modifier = Modifier
                                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(5.dp)
                        ) {


                            // Параметр "Категории"
                            Text(text = "Категории", fontSize = 24.sp)

                            FlowRow(
                                Modifier
                                    .wrapContentHeight()
                                    .fillMaxWidth(1f),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                val visibleCategories =
                                    if (showAllCategories) categories else categories.take(3)

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
                            Text(text = if (showAllCategories) "Скрыть" else "Показать все",
                                modifier = Modifier
                                    .clickable { showAllCategories = !showAllCategories })


                            // Параметр "Возраст"
                            Spacer(modifier = Modifier.height(5.dp))

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(text = "Возраст", fontSize = 24.sp)
                                Switch(
                                    checked = isAge,
                                    onCheckedChange = { isAge = it }
                                )
                            }
                            AnimatedVisibility(visible = isAge){
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                )
                                {
                                    OutlinedTextField(
                                        value = minAge,
                                        onValueChange = { minAge = it },
                                        label = { Text("Мин") },
                                        modifier = Modifier.width(134.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(10.dp))

                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(text = "Цена", fontSize = 24.sp)
                                Switch(
                                    checked = isPaid,
                                    onCheckedChange = { isPaid = it }
                                )
                            }
                            AnimatedVisibility(visible = isPaid){
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                )
                                {
                                    OutlinedTextField(
                                        value = minPrice,
                                        onValueChange = { minPrice = it },
                                        label = { Text("Мин") },
                                        modifier = Modifier.width(134.dp)
                                    )

                                    OutlinedTextField(
                                        value = maxPrice,
                                        onValueChange = { maxPrice = it },
                                        label = { Text("Макс") },
                                        modifier = Modifier.width(134.dp)
                                    )
                                }
                            }

                            //Сортировка
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(text = "Сортировка", fontSize = 24.sp)
                                Switch(
                                    checked = sort,
                                    onCheckedChange = { sort = it }
                                )
                            }
                            AnimatedContent(
                                targetState = if (sort) "ближайшие" else "далёкие",
                                transitionSpec = {
                                    if (sort) {
                                        slideInVertically(initialOffsetY = {height->-height}) togetherWith slideOutVertically(targetOffsetY = {height->height})
                                    } else {
                                        slideInVertically(initialOffsetY = {height->height}) togetherWith slideOutVertically(targetOffsetY = {height->-height})
                                    }
                                }, label = "sort state"
                            ) {
                                Text(it, fontSize = 24.sp)
                            }

                        }
                    }
                }
            }
        }
    }
}