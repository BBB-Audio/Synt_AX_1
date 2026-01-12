package bbb.audio.syntAX1.ui.dialog

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import bbb.audio.syntAX1.R

@Composable
fun ErrorDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    errorCode: Int,
    errorMessage: String,
    painter: Painter,
    imageDescription: String,
    modifier: Modifier = Modifier
) {
    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .height(404.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .align(alignment = Alignment.CenterHorizontally),
                    contentAlignment = Alignment.Center
                ) {
                    // Image
                    Image(
                        painter = painter,
                        contentDescription = imageDescription,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .height(160.dp)
                    )

                    // Error Code
                    Text(
                        text = errorCode.toString(),
                        style = MaterialTheme.typography.displayLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.Red.copy(alpha = 0.8f),
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))

                // Error Message
                Text(
                    text = errorMessage,
                    modifier = Modifier.padding(horizontal = 16.dp),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Solution
                when (errorCode) {
                    400 -> {
                        Text(
                            text = "💡 Check your request for errors.",
                            modifier = Modifier.padding(horizontal = 16.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.secondary,
                            textAlign = TextAlign.Center
                        )
                    }
                    401, 403 -> {
                        Text(
                            text = "💡 Check your authentication/authorization.",
                            modifier = Modifier.padding(horizontal = 16.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.secondary,
                            textAlign = TextAlign.Center
                        )
                    }
                    404 -> {
                        Text(
                            text = "💡 The requested resource does not exist.",
                            modifier = Modifier.padding(horizontal = 16.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.secondary,
                            textAlign = TextAlign.Center
                        )
                    }
                    500, 503 -> {
                        Text(
                            text = "💡 Try again later.",
                            modifier = Modifier.padding(horizontal = 16.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.secondary,
                            textAlign = TextAlign.Center
                        )
                    }
                    else -> {
                        Text(
                            text = "💡 Check your internet connection and try again.",
                            modifier = Modifier.padding(horizontal = 16.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.secondary,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    TextButton(
                        onClick = { onDismissRequest() },
                        modifier = modifier.padding(8.dp),
                    ) {
                        Text("Close")
                    }
                    TextButton(
                        onClick = { onConfirmation() },
                        modifier = modifier.padding(8.dp),
                    ) {
                        Text("Try Again")
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun ErrorDialogPreview() {
    ErrorDialog(
        onDismissRequest = { },
        onConfirmation = { },
        errorCode = 404,
        errorMessage = "The requested resource does not exist.",
        painter = painterResource(R.drawable.error),
        imageDescription = " "
    )
}