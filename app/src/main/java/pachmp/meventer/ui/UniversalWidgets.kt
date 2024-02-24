package pachmp.meventer.ui

import android.widget.CalendarView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog

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
fun showDatePicker(visible: MutableState<Boolean>, onDatePick: (String) -> Unit) {
    if (visible.value) {
        Dialog(onDismissRequest = { visible.value = false }) {
            Surface(modifier = Modifier.clip(RoundedCornerShape(10.dp))) {
                AndroidView(
                    { CalendarView(it) },
                    modifier = Modifier
                        .wrapContentWidth()
                        .padding(20.dp),
                    update = { views ->
                        views.setOnDateChangeListener { calendarView, year, month, dayOfMonth ->
                            onDatePick("%04d-%02d-%02d".format(year, month, dayOfMonth))
                            visible.value = false
                        }
                    }
                )
            }
        }
    }
}