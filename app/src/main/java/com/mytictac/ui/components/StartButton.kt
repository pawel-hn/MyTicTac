package com.mytictac.ui.components


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun StartButton(
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(24.dp)
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .height(60.dp)
                .width(120.dp)
                .blur(
                    radiusX = 15.dp,
                    radiusY = 15.dp,
                    edgeTreatment = BlurredEdgeTreatment.Unbounded
                )
                .clip(shape)
                .background(colors.containerColor)
        )
        Button(
            onClick = {},
            modifier = Modifier
                .height(60.dp)
                .width(120.dp),
            colors = colors,
            shape = shape,
            border = BorderStroke(width = 1.dp, color = Color.White),
            contentPadding = PaddingValues(20.dp),
        ) {
            Text(text = "Start", color = Color(0xFF7FFF00), fontSize = 20.sp)
        }
    }
}

val colors = ButtonColors(
    containerColor = Color(0xFF2E7D32),
    contentColor = Color.Transparent,
    disabledContainerColor = Color.Black,
    disabledContentColor = Color.LightGray

)


@Preview
@Composable
fun StartButtonPreview() {
    Box(
        modifier = Modifier.fillMaxSize().background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        StartButton()
    }
}


// XOXOXOXOXOXO