package pachmp.meventer.components.widgets

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.BrushPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import pachmp.meventer.R
import pachmp.meventer.ui.theme.MainColor
import pachmp.meventer.ui.theme.TextBox
import kotlin.math.absoluteValue

class MaskVisualTransformation(private val mask: String) : VisualTransformation {

    private val specialSymbolsIndices = mask.indices.filter { mask[it] != '#' }

    override fun filter(text: AnnotatedString): TransformedText {
        var out = ""
        var maskIndex = 0
        text.forEach { char ->
            while (specialSymbolsIndices.contains(maskIndex)) {
                out += mask[maskIndex]
                maskIndex++
            }
            out += char
            maskIndex++
        }
        return TransformedText(AnnotatedString(out), offsetTranslator())
    }

    private fun offsetTranslator() = object : OffsetMapping {
        override fun originalToTransformed(offset: Int): Int {
            val offsetValue = offset.absoluteValue
            if (offsetValue == 0) return 0
            var numberOfHashtags = 0
            val masked = mask.takeWhile {
                if (it == '#') numberOfHashtags++
                numberOfHashtags < offsetValue
            }
            return masked.length + 1
        }

        override fun transformedToOriginal(offset: Int): Int {
            return mask.take(offset.absoluteValue).count { it == '#' }
        }
    }
}

@Composable
fun MaterialButton(modifier: Modifier = Modifier, text: String = "", onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = MainColor
        ),
        modifier = modifier
    ) {
        Text(text, textAlign = TextAlign.Center, fontSize = 20.sp)
    }
}



@Composable
fun Background() {
    Image(
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.FillBounds,
        painter = painterResource(id = R.drawable.white65),
        contentDescription = null
    )
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeadingTextComponent(
    modifier: Modifier = Modifier,
    textFieldModifier: Modifier = Modifier,
    textModifier: Modifier = Modifier,
    labelValue: String = "",
    maxLenght: Int = 0
) {
    val textValue = remember {
        mutableStateOf("")
    }
    Scaffold(
        modifier = modifier,
        bottomBar = {
            if (maxLenght != 0) {
                Text(
                    text = "${textValue.value.length} / $maxLenght",
                    textAlign = TextAlign.End,
                    style = MaterialTheme.typography.labelMedium,
                    modifier = textModifier
                        .height(15.dp)
                        .fillMaxWidth()
                        .padding(end = 10.dp)
                )
            }
        })
    { paddingValues ->
        TextField(
            modifier = textFieldModifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(5.dp))
                .padding(paddingValues)
                .border(1.dp, Color.LightGray, RoundedCornerShape(5.dp)),
            label = {
                Text(
                    text = labelValue,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            },
            colors = TextFieldDefaults.textFieldColors(
                focusedLabelColor = Color.Black,
                cursorColor = Color.Black,
                containerColor = TextBox,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            ),
            keyboardOptions = KeyboardOptions.Default,
            value = textValue.value,
            textStyle = androidx.compose.ui.text.TextStyle(
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            ),
            onValueChange = {
                if (maxLenght != 0) {
                    if (it.length <= maxLenght) {
                        textValue.value = it
                    }
                } else {
                    textValue.value = it
                }
            },
            singleLine = true
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextComponent(
    modifier: Modifier = Modifier,
    textFieldModifier: Modifier = Modifier,
    textModifier: Modifier = Modifier,
    labelValue: String = "",
    maxLenght: Int = 0,
    mask: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    singleLine: Boolean = false
) {

    val textValue = remember {
        mutableStateOf("")
    }

    Scaffold(
        modifier = modifier,
        bottomBar = {
            if (maxLenght != 0) {
                Text(
                    text = "${textValue.value.length} / $maxLenght",
                    textAlign = TextAlign.End,
                    style = MaterialTheme.typography.labelMedium,
                    modifier = textModifier
                        .height(15.dp)
                        .fillMaxWidth()
                        .padding(end = 10.dp)
                )
            }
        }
    ) { paddingValues ->
        TextField(
            modifier = textFieldModifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(5.dp))
                .padding(paddingValues)
                .border(1.dp, Color.LightGray, RoundedCornerShape(5.dp)),
            label = { Text(text = labelValue, fontSize = 18.sp) },
            colors = TextFieldDefaults.textFieldColors(
                focusedLabelColor = Color.Black,
                cursorColor = Color.Black,
                containerColor = TextBox,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            ),
            keyboardOptions = keyboardOptions,
            value = textValue.value,
            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 15.sp),
            onValueChange = {
                if (maxLenght != 0) {
                    if (it.length <= maxLenght) {
                        textValue.value = it
                    }
                } else {
                    textValue.value = it
                }

            },
            visualTransformation = mask,
            singleLine = singleLine
        )
    }
}

@Composable
fun dropDownMenu(leb: String) {

    var expanded by remember { mutableStateOf(false) }
    val ages = (0..100).toList()
    var selectedText by remember { mutableStateOf("Нет") }
    var textfieldSize by remember { mutableStateOf(Size.Zero) }

    val icon = if (expanded)
        Icons.Filled.KeyboardArrowUp
    else
        Icons.Filled.KeyboardArrowDown


    Column(Modifier.padding(bottom = 20.dp)) {

        OutlinedTextField(
            value = selectedText,
            onValueChange = { selectedText = it },
            readOnly = true,
            modifier = Modifier
                .width(160.dp)
                .onGloballyPositioned { coordinates ->
                    //This value is used to assign to the DropDown the same width
                    textfieldSize = coordinates.size.toSize()
                },
            label = { Text("$leb")},
            trailingIcon = {
                Icon(icon, "contentDescription",
                    Modifier.clickable { expanded = !expanded })
            }
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .width(with(LocalDensity.current) { textfieldSize.width.toDp() })
        ) {
            DropdownMenuItem(text = { Text("Нет") }, onClick = {
                selectedText = "Нет"
                expanded = false
            })
            ages.forEach {
                    age ->
                DropdownMenuItem(text = { Text("$age" + if((age%10 == 2 && age != 12) || (age%10 == 3 && age != 13) || (age%10 == 4 && age != 14)) " года" else if(age%10==1 && age!=11) " год" else " лет") }, onClick = {
                    selectedText = "$age"
                    expanded = false
                })

            }
        }
    }
}

@Composable
fun TextCom(key: String, value: String){
    Column() {
        OutlinedTextField(
            value = value,
            label = {Text(key, fontWeight = FontWeight.Bold, fontSize = 22.sp)},
            onValueChange = { /*profileViewModel.setUserName(it)*/ },
            singleLine = true
        )
    }
}