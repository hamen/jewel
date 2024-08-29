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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.foundation.util.myLogger
import org.jetbrains.jewel.ui.Orientation.Horizontal
import org.jetbrains.jewel.ui.Orientation.Vertical
import org.jetbrains.jewel.ui.component.splitlayout.SplitLayoutStrategy
import java.awt.Cursor
import kotlin.math.roundToInt

@Composable
public fun SplitLayout2(
    first: @Composable () -> Unit,
    second: @Composable () -> Unit,
    strategy: SplitLayoutStrategy,
    modifier: Modifier = Modifier,
    dividerColor: Color = JewelTheme.globalColors.borders.normal,
    dividerThickness: Dp = 1.dp,
    draggableWidth: Dp = 8.dp,
    minFirstPaneSize: Dp = 100.dp,
    minSecondPaneSize: Dp = 100.dp,
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
                modifier = Modifier.then(fillMaxDirection).layoutId("divider"),
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
            val minFirstPaneSizePx = with(density) { minFirstPaneSize.roundToPx() }
            val minSecondPaneSizePx = with(density) { minSecondPaneSize.roundToPx() }

            // The visual divider itself. It's a thin line that separates the two panes
            val dividerPlaceable = dividerMeasurable.measure(
                when (gapOrientation) {
                    Orientation.Vertical -> {
                        constraints.copy(
                            minWidth = dividerWidth,
                            maxWidth = dividerWidth,
                            minHeight = constraints.minHeight,
                            maxHeight = constraints.maxHeight
                        )
                    }

                    Orientation.Horizontal -> {
                        constraints.copy(
                            minWidth = constraints.minWidth,
                            maxWidth = constraints.maxWidth,
                            minHeight = dividerWidth,
                            maxHeight = dividerWidth
                        )
                    }
                }
            )

            // This is a invisible, wider area around the divider that can be dragged by the user to resize the panes
            val dividerHandlePlaceable = dividerHandleMeasurable.measure(
                when (gapOrientation) {
                    Orientation.Vertical -> {
                        constraints.copy(
                            minWidth = handleWidth,
                            maxWidth = handleWidth,
                            minHeight = constraints.minHeight,
                            maxHeight = constraints.maxHeight
                        )
                    }

                    Orientation.Horizontal -> {
                        constraints.copy(
                            minWidth = constraints.minWidth,
                            maxWidth = constraints.maxWidth,
                            minHeight = handleWidth,
                            maxHeight = handleWidth
                        )
                    }
                }
            )

            val availableSpace = if (gapOrientation == Orientation.Vertical) {
                constraints.maxWidth - dividerWidth
            } else {
                constraints.maxHeight - dividerWidth
            }

            val firstGap = when (gapOrientation) {
                Orientation.Vertical -> gapBounds.left
                Orientation.Horizontal -> gapBounds.top
            }

            require(availableSpace - minSecondPaneSizePx > minFirstPaneSizePx) {
                myLogger().error(
                    "Not enough space for first pane:\n" +
                        "minFirstPaneSizePx: $minFirstPaneSizePx\n" +
                        "availableSpace - minSecondPaneSizePx: ${availableSpace - minSecondPaneSizePx}\n" +
                        "Please, adjust the panes sizes."
                )
                // I'm adding this to have something meaningful in the error dialog
                IllegalStateException("Not enough space for first pane")
            }
            val firstSize: Int = firstGap
                .roundToInt()
                .coerceIn(minFirstPaneSizePx, availableSpace - minSecondPaneSizePx)

            val secondSize = availableSpace - firstSize

            val firstConstraints = when (gapOrientation) {
                Orientation.Vertical -> constraints.copy(minWidth = minFirstPaneSizePx, maxWidth = firstSize)
                Orientation.Horizontal -> constraints.copy(minHeight = minFirstPaneSizePx, maxHeight = firstSize)
            }

            val secondConstraints = when (gapOrientation) {
                Orientation.Vertical -> constraints.copy(minWidth = minSecondPaneSizePx, maxWidth = secondSize)
                Orientation.Horizontal -> constraints.copy(minHeight = minSecondPaneSizePx, maxHeight = secondSize)
            }

            val firstPlaceable = firstMeasurable.measure(firstConstraints)
            val secondPlaceable = secondMeasurable.measure(secondConstraints)

            layout(constraints.maxWidth, constraints.maxHeight) {
                firstPlaceable.placeRelative(0, 0)
                when (gapOrientation) {
                    Orientation.Vertical -> {
                        dividerPlaceable.placeRelative(firstSize, 0)
                        dividerHandlePlaceable.placeRelative(firstSize - handleWidth / 2, 0)
                        secondPlaceable.placeRelative(firstSize + dividerWidth, 0)
                    }

                    Orientation.Horizontal -> {
                        dividerPlaceable.placeRelative(0, firstSize)
                        dividerHandlePlaceable.placeRelative(0, firstSize - handleWidth / 2)
                        secondPlaceable.placeRelative(0, firstSize + dividerWidth)
                    }
                }
            }
        } ?: layout(constraints.minWidth, constraints.minHeight) {}
    }
}

