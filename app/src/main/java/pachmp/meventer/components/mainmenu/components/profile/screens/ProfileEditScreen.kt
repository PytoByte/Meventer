package pachmp.meventer.components.mainmenu.components.profile.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ramcosta.composedestinations.annotation.Destination
import pachmp.meventer.R
import pachmp.meventer.components.mainmenu.components.profile.ProfileViewModel
import pachmp.meventer.components.widgets.Background
import pachmp.meventer.components.widgets.TextCom
import pachmp.meventer.ui.transitions.FadeTransition
import java.time.LocalDate

@ProfileNavGraph
@Destination(style = FadeTransition::class)
@Composable
fun ProfileEdit(profileViewModel: ProfileViewModel) {
    Background()
    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Назад",
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    color = Color.Blue,
                    fontSize = 18.sp,
                    modifier = Modifier.clickable { profileViewModel.navigateToProfile() }
                )
                Text(
                    text = "Готово",
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    color = Color.Blue,
                    fontSize = 18.sp,
                    modifier = Modifier.clickable {  }
                )
            }
        }
    ) { paddingValues ->
        Background()
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Avatar(image = painterResource(id = R.drawable.avatar))
            Text(
                text = "Выбрать фотографию",
                textAlign = TextAlign.Center,
                fontSize = 18.sp,
                color = Color.Blue
            )

            TextCom(key = "Имя", value = profileViewModel.user!!.id.toString())
            TextCom(key = "Почта", value = profileViewModel.user!!.email)
            TextCom(key = "Возраст", value = (LocalDate.now().year - profileViewModel.user!!.dateOfBirth.year).toString())
            TextCom(key = "Пол", value = "ленолиум")

        }
    }
}