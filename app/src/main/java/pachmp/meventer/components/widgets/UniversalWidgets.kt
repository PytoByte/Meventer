package pachmp.meventer.components.widgets

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp

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
    }
}