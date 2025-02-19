package com.mytictac.ui.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mytictac.game.GameDialog

@Composable
fun TicTacDialog(gameDialog: GameDialog, onConfirm: () -> Unit, onCancel: () -> Unit) {
    AlertDialog(
        onDismissRequest = { onCancel() },
        title = { Text(text = gameDialog.title) },
        text = { Text(text = gameDialog.message) },
        confirmButton = {
            Button(onClick = {
                onConfirm()
            }) {
                Text(text = gameDialog.confirmButtonText)
            }
        },
        dismissButton = {
            Button(onClick = {
                onCancel()
            }) {
                Text(text = gameDialog.cancelButtonText)
            }
        },
        shape = RoundedCornerShape(16.dp),
        containerColor = Color.LightGray
    )
}

@Preview
@Composable
fun PreviewGameDialog() {
    TicTacDialog(
        gameDialog = GameDialog.CancelGame,
        onConfirm = {},
        onCancel = {}
    )
}