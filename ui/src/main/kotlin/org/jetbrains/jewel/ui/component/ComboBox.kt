package org.jetbrains.jewel.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.HoverInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.input.InputMode
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import org.jetbrains.jewel.foundation.Stroke
import org.jetbrains.jewel.foundation.modifier.border
import org.jetbrains.jewel.foundation.state.CommonStateBitMask.Active
import org.jetbrains.jewel.foundation.state.CommonStateBitMask.Enabled
import org.jetbrains.jewel.foundation.state.CommonStateBitMask.Focused
import org.jetbrains.jewel.foundation.state.CommonStateBitMask.Hovered
import org.jetbrains.jewel.foundation.state.CommonStateBitMask.Pressed
import org.jetbrains.jewel.foundation.state.FocusableComponentState
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.foundation.theme.LocalContentColor
import org.jetbrains.jewel.ui.Orientation
import org.jetbrains.jewel.ui.Outline
import org.jetbrains.jewel.ui.component.styling.DropdownStyle
import org.jetbrains.jewel.ui.focusOutline
import org.jetbrains.jewel.ui.outline
import org.jetbrains.jewel.ui.theme.dropdownStyle
import org.jetbrains.jewel.ui.util.thenIf

@Composable
public fun ComboBox(
    modifier: Modifier = Modifier,
    inputTextFieldState: TextFieldState,
    enabled: Boolean = true,
    menuModifier: Modifier = Modifier,
    outline: Outline = Outline.None,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    style: DropdownStyle = JewelTheme.dropdownStyle,
    menuContent: MenuScope.() -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    var skipNextClick by remember { mutableStateOf(false) }

    var dropdownState by remember(interactionSource) { mutableStateOf(DropdownState.of(enabled = enabled)) }

    remember(enabled) { dropdownState = dropdownState.copy(enabled = enabled) }

    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction ->
            when (interaction) {
                is PressInteraction.Press -> dropdownState = dropdownState.copy(pressed = true)
                is PressInteraction.Cancel,
                is PressInteraction.Release -> dropdownState = dropdownState.copy(pressed = false)
                is HoverInteraction.Enter -> dropdownState = dropdownState.copy(hovered = false)
                is HoverInteraction.Exit -> dropdownState = dropdownState.copy(hovered = false)
                is FocusInteraction.Focus -> dropdownState = dropdownState.copy(focused = true)
                is FocusInteraction.Unfocus -> dropdownState = dropdownState.copy(focused = false)
            }
        }
    }

    val colors = style.colors
    val metrics = style.metrics
    val shape = RoundedCornerShape(style.metrics.cornerSize)
    val minSize = metrics.minSize
    // TODO: Different from Dropdown. We should create a new style for this
    // val arrowMinSize = style.metrics.arrowMinSize
    val arrowMinSize = DpSize(21.dp, 22.dp)
    val borderColor by colors.borderFor(dropdownState)
    val hasNoOutline = outline == Outline.None

    var componentWidth by remember { mutableIntStateOf(-1) }
    Box(
        modifier =
            modifier
                .clickable(
                    onClick = {
                        // TODO: Trick to skip click event when close menu by click dropdown
                        if (!skipNextClick) {
                            expanded = !expanded
                        }
                        skipNextClick = false
                    },
                    enabled = enabled,
                    role = Role.Button,
                    interactionSource = interactionSource,
                    indication = null,
                )
                .background(colors.backgroundFor(dropdownState).value, shape)
                .thenIf(hasNoOutline) { border(Stroke.Alignment.Inside, style.metrics.borderWidth, borderColor, shape) }
                .thenIf(outline == Outline.None) { focusOutline(dropdownState, shape) }
                .outline(dropdownState, outline, shape)
                .width(IntrinsicSize.Max)
                .defaultMinSize(minSize.width, minSize.height)
                .onSizeChanged { componentWidth = it.width },
        contentAlignment = Alignment.CenterStart,
    ) {
        CompositionLocalProvider(LocalContentColor provides colors.contentFor(dropdownState).value) {
            Box(
                modifier =
                    Modifier.fillMaxWidth().padding(style.metrics.contentPadding).padding(end = arrowMinSize.width),
                contentAlignment = Alignment.CenterStart,
                content = {
                    BasicTextField(
                        state = inputTextFieldState,
                        modifier = Modifier.fillMaxWidth(),
                        lineLimits = TextFieldLineLimits.SingleLine,
                    )
                },
            )

            Box(
                modifier =
                    Modifier.height(IntrinsicSize.Min)
                        .defaultMinSize(arrowMinSize.width, arrowMinSize.height)
                        .align(Alignment.CenterEnd),
                contentAlignment = Alignment.Center,
            ) {
                Divider(
                    orientation = Orientation.Vertical,
                    thickness = metrics.borderWidth,
                    color = colors.border,
                    modifier = Modifier.align(Alignment.CenterStart),
                )
                Icon(key = style.icons.chevronDown, contentDescription = null, tint = colors.iconTint)
            }
        }

        if (expanded) {
            val density = LocalDensity.current
            PopupMenu(
                onDismissRequest = {
                    expanded = false
                    if (it == InputMode.Touch && dropdownState.isHovered) {
                        skipNextClick = true
                    }
                    true
                },
                modifier =
                    menuModifier
                        .padding(horizontal = 3.dp)
                        .focusProperties { canFocus = true }
                        .defaultMinSize(minWidth = with(density) { componentWidth.toDp() }),
                style = style.menuStyle,
                horizontalAlignment = Alignment.Start,
                content = menuContent,
            )
        }
    }
}

@Immutable
@JvmInline
public value class ComboBoxState(public val state: ULong) : FocusableComponentState {
    override val isActive: Boolean
        get() = state and Active != 0UL

    override val isEnabled: Boolean
        get() = state and Enabled != 0UL

    override val isFocused: Boolean
        get() = state and Focused != 0UL

    override val isHovered: Boolean
        get() = state and Hovered != 0UL

    override val isPressed: Boolean
        get() = state and Pressed != 0UL

    public fun copy(
        enabled: Boolean = isEnabled,
        focused: Boolean = isFocused,
        pressed: Boolean = isPressed,
        hovered: Boolean = isHovered,
        active: Boolean = isActive,
    ): ComboBoxState = of(enabled = enabled, focused = focused, pressed = pressed, hovered = hovered, active = active)

    override fun toString(): String =
        "${javaClass.simpleName}(isEnabled=$isEnabled, isFocused=$isFocused, " +
            "isHovered=$isHovered, isPressed=$isPressed, isActive=$isActive)"

    public companion object {
        public fun of(
            enabled: Boolean = true,
            focused: Boolean = false,
            pressed: Boolean = false,
            hovered: Boolean = false,
            active: Boolean = false,
        ): ComboBoxState =
            ComboBoxState(
                (if (enabled) Enabled else 0UL) or
                    (if (focused) Focused else 0UL) or
                    (if (hovered) Hovered else 0UL) or
                    (if (pressed) Pressed else 0UL) or
                    (if (active) Active else 0UL)
            )
    }
}
