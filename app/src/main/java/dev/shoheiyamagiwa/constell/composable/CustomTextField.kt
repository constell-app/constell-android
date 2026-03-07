package dev.shoheiyamagiwa.constell.composable

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.shoheiyamagiwa.constell.R
import dev.shoheiyamagiwa.constell.ui.theme.Blue400
import dev.shoheiyamagiwa.constell.ui.theme.Blue500
import dev.shoheiyamagiwa.constell.ui.theme.Purple500
import dev.shoheiyamagiwa.constell.ui.theme.Slate100
import dev.shoheiyamagiwa.constell.ui.theme.Slate500
import dev.shoheiyamagiwa.constell.ui.theme.Slate700
import dev.shoheiyamagiwa.constell.ui.theme.Slate800

@Composable
public fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String,
    leadingIcon: ImageVector,
    maskValue: Boolean = false,
    isValueMasked: Boolean = true,
    onMaskToggled: () -> Unit = {},
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    val glowAlpha by animateFloatAsState(targetValue = if (isFocused) 0.5F else 0.0F, label = "glow")
    val borderColor by animateColorAsState(targetValue = if (isFocused) Blue500.copy(alpha = 0.5F) else Slate700, label = "border")
    val iconColor by animateColorAsState(targetValue = if (isFocused) Blue400 else Slate500, label = "icon")

    Box(modifier = modifier.fillMaxWidth().clip(shape = RoundedCornerShape(size = 12.dp)), contentAlignment = Alignment.Center) {
        // Background Glow Effect
        if (glowAlpha > 0f) {
            Box(modifier = Modifier.matchParentSize().blur(radius = 16.dp).background(Brush.horizontalGradient(colors = listOf(Blue500, Purple500))).padding(all = 4.dp))
        }

        // Actual TextField Container
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            interactionSource = interactionSource,
            textStyle = TextStyle(color = Slate100, fontSize = 14.sp),
            cursorBrush = SolidColor(value = Blue400),
            keyboardOptions = keyboardOptions,
            visualTransformation = if (maskValue && isValueMasked) PasswordVisualTransformation() else VisualTransformation.None,
            singleLine = true,
            modifier = Modifier.fillMaxWidth().background(color = Slate800.copy(alpha = 0.8f), shape = RoundedCornerShape(size = 12.dp)).border(width = 1.dp, borderColor, shape = RoundedCornerShape(size = 12.dp)),
            decorationBox = { innerTextField ->
                Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = leadingIcon, contentDescription = null, tint = iconColor, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Box(modifier = Modifier.weight(1.0F)) {
                        if (value.isEmpty()) {
                            Text(text = placeholder, color = Slate500, fontSize = 14.sp)
                        }
                        innerTextField()
                    }
                    if (maskValue) {
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(onClick = onMaskToggled, modifier = Modifier.size(24.dp)) {
                            val iconId = if (!isValueMasked) R.drawable.outline_visibility_off_24 else R.drawable.outline_visibility_24
                            Icon(painter = painterResource(id = iconId), contentDescription = "Toggle Visibility", tint = Slate500, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        )
    }
}

@Preview
@Composable
private fun CustomTextFieldPreview_Empty() {
    CustomTextField(
        value = "",
        placeholder = "Enter your text here",
        leadingIcon = Icons.Filled.Email,
        modifier = Modifier,
        maskValue = false,
        isValueMasked = false,
        onValueChange = {},
        onMaskToggled = {}
    )
}

@Preview
@Composable
private fun CustomTextFieldPreview_Input() {
    CustomTextField(
        value = "Hello World!",
        placeholder = "Enter your text here",
        leadingIcon = Icons.Filled.Email,
        modifier = Modifier,
        maskValue = false,
        isValueMasked = false,
        onValueChange = {},
        onMaskToggled = {}
    )
}

@Preview
@Composable
private fun CustomTextFieldPreview_Masked() {
    CustomTextField(
        value = "Hello World!",
        placeholder = "Enter your text here",
        leadingIcon = Icons.Filled.Email,
        modifier = Modifier,
        maskValue = true,
        isValueMasked = true,
        onValueChange = {},
        onMaskToggled = {}
    )
}

@Preview
@Composable
private fun CustomTextFieldPreview_UnMasked() {
    CustomTextField(
        value = "Hello World!",
        placeholder = "Enter your text here",
        leadingIcon = Icons.Filled.Email,
        modifier = Modifier,
        maskValue = true,
        isValueMasked = false,
        onValueChange = {},
        onMaskToggled = {}
    )
}