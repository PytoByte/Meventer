package pachmp.meventer.ui.screens.mainmenu.bottomScreens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pachmp.meventer.ui.Background
import pachmp.meventer.ui.MaterialButton

@Composable
@Preview
fun YourEventsScreen() {
    Background()
    Scaffold(
        backgroundColor = Color.Transparent,
        bottomBar = {
            Box(contentAlignment = Alignment.Center) {
                MaterialButton(text = "Создать мероприятие", modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth(),
                    onClick = {})

            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(10) { index ->
                EventCard("bonyk", 12342006u, 203u, true)
            }
        }
    }
}