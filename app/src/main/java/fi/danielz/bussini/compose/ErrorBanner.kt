package fi.danielz.bussini.compose

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.Text
import com.apollographql.apollo3.api.Error


@ExperimentalAnimationApi
@Composable
fun ErrorBanner(errors: List<Error>) {
    AnimatedVisibility(visible = errors.isNotEmpty()) {

        Column(modifier = Modifier.padding(4.dp)) {
            Text(text = "An error occurred, please try again later")
            Button(content = {
                Text(text = "Retry")
            }, onClick = {
                // TODO
            })
        }
    }
}