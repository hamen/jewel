package org.jetbrains.jewel.ui.component.splitlayout

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

public interface SplitLayoutStrategy {
    public fun calculateSplitResult(
        density: Density,
        layoutDirection: LayoutDirection,
        layoutCoordinates: LayoutCoordinates,
        dividerPosition: Float,
    ): SplitResult

    public fun isHorizontal(): Boolean
}

public fun horizontalTwoPaneStrategy(
    initialSplitFraction: Float = 0.5f,
    gapWidth: Dp = 0.dp,
): SplitLayoutStrategy = object : SplitLayoutStrategy {
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

public fun verticalTwoPaneStrategy(
    initialSplitFraction: Float = 0.5f,
    gapHeight: Dp = 0.dp,
): SplitLayoutStrategy = object : SplitLayoutStrategy {
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