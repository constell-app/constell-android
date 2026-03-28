package dev.shoheiyamagiwa.constell.composable

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.shoheiyamagiwa.constell.R

@Composable
public fun ErrorAlert(message: String, modifier: Modifier = Modifier) {
    Alert(contentColor = Color.Red, message = message, iconResId = R.drawable.outline_error_24, modifier = modifier)
}

@Preview(showBackground = true, backgroundColor = 0xFF202020)
@Composable
private fun ErrorAlert_Preview() {
    ErrorAlert(message = "This is an error message")
}

@Composable
private fun Alert(contentColor: Color, message: String, @DrawableRes iconResId: Int, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .background(color = contentColor.copy(alpha = 0.1F), shape = RoundedCornerShape(size = 12.dp))
            .border(width = 1.dp, color = contentColor.copy(alpha = 0.3F), shape = RoundedCornerShape(size = 12.dp))
            .padding(all = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(painter = painterResource(id = iconResId), contentDescription = null, tint = contentColor, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = message, color = contentColor, fontSize = 13.sp, fontWeight = FontWeight.Medium)
    }
}
