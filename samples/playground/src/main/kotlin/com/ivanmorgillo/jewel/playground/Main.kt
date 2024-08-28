package com.ivanmorgillo.jewel.playground

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.intui.standalone.Inter
import org.jetbrains.jewel.intui.standalone.JetBrainsMono
import org.jetbrains.jewel.intui.standalone.theme.IntUiTheme
import org.jetbrains.jewel.intui.standalone.theme.createDefaultTextStyle
import org.jetbrains.jewel.intui.standalone.theme.createEditorTextStyle
import org.jetbrains.jewel.intui.standalone.theme.default
import org.jetbrains.jewel.intui.standalone.theme.lightThemeDefinition
import org.jetbrains.jewel.intui.window.decoratedWindow
import org.jetbrains.jewel.ui.ComponentStyling
import org.jetbrains.jewel.ui.component.HorizontalTwoPaneStrategy
import org.jetbrains.jewel.ui.component.SplitLayout2
import org.jetbrains.jewel.ui.component.Text
import org.jetbrains.jewel.window.DecoratedWindow

fun main() {
    application {
        val textStyle = JewelTheme.createDefaultTextStyle(fontFamily = FontFamily.Inter)
        val editorStyle = JewelTheme.createEditorTextStyle(fontFamily = FontFamily.JetBrainsMono)

        val themeDefinition =
            JewelTheme.lightThemeDefinition(defaultTextStyle = textStyle, editorTextStyle = editorStyle)

        IntUiTheme(
            theme = themeDefinition,
            styling = ComponentStyling.default().decoratedWindow(),
        ) {
            DecoratedWindow(
                onCloseRequest = { exitApplication() },
                title = "Jewel playground",
                state =
                    rememberWindowState(
                        size = DpSize(1000.dp, 600.dp),
                        position = WindowPosition(Alignment.Center),
                    ),
                content = {
                    SplitLayout2(
                        first = {
                            Box(
                                modifier = Modifier.fillMaxSize().padding(16.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text("Left Panel Content")
                            }
                        },
                        second = {
                            Box(
                                modifier = Modifier.fillMaxSize().padding(16.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text("Right Panel Content")
                            }
                        },
                        strategy =
                            HorizontalTwoPaneStrategy(
                                initialSplitFraction = 0.5f,
                                gapWidth = 1.dp,
                            ),
                        modifier = Modifier.fillMaxSize(),
                    )
                },
            )
        }
    }
}
