package com.mytictac.ui.components


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun OptionButton(
    modifier: Modifier = Modifier,
    width: Float,
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) Color(0xFF2E7D32) else Color.LightGray
    val textColor = if (isSelected) Color(0xFF7FFF00) else Color.Gray

    val dpWidth = LocalDensity.current.run { width.dp }

    val shape = RoundedCornerShape(24.dp)
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .height(60.dp)
                .width(dpWidth)
                .blur(
                    radiusX = 15.dp,
                    radiusY = 15.dp,
                    edgeTreatment = BlurredEdgeTreatment.Unbounded
                )
                .clip(shape)
                .background(backgroundColor)
        )
        Button(
            onClick = onClick,
            modifier = Modifier
                .height(60.dp)
                .width(dpWidth),
            colors = ButtonDefaults.buttonColors(containerColor = backgroundColor),
            shape = shape,
            border = BorderStroke(width = 1.dp, color = Color.White),
            contentPadding = PaddingValues(20.dp),
        ) {
            Text(text = text, color = textColor, fontSize = 20.sp, style = TextStyle())
        }
    }
}


@Preview
@Composable
fun OptionButtonPreview() {
    Box(
        modifier = Modifier.fillMaxSize().background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        StartButton()
    }
}


// XOXOXOXOXOXO