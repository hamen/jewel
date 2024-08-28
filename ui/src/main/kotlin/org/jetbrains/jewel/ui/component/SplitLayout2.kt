package org.jetbrains.jewel.ui.component

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.ui.Orientation.Horizontal
import org.jetbrains.jewel.ui.Orientation.Vertical
import java.awt.Cursor
import kotlin.math.roundToInt

@Composable
public fun SplitLayout2(
    first: @Composable () -> Unit,
    second: @Composable () -> Unit,
    strategy: TwoPaneStrategy,
    modifier: Modifier = Modifier,
    dividerColor: Color = JewelTheme.globalColors.borders.normal,
    dividerThickness: Dp = 1.dp,
    draggableWidth: Dp = 8.dp,
) {
    val density = LocalDensity.current
    var dividerPosition by remember { mutableStateOf(0f) }
    var layoutCoordinates by remember { mutableStateOf<LayoutCoordinates?>(null) }

    Layout(
        modifier = modifier.onGloballyPositioned { coordinates ->
            layoutCoordinates = coordinates
        },
        content = {
            Box(Modifier.layoutId("first")) { first() }
            Box(Modifier.layoutId("second")) { second() }

            val dividerInteractionSource = remember { MutableInteractionSource() }
            val dividerOrientation = when {
                strategy.isHorizontal() -> Vertical
                else -> Horizontal
            }
            val fillMaxDirection = when {
                strategy.isHorizontal() -> Modifier.fillMaxHeight()
                else -> Modifier.fillMaxWidth()
            }

            val orientation = when {
                strategy.isHorizontal() -> Orientation.Horizontal
                else -> Orientation.Vertical
            }

            val cursor = when {
                strategy.isHorizontal() -> Cursor(Cursor.E_RESIZE_CURSOR)
                else -> Cursor(Cursor.N_RESIZE_CURSOR)
            }

            Divider(
                orientation = dividerOrientation,
                modifier = Modifier
                    .then(fillMaxDirection)
                    .layoutId("divider"),
                color = dividerColor,
                thickness = dividerThickness,
            )

            Box(
                Modifier
                    .let { modifier ->
                        if (strategy.isHorizontal()) {
                            modifier.fillMaxHeight().width(draggableWidth)
                        } else {
                            modifier.fillMaxWidth().height(draggableWidth)
                        }
                    }
                    .draggable(
                        orientation = orientation,
                        state = rememberDraggableState { delta -> dividerPosition += delta },
                        interactionSource = dividerInteractionSource,
                    )
                    .pointerHoverIcon(PointerIcon(cursor))
                    .layoutId("divider-handle")
            )
        }
    ) { measurables, constraints ->
        val firstMeasurable = measurables.find { it.layoutId == "first" }!!
        val secondMeasurable = measurables.find { it.layoutId == "second" }!!
        val dividerMeasurable = measurables.find { it.layoutId == "divider" }!!
        val dividerHandleMeasurable = measurables.find { it.layoutId == "divider-handle" }!!

        layoutCoordinates?.let { coordinates ->
            val splitResult = strategy.calculateSplitResult(
                density = density,
                layoutDirection = layoutDirection,
                layoutCoordinates = coordinates,
                dividerPosition = dividerPosition
            )

            val gapOrientation = splitResult.gapOrientation
            val gapBounds = splitResult.gapBounds

            val dividerWidth = with(density) { dividerThickness.roundToPx() }
            val handleWidth = with(density) { draggableWidth.roundToPx() }

            val dividerPlaceable = dividerMeasurable.measure(
                if (gapOrientation == Orientation.Vertical) {
                    constraints.copy(
                        minWidth = dividerWidth,
                        maxWidth = dividerWidth,
                        minHeight = constraints.minHeight,
                        maxHeight = constraints.maxHeight
                    )
                } else {
                    constraints.copy(
                        minWidth = constraints.minWidth,
                        maxWidth = constraints.maxWidth,
                        minHeight = dividerWidth,
                        maxHeight = dividerWidth
                    )
                }
            )

            val dividerHandlePlaceable = dividerHandleMeasurable.measure(
                if (gapOrientation == Orientation.Vertical) {
                    constraints.copy(
                        minWidth = handleWidth,
                        maxWidth = handleWidth,
                        minHeight = constraints.minHeight,
                        maxHeight = constraints.maxHeight
                    )
                } else {
                    constraints.copy(
                        minWidth = constraints.minWidth,
                        maxWidth = constraints.maxWidth,
                        minHeight = handleWidth,
                        maxHeight = handleWidth
                    )
                }
            )

            val availableSpace = if (gapOrientation == Orientation.Vertical) {
                constraints.maxWidth - dividerWidth
            } else {
                constraints.maxHeight - dividerWidth
            }

            val firstSize = (if (gapOrientation == Orientation.Vertical) gapBounds.left else gapBounds.top).roundToInt()
            val secondSize = availableSpace - firstSize

            val firstConstraints = if (gapOrientation == Orientation.Vertical) {
                constraints.copy(minWidth = 0, maxWidth = firstSize.coerceAtLeast(0))
            } else {
                constraints.copy(minHeight = 0, maxHeight = firstSize.coerceAtLeast(0))
            }

            val secondConstraints = if (gapOrientation == Orientation.Vertical) {
                constraints.copy(minWidth = 0, maxWidth = secondSize.coerceAtLeast(0))
            } else {
                constraints.copy(minHeight = 0, maxHeight = secondSize.coerceAtLeast(0))
            }

            val firstPlaceable = firstMeasurable.measure(firstConstraints)
            val secondPlaceable = secondMeasurable.measure(secondConstraints)

            layout(constraints.maxWidth, constraints.maxHeight) {
                firstPlaceable.placeRelative(0, 0)
                if (gapOrientation == Orientation.Vertical) {
                    dividerPlaceable.placeRelative(firstSize, 0)
                    dividerHandlePlaceable.placeRelative(firstSize - handleWidth / 2, 0)
                    secondPlaceable.placeRelative(firstSize + dividerWidth, 0)
                } else {
                    dividerPlaceable.placeRelative(0, firstSize)
                    dividerHandlePlaceable.placeRelative(0, firstSize - handleWidth / 2)
                    secondPlaceable.placeRelative(0, firstSize + dividerWidth)
                }
            }
        } ?: layout(constraints.minWidth, constraints.minHeight) {}
    }
}

