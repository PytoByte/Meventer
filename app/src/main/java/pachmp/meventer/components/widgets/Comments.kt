package pachmp.meventer.components.widgets

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gowtham.ratingbar.RatingBar
import com.gowtham.ratingbar.RatingBarConfig
import com.gowtham.ratingbar.RatingBarStyle
import pachmp.meventer.DefaultViewModel
import pachmp.meventer.R
import pachmp.meventer.components.widgets.models.FeedbackModel

@Composable
fun FeedbackCard(defaultViewModel: DefaultViewModel, feedback: FeedbackModel) {
    with(defaultViewModel) {
        Card(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
        ) {
            var expanded by remember { mutableStateOf(false) }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.padding(top = 12.dp, start = 12.dp, end = 12.dp)
            ) {
                if (feedback.author!=null) {
                    Image(
                        bitmap = getImageFromName(feedback.author.avatar).value,
                        contentDescription = "author avatar",
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Text(text = feedback.author.name, fontWeight = FontWeight.Bold)
                } else {
                    Text(text = stringResource(R.string.user_loading_failed))
                }
            }
            Column(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "${feedback.rating}")
                    RatingBar(
                        value = feedback.rating,
                        config = RatingBarConfig().numStars(5).style(RatingBarStyle.HighLighted)
                            .size(20.dp),
                        onValueChange = {},
                        onRatingChanged = {})
                }
                Column {
                    if(feedback.comment.length < 55){
                        Text(text = feedback.comment)
                    }else{
                        if (expanded) {
                            Text(text = feedback.comment)
                        } else {
                            Text(
                                text = feedback.comment.take(55) + "..."
                            )
                        }

                        if (feedback.comment.length > 55) {
                            OutlinedButton(onClick = { expanded = !expanded }) {
                                Text(if (expanded) stringResource(R.string.condense) else stringResource(R.string.expand))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CommentsList(defaultViewModel: DefaultViewModel, feedbacks: List<FeedbackModel>) {
    LazyColumn {
        items(feedbacks) {
            FeedbackCard(defaultViewModel, it)
        }
    }
}