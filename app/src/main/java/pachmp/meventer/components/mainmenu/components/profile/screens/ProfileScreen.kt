package pachmp.meventer.components.mainmenu.components.profile.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gowtham.ratingbar.RatingBar
import com.gowtham.ratingbar.RatingBarConfig
import com.gowtham.ratingbar.RatingBarStyle
import com.ramcosta.composedestinations.annotation.Destination
import pachmp.meventer.R
import pachmp.meventer.components.mainmenu.components.profile.ProfileNavGraph
import pachmp.meventer.components.mainmenu.components.profile.ProfileViewModel
import pachmp.meventer.components.widgets.Avatar
import pachmp.meventer.components.widgets.CommentsList
import pachmp.meventer.components.widgets.LoadingScreen
import pachmp.meventer.ui.theme.MeventerTheme
import pachmp.meventer.ui.transitions.BottomTransition
import java.time.LocalDate

@ProfileNavGraph(start = true)
@Destination(style = BottomTransition::class)
@Composable
fun ProfileScreen(profileViewModel: ProfileViewModel) {
    var dropdownMenuExpanded by remember { mutableStateOf(false) }

    with(profileViewModel) {
        if (user == null) {
            LoadingScreen(Modifier.fillMaxSize())
        } else {
            MeventerTheme {
                Scaffold(
                    snackbarHost = { SnackbarHost(snackbarHostState) },
                    topBar = {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Edit button
                            IconButton(
                                onClick = { dropdownMenuExpanded = true }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit account",
                                    modifier = Modifier.size(32.dp)
                                )
                            }

                            DropdownMenu(
                                expanded = dropdownMenuExpanded,
                                onDismissRequest = { dropdownMenuExpanded = false }) {
                                DropdownMenuItem(
                                    text = { Text(stringResource(R.string.data_edit)) },
                                    onClick = { navigateToEditData() })
                                DropdownMenuItem(
                                    text = { Text(stringResource(R.string.email_change)) },
                                    onClick = { navigateToEditEmail() })
                                DropdownMenuItem(
                                    text = { Text(stringResource(R.string.password_change)) },
                                    onClick = { navigateToEditPassword() })
                            }

                            // Logout button
                            IconButton(
                                onClick = { logout() }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Logout,
                                    contentDescription = "Logout",
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                    }
                ) { paddingValues ->
                    if (profileViewModel.user != null) {
                        Column(
                            modifier = Modifier
                                .fillMaxHeight()
                                .padding(paddingValues)
                                .verticalScroll(rememberScrollState()),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Spacer(modifier = Modifier.height(25.dp))
                            // Avatar
                            Avatar(getImageFromName(avatar).value)

                            // Username and ID
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(user!!.name, style = MaterialTheme.typography.headlineLarge)
                            Text("@${user!!.nickname}", style = MaterialTheme.typography.bodySmall)

                            // Details
                            Spacer(modifier = Modifier.height(20.dp))
                            Column(
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.padding(horizontal = 12.dp)
                            ) {

                                OutlinedCard(
                                    modifier = Modifier.fillMaxWidth(),
                                    border = BorderStroke(0.55f.dp, MaterialTheme.colorScheme.onSecondaryContainer)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(10.dp),
                                        verticalArrangement = Arrangement.spacedBy(5.dp)
                                    ) {
                                        ProfileDetail(
                                            stringResource(R.string.email),
                                            user!!.email,
                                        )

                                        ProfileDetail(
                                            stringResource(R.string.age),
                                            (LocalDate.now().year - user!!.dateOfBirth.year - (if (LocalDate.now().dayOfYear < user!!.dateOfBirth.dayOfYear) 1 else 0)).toString()
                                        )
                                    }
                                }

                                //comments
                                OutlinedCard(
                                    modifier = Modifier
                                        .heightIn(max = 288.dp)
                                        .fillMaxWidth(),
                                    border = BorderStroke(
                                        0.65f.dp,
                                        MaterialTheme.colorScheme.onSecondaryContainer
                                    ),

                                    ) {
                                    if (feedbackModels != null) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = stringResource(R.string.feedbacks),
                                                modifier = Modifier.padding(12.dp)
                                            )
                                            RatingBar(
                                                value = avrRating,
                                                config = RatingBarConfig().numStars(5)
                                                    .style(RatingBarStyle.HighLighted).size(24.dp),
                                                onValueChange = {},
                                                onRatingChanged = {})
                                        }
                                        CommentsList(profileViewModel, feedbackModels!!)
                                    } else {
                                        LoadingScreen(Modifier.fillMaxSize())
                                    }

                                }
                            }

                        }
                    } else {
                        LoadingScreen(Modifier.fillMaxSize())
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileDetail(label: String, value: String) {
    Column(
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            label,
            modifier = Modifier,
            fontSize = 18.sp,
            maxLines = 1
        )
        Text(
            value,
            modifier = Modifier,
            fontSize = 15.sp,
            maxLines = 1
        )
    }
}
