package pachmp.meventer.components.mainmenu.components.events.screens

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ramcosta.composedestinations.annotation.Destination
import pachmp.meventer.R
import pachmp.meventer.components.mainmenu.components.events.EventsViewModel
import pachmp.meventer.components.widgets.Background
import pachmp.meventer.components.widgets.HeadingTextComponent
import pachmp.meventer.components.widgets.MaskVisualTransformation
import pachmp.meventer.components.widgets.MaterialButton
import pachmp.meventer.components.widgets.TextComponent
import pachmp.meventer.components.widgets.dropDownMenu
import pachmp.meventer.ui.transitions.FadeTransition

@EventsNavGraph
@Destination(style = FadeTransition::class)
@Composable
fun EditEventScreen(eventsViewModel: EventsViewModel) {
    Background()

    Card(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp), colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.2f),
            contentAlignment = Alignment.BottomEnd

        ) {
            Image(
                modifier = Modifier.fillMaxSize(),
                painter = painterResource(id = R.drawable.defaultimage),
                contentDescription = null,
                contentScale = ContentScale.Crop
            )

            IconButton(
                onClick = { },
                modifier = Modifier.padding(start = 3.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "EditPicture",
                    modifier = Modifier.size(32.dp)
                )
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
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { },
                        modifier = Modifier.padding(start = 3.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBackIosNew,
                            contentDescription = "Back",
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    MaterialButton(
                        modifier = Modifier
                            .padding(start = 5.dp, end = 5.dp)
                            .fillMaxWidth()
                            .heightIn(40.dp)
                            .height(50.dp), text = "Сохранить",
                        onClick = { }
                    )
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                HeadingTextComponent(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(81.dp)
                        .padding(top = 10.dp, start = 15.dp, end = 15.dp),
                    labelValue = "Заголовок",
                    maxLenght = 50
                )

                TextComponent(
                    labelValue = "Дата начала",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(81.dp)
                        .padding(top = 10.dp, start = 15.dp, end = 15.dp),
                    mask = MaskVisualTransformation("##/##/#### ##:##"),
                    maxLenght = 12,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                Text(text = "Возрастное ограничение:", style = MaterialTheme.typography.titleLarge, modifier = Modifier
                    .padding(start = 15.dp, end = 15.dp, top = 10.dp)
                    .fillMaxWidth(), textAlign = TextAlign.Start)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 15.dp, end = 15.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    dropDownMenu("От")
                    dropDownMenu("До")
                }

                TextComponent(
                    labelValue = "Стоимость мероприятия",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(81.dp)
                        .padding(start = 15.dp, end = 15.dp, top = 10.dp),
                    maxLenght = 100,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                TextComponent(
                    labelValue = "Описание",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .padding(start = 15.dp, end = 15.dp, top = 10.dp),
                    textFieldModifier = Modifier.height(300.dp),
                    maxLenght = 500
                )

            }
        }
    }
}