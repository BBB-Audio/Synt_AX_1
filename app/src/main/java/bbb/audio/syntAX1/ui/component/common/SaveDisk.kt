package bbb.audio.syntAX1.ui.component.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import bbb.audio.syntAX1.R
import bbb.audio.syntAX1.ui.theme.Synt_AX_1Theme
import bbb.audio.syntAX1.ui.theme.grapeNutsFont

@Composable
fun SaveDisk(
    saveName: String,
    onNameChange: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color.Transparent)
    ) {
        Image(
            painter = painterResource(R.drawable.disk_graphic),
            contentDescription = "Floppy Disk",
            modifier = Modifier
                .align(Alignment.Center)
                .size(300.dp)
        )

        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = (28).dp)
        ) {
            Text(
                "Save as:",
                fontFamily = grapeNutsFont,
                fontSize = 36.sp,
                color = Color.Black
            )

            TextField(
                value = saveName,
                onValueChange = onNameChange,
                textStyle = TextStyle(
                    fontFamily = grapeNutsFont,
                    fontSize = 28.sp,
                    color = Color.Black
                ),
                modifier = Modifier.width(180.dp),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent
                )
            )
        }
    }
}

@Preview
@Composable
private fun SaveDiskPreview() {
    Synt_AX_1Theme{
    SaveDisk(
        saveName = "Sample Name",
        onNameChange = { }
    )
    }
}