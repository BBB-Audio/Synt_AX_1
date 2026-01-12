package bbb.audio.syntAX1.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import bbb.audio.syntAX1.R

// 1. Define Font Families
val gameOfSquids = FontFamily(
    Font(R.font.game_of_squids, FontWeight.Normal)
)

val syntheticSharps = FontFamily(
    Font(R.font.synthetic_sharps, FontWeight.Normal)
)

val grapeNutsFont = FontFamily(
    Font(R.font.grape_nuts))

val notesFont = FontFamily(
    Font(R.font.notes))

val synthetic_synchronism = FontFamily(
    Font(R.font.synthetic_synchronism))


// 2. Define Typography
val Typography = Typography(
    // For large screen titles
    headlineLarge = TextStyle(
        fontFamily = gameOfSquids,
        fontWeight = FontWeight.Normal,
        fontSize = 48.sp
    ),
    // For Medium screen titles
    headlineMedium = TextStyle(
        fontFamily = gameOfSquids,
        fontWeight = FontWeight.Normal,
        fontSize = 28.sp
    ),
    // For small screen titles
    headlineSmall = TextStyle(
        fontFamily = gameOfSquids,
        fontWeight = FontWeight.Normal,
        fontSize = 18.sp
    ),
    // For standard text, buttons, and components
    bodyLarge = TextStyle(
        fontFamily = syntheticSharps,
        fontWeight = FontWeight.Normal,
        fontSize = 18.sp,
        lineHeight = 24.sp
    ),
    // For smaller labels
    labelSmall = TextStyle(
        fontFamily = syntheticSharps,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp
    ),
    // For medium labels
    labelMedium = TextStyle(
        fontFamily = syntheticSharps,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        lineHeight = 20.sp
    ),
    titleLarge = TextStyle(
        fontFamily = syntheticSharps,
        fontWeight = FontWeight.Normal,
        fontSize = 24.sp,
        lineHeight = 28.sp
    ),

    displayMedium = TextStyle(
        fontFamily = synthetic_synchronism,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp

    )
)