public class SplitResult(
    public val gapOrientation: Orientation,
    public val gapBounds: Rect,
)

public interface TwoPaneStrategy {
    public fun calculateSplitResult(
        density: Density,
        layoutDirection: LayoutDirection,
        layoutCoordinates: LayoutCoordinates,
        dividerPosition: Float,
    ): SplitResult

    public fun isHorizontal(): Boolean
}

public fun HorizontalTwoPaneStrategy(
    initialSplitFraction: Float = 0.5f,
    gapWidth: Dp = 0.dp,
): TwoPaneStrategy = object : TwoPaneStrategy {
    override fun calculateSplitResult(
        density: Density,
        layoutDirection: LayoutDirection,
        layoutCoordinates: LayoutCoordinates,
        dividerPosition: Float,
    ): SplitResult {
        val availableWidth = layoutCoordinates.size.width
        val splitWidthPixel = with(density) { gapWidth.toPx() }
        val initialSplitX = availableWidth * initialSplitFraction
        val splitX = (initialSplitX + dividerPosition).coerceIn(0f, availableWidth.toFloat())

        return SplitResult(
            gapOrientation = Orientation.Vertical,
            gapBounds = Rect(
                left = splitX - splitWidthPixel / 2f,
                top = 0f,
                right = (splitX + splitWidthPixel / 2f).coerceAtMost(availableWidth.toFloat()),
                bottom = layoutCoordinates.size.height.toFloat(),
            )
        )
    }

    override fun isHorizontal(): Boolean = true
}

public fun VerticalTwoPaneStrategy(
    initialSplitFraction: Float = 0.5f,
    gapHeight: Dp = 0.dp,
): TwoPaneStrategy = object : TwoPaneStrategy {
    override fun calculateSplitResult(
        density: Density,
        layoutDirection: LayoutDirection,
        layoutCoordinates: LayoutCoordinates,
        dividerPosition: Float,
    ): SplitResult {
        val availableHeight = layoutCoordinates.size.height
        val splitHeightPixel = with(density) { gapHeight.toPx() }
        val initialSplitY = availableHeight * initialSplitFraction
        val splitY = (initialSplitY + dividerPosition).coerceIn(0f, availableHeight.toFloat())

        return SplitResult(
            gapOrientation = Orientation.Horizontal,
            gapBounds = Rect(
                left = 0f,
                top = splitY - splitHeightPixel / 2f,
                right = layoutCoordinates.size.width.toFloat(),
                bottom = (splitY + splitHeightPixel / 2f).coerceAtMost(availableHeight.toFloat()),
            )
        )
    }

    override fun isHorizontal(): Boolean = false
}
