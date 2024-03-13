package pachmp.meventer.components.mainmenu.components.events.screens

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.PeopleAlt
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.ramcosta.composedestinations.annotation.Destination
import pachmp.meventer.components.mainmenu.components.events.EventsViewModel
import pachmp.meventer.components.widgets.Background
import pachmp.meventer.components.widgets.CustomText
import pachmp.meventer.components.widgets.MaterialButton
import pachmp.meventer.ui.transitions.FadeTransition
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalFoundationApi::class)
@EventsNavGraph
@Destination(style = FadeTransition::class)
@Composable
fun EventScreen(eventsViewModel: EventsViewModel) {
    Log.d("VIEWMODEL", eventsViewModel.toString())
    val selected = eventsViewModel.getSelected()!!
    println(selected)
    val pagerState = rememberPagerState {
        selected.images.size
    }

    Background()
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
                key = { selected.images[it] }
            ) {
                AsyncImage(modifier = Modifier.fillMaxSize(), model = selected.images[it], contentDescription = "image", contentScale = ContentScale.Crop)
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
                    MarqueeText(text = selected.name)
                    CustomText(
                        text = "**Начало: **" + selected.startTime.atZone(ZoneId.systemDefault())
                            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd | hh:mm:ss"))
                    )
                    CustomText(
                        text = if (selected.maximalAge != null) "**Возростное ограничение:** от ${selected.minimalAge} до ${selected.maximalAge} лет" else "**Возростное ограничение:** ${selected.minimalAge}+ лет"
                    )
                    CustomText(
                        text = "**Стоимость: **" + if (selected.price == 0) "Бесплатно" else "${selected.price}₽"
                    )
                    CustomText(
                        text = "**Описание: **" + selected.description
                    )
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
