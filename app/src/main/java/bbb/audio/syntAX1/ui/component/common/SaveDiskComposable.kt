package bbb.audio.syntAX1.ui.component.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import bbb.audio.syntAX1.R
import bbb.audio.syntAX1.ui.theme.grapeNutsFont


@Composable
fun SaveDiskComposable(
    onSave: (String) -> Unit,
    onCancel: () -> Unit = {}
) {
    var saveName by remember { mutableStateOf("") }
    val isNameValid = saveName.isNotBlank()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Disk-Grafik
        Box(
            modifier = Modifier
                .size(300.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            Image(
                painter = painterResource(R.drawable.disk_graphic),
                contentDescription = "Floppy Disk",
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.Center)
            )

            // Text on Diskette Label (über der Disk)
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(y = 16.dp)
            ) {
                Text(
                    "Save as:",
                    fontFamily = grapeNutsFont,
                    fontSize = 14.sp,
                    color = Color.Black
                )

                TextField(
                    value = saveName,
                    onValueChange = { saveName = it },
                    textStyle = TextStyle(
                        fontFamily = grapeNutsFont,
                        fontSize = 16.sp,
                        color = Color.Black
                    ),
                    modifier = Modifier.width(180.dp),
                    placeholder = { Text("Sample Name...") },
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent
                    )
                )
            }
        }

        // Buttons (außerhalb der Floppy, mit Abstand)
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onCancel,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Cancel")
            }

            Button(
                onClick = { onSave(saveName) },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                enabled = isNameValid
            ) {
                Text("Save")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
