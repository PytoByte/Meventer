package pachmp.meventer.components.widgets

import android.widget.ProgressBar
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize

@Composable
fun MaterialButton(modifier: Modifier = Modifier, text: String = "", enabled: Boolean=true, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled
    ) {
        Text(text, textAlign = TextAlign.Center, fontSize = 20.sp)
    }
}

@Composable
fun CustomText(text: String, modifier: Modifier = Modifier) {
    var results: MatchResult? = Regex("(?<!\\*)\\*\\*(?!\\*).*?(?<!\\*)\\*\\*(?!\\*)").find(text)
    var finalText = text
    val boldIndexes = buildMap {
        while (results != null) {
            val keyword = results!!.value
            val indexOf = finalText.indexOf(keyword)
            val newKeyword = keyword.removeSurrounding("**")
            finalText = finalText.replace(keyword, newKeyword)
            set(indexOf, indexOf + newKeyword.length)
            results = results?.next()
        }
    }
    Text(
        modifier = modifier
            .fillMaxWidth(),
        fontSize = 18.sp,
        text = buildAnnotatedString {
            append(finalText)
            for ((key, value) in boldIndexes) {
                addStyle(
                    style = SpanStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp

                    ),
                    start = key,
                    end = value
                )
            }
        }
    )
}

@Composable
fun TextCom(label: String, value: String, keyboardOptions: KeyboardOptions = KeyboardOptions.Default, onValueChange: (String)->Unit){
    Column() {
        OutlinedTextField(
            value = value,
            label = {Text(label, fontWeight = FontWeight.Bold, fontSize = 22.sp)},
            onValueChange = {onValueChange(it)},
            singleLine = true,
            keyboardOptions = keyboardOptions
        )
    }
}

@Composable
fun LoadingScreen(modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
        //Text("Загрузка")
    }
}