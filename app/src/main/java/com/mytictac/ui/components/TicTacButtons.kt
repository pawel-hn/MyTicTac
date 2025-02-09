package com.mytictac.ui.components


import androidx.compose.animation.animateColor
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mytictac.ui.theme.MyTicTacTheme

const val BUTTON_TRANSITION = 700

@Composable
fun TicTacButton(
    modifier: Modifier = Modifier,
    width: Dp,
    enabledPrimaryColor: Color,
    enabledSecondaryColor: Color,
    height: Dp = 60.dp,
    textSize: TextUnit = 20.sp,
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {

    val transition = updateTransition(targetState = isSelected, label = null)

    val animatePrimaryColor = transition.animateColor(
        label = "",
        transitionSpec = { tween(BUTTON_TRANSITION) }
    ) { selectedState ->
        if (selectedState) enabledPrimaryColor else Color.LightGray
    }
    val animateSecondaryColor = transition.animateColor(
        label = "",
        transitionSpec = { tween(BUTTON_TRANSITION) }
        ) { selectedState ->
        if (selectedState)  enabledSecondaryColor else Color.Gray
    }

    val shape = RoundedCornerShape(24.dp)
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .height(height)
                .width(width)
                .blur(
                    radiusX = 15.dp,
                    radiusY = 15.dp,
                    edgeTreatment = BlurredEdgeTreatment.Unbounded
                )
                .clip(shape)
                .background(animatePrimaryColor.value)
        )
        Button(
            onClick = onClick,
            modifier = Modifier
                .height(height)
                .width(width),
            colors = ButtonDefaults.buttonColors(containerColor = animatePrimaryColor.value),
            shape = shape,
            border = BorderStroke(width = 1.dp, color = animateSecondaryColor.value),
        ) {
            Text(
                text = text,
                color = animateSecondaryColor.value,
                fontSize = textSize,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
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
        TicTacButton(
            width = 120.dp,
            enabledPrimaryColor = MyTicTacTheme.colours.interactivePrimary,
            enabledSecondaryColor = MyTicTacTheme.colours.interactivePrimaryContent,
            text = "Start",
            isSelected = true,
            onClick = {}
        )
    }
}
