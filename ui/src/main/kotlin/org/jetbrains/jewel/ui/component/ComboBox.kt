package org.jetbrains.jewel.ui.component

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.foundation.theme.LocalContentColor
import org.jetbrains.jewel.foundation.theme.LocalTextStyle
import org.jetbrains.jewel.ui.Outline
import org.jetbrains.jewel.ui.component.styling.DropdownStyle
import org.jetbrains.jewel.ui.theme.dropdownStyle

@Composable
public fun ComboBox(
    modifier: Modifier = Modifier,
    items: List<String>,
    textFieldState: TextFieldState,
    selectedItem: String?,
    enabled: Boolean = true,
    menuModifier: Modifier = Modifier,
    outline: Outline = Outline.None,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    style: DropdownStyle = JewelTheme.dropdownStyle,
    onItemSelect: (String) -> Unit,
    onExpandedChange: (Boolean) -> Unit = {},
    content: @Composable (BoxScope.(String) -> Unit),
) {
    Dropdown(
        modifier = modifier,
        enabled = enabled,
        menuModifier = menuModifier,
        outline = outline,
        interactionSource = interactionSource,
        style = style,
        menuContent = {
            items.forEach { item ->
                selectableItem(
                    selected = item == selectedItem,
                    onClick = {
                        onItemSelect(item)
                        textFieldState.edit { replace(0, length, item) }
                        onExpandedChange(false)
                    },
                ) {
                    Box { content(this, item) }
                }
            }
        },
    ) {
        BasicTextField(
            state = textFieldState,
            modifier = Modifier.fillMaxWidth(),
            textStyle = LocalTextStyle.current.copy(color = LocalContentColor.current),
            lineLimits = TextFieldLineLimits.SingleLine,
        )
    }
}
