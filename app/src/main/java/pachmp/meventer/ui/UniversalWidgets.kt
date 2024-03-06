package pachmp.meventer.ui

import android.widget.CalendarView
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import com.kizitonwose.calendar.compose.CalendarState
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import com.kizitonwose.calendar.core.nextMonth
import com.kizitonwose.calendar.core.previousMonth
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.spec.DestinationStyle
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import pachmp.meventer.R
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Month
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale
import kotlin.concurrent.thread

@Composable
fun MessageDialog(visible: Boolean, messageModel: MessageModel, onDismissRequest: () -> Unit) {
    if (visible) {
        Dialog(onDismissRequest = onDismissRequest) {
            Surface(modifier = Modifier.clip(RoundedCornerShape(10.dp))) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(messageModel.title, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.size(5.dp))
                    Text(messageModel.description, fontSize = 15.sp)
                    Spacer(modifier = Modifier.size(10.dp))
                    Button(onClick = onDismissRequest) {
                        Text("ОК")
                    }
                }
            }
        }
    }
}


@Composable
fun DaysOfWeekTitle(daysOfWeek: List<DayOfWeek>) {
    Row(modifier = Modifier.fillMaxWidth()) {
        for (dayOfWeek in daysOfWeek) {
            Text(
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                text = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
            )
        }
    }
}

@Composable
fun dialogDatePicker(
    visible: MutableState<Boolean>,
    currentSelected: CalendarDay = CalendarDay(
        date = LocalDate.now(),
        position = DayPosition.InDate
    ),
    onDatePick: (CalendarDay) -> Unit
) {
    val currentMonth = remember { YearMonth.now() }
    val firstVisibleMonth = remember { YearMonth.now() }
    val startMonth = remember { currentMonth.minusYears(200) }
    val endMonth = remember { currentMonth }
    val selected = remember { mutableStateOf(currentSelected) }
    val daysOfWeek = remember { firstDayOfWeekFromLocale() }
    val state = rememberCalendarState(
        startMonth = startMonth,
        endMonth = endMonth,
        firstVisibleMonth = firstVisibleMonth,
        firstDayOfWeek = daysOfWeek
    )
    if (visible.value) {
        Dialog(onDismissRequest = { visible.value = false }) {
            Surface(modifier = Modifier.clip(RoundedCornerShape(10.dp))) {
                Column(
                    modifier = Modifier
                        .background(Color.White)
                        .padding(10.dp),
                ) {
                    val coroutineScope = rememberCoroutineScope()
                    SimpleCalendarYearTitle(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        calendarState = state,
                        goToPrevious = {
                            coroutineScope.launch {
                                state.scrollToMonth(state.firstVisibleMonth.yearMonth.minusYears(1))
                            }
                        },
                        goToNext = {
                            coroutineScope.launch {
                                state.scrollToMonth(state.firstVisibleMonth.yearMonth.plusYears(1))
                            }
                        },
                    )
                    SimpleCalendarMonthTitle(
                        modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp),
                        calendarState = state,
                        goToPrevious = {
                            coroutineScope.launch {
                                state.animateScrollToMonth(state.firstVisibleMonth.yearMonth.previousMonth)
                            }
                        },
                        goToNext = {
                            coroutineScope.launch {
                                state.animateScrollToMonth(state.firstVisibleMonth.yearMonth.nextMonth)
                            }
                        },
                    )
                    HorizontalCalendar(
                        state = state,
                        dayContent = { day ->
                            Day(day, isSelected = selected.value.date == day.date) { clicked ->
                                selected.value = clicked
                            }
                        },
                        monthHeader = { month ->
                            DaysOfWeekTitle(
                                daysOfWeek = month.weekDays.first().map { it.date.dayOfWeek })
                        },
                    )

                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                        Button(onClick = { onDatePick(selected.value); visible.value = false }) {
                            Text("Выбрать")
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun CalendarNavigationIcon(
    icon: Painter,
    contentDescription: String,
    onClick: () -> Unit,
) = Box(
    modifier = Modifier
        .fillMaxHeight()
        .aspectRatio(1f)
        .clip(shape = CircleShape)
        .combinedClickable(
            onClick = { onClick() },
            onLongClick = { repeat(10) { onClick() } },
        )
) {
    Icon(
        modifier = Modifier
            .fillMaxSize()
            .padding(4.dp)
            .align(Alignment.Center),
        painter = icon,
        contentDescription = contentDescription,
    )
}

@Composable
fun SimpleCalendarYearTitle(
    modifier: Modifier,
    calendarState: CalendarState,
    goToPrevious: () -> Unit,
    goToNext: () -> Unit,
) {
    Row(
        modifier = modifier.height(40.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CalendarNavigationIcon(
            icon = painterResource(id = R.drawable.ic_chevron_left),
            contentDescription = "Previous",
            onClick = goToPrevious,
        )
        Text(
            modifier = Modifier
                .weight(1f),
            text = calendarState.firstVisibleMonth.yearMonth.year.toString(),
            fontSize = 30.sp,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Medium,
        )
        CalendarNavigationIcon(
            icon = painterResource(id = R.drawable.ic_chevron_right),
            contentDescription = "Next",
            onClick = goToNext,
        )
    }
}

@Composable
fun SimpleCalendarMonthTitle(
    modifier: Modifier,
    calendarState: CalendarState,
    goToPrevious: () -> Unit,
    goToNext: () -> Unit,
) {
    Row(
        modifier = modifier.height(40.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CalendarNavigationIcon(
            icon = painterResource(id = R.drawable.ic_chevron_left),
            contentDescription = "Previous",
            onClick = goToPrevious,
        )
        Text(
            modifier = Modifier
                .weight(1f),
            text = calendarState.firstVisibleMonth.yearMonth.month.getDisplayName(
                TextStyle.SHORT,
                Locale.getDefault()
            ),
            fontSize = 22.sp,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Medium,
        )
        CalendarNavigationIcon(
            icon = painterResource(id = R.drawable.ic_chevron_right),
            contentDescription = "Next",
            onClick = goToNext,
        )
    }
}

@Composable
private fun Day(day: CalendarDay, isSelected: Boolean, onClick: (CalendarDay) -> Unit) {
    Box(
        modifier = Modifier
            .aspectRatio(1f) // This is important for square-sizing!
            .padding(6.dp)
            .clip(CircleShape)
            .background(color = if (isSelected) Color.Blue else Color.Transparent)
            // Disable clicks on inDates/outDates
            .clickable(
                enabled = day.position == DayPosition.MonthDate,
                onClick = { onClick(day) },
            ),
        contentAlignment = Alignment.Center,
    ) {
        val textColor = when (day.position) {
            // Color.Unspecified will use the default text color from the current theme
            DayPosition.MonthDate -> if (isSelected) Color.White else Color.Unspecified
            DayPosition.InDate, DayPosition.OutDate -> Color.Gray
        }
        Text(
            text = day.date.dayOfMonth.toString(),
            color = textColor,
            fontSize = 14.sp,
        )
    }
}