package uz.tubeme.app.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// TubeMe AMOLED palette (matches css/variables.css)
val Bg = Color(0xFF050505)
val BgElev = Color(0xFF0A0A0A)
val Surface = Color(0xFF101010)
val Surface2 = Color(0xFF161616)
val Surface3 = Color(0xFF1C1C1C)
val BorderSoft = Color(0x0FFFFFFF)
val BorderStrong = Color(0x1FFFFFFF)

val NeonBlue = Color(0xFF00D4FF)
val NeonPurple = Color(0xFF7B61FF)
val NeonPink = Color(0xFFFF3CAC)

val TextPri = Color(0xFFFFFFFF)
val TextSec = Color(0xFFA0A0A0)
val TextDim = Color(0xFF6B6B6B)
val Danger = Color(0xFFFF3B5C)
val Success = Color(0xFF2ECC71)
val Warning = Color(0xFFF39C12)

val NeonGradient = Brush.linearGradient(
    colors = listOf(NeonBlue, NeonPurple)
)
val NeonGradient3 = Brush.linearGradient(
    colors = listOf(NeonBlue, NeonPurple, NeonPink)
)
