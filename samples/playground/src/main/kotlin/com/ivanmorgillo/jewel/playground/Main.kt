package com.ivanmorgillo.jewel.playground

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import org.jetbrains.jewel.intui.standalone.theme.darkThemeDefinition
import org.jetbrains.jewel.intui.standalone.theme.default
import org.jetbrains.jewel.intui.standalone.theme.lightThemeDefinition
import org.jetbrains.jewel.intui.window.decoratedWindow
import org.jetbrains.jewel.ui.ComponentStyling
import org.jetbrains.jewel.ui.component.CheckboxRow
import org.jetbrains.jewel.ui.component.SplitLayout2
import org.jetbrains.jewel.ui.component.Text
import org.jetbrains.jewel.ui.component.splitlayout.horizontalTwoPaneStrategy
import org.jetbrains.jewel.window.DecoratedWindow
import org.jetbrains.skiko.SystemTheme
import org.jetbrains.skiko.currentSystemTheme

fun main() {
    application {
        val textStyle = JewelTheme.createDefaultTextStyle(fontFamily = FontFamily.Inter)
        val editorStyle = JewelTheme.createEditorTextStyle(fontFamily = FontFamily.JetBrainsMono)
        var theme: IntUiThemes by remember { mutableStateOf(IntUiThemes.Light) }

        val themeDefinition =
            if (theme == IntUiThemes.Dark) {
                JewelTheme.darkThemeDefinition(defaultTextStyle = textStyle, editorTextStyle = editorStyle)
            } else {
                JewelTheme.lightThemeDefinition(defaultTextStyle = textStyle, editorTextStyle = editorStyle)
            }

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
                    Column {
                        CheckboxRow(
                            checked = theme == IntUiThemes.Dark,
                            onCheckedChange = {
                                theme =
                                    when (theme) {
                                        IntUiThemes.Dark -> IntUiThemes.Light
                                        IntUiThemes.Light -> IntUiThemes.Dark
                                        IntUiThemes.System -> TODO()
                                    }
                            },
                        ) {
                            Text("Dark Theme")
                        }

                        Box(
                            Modifier.fillMaxSize().border(1.dp, Color.Red),
                        ) {
                            SplitLayout2(
                                first = {
                                    Box(
                                        modifier =
                                            Modifier
                                                .fillMaxSize()
                                                .background(JewelTheme.globalColors.panelBackground)
                                                .padding(16.dp),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Text("Left Panel Content")
                                    }
                                },
                                second = {
                                    SplitLayout2(
                                        first = {
                                            Box(
                                                modifier =
                                                    Modifier
                                                        .fillMaxSize()
                                                        .background(JewelTheme.globalColors.panelBackground)
                                                        .padding(16.dp),
                                                contentAlignment = Alignment.Center,
                                            ) {
                                                Text("Nested - Left Panel Content")
                                            }
                                        },
                                        second = {
                                            Box(
                                                modifier =
                                                    Modifier
                                                        .fillMaxSize()
                                                        .background(JewelTheme.globalColors.panelBackground)
                                                        .padding(16.dp),
                                                contentAlignment = Alignment.Center,
                                            ) {
                                                Text("Nested - Right Panel Content")
                                            }
                                        },
                                        strategy =
                                            horizontalTwoPaneStrategy(
                                                initialSplitFraction = 0.5f,
                                                gapWidth = 1.dp,
                                            ),
                                        modifier = Modifier.fillMaxSize(),
                                        minFirstPaneSize = 200.dp,
                                        minSecondPaneSize = 100.dp,
                                    )
                                },
                                strategy =
                                    horizontalTwoPaneStrategy(
                                        initialSplitFraction = 0.5f,
                                        gapWidth = 1.dp,
                                    ),
                                modifier = Modifier.fillMaxSize(),
                                minFirstPaneSize = 100.dp,
                                minSecondPaneSize = 302.dp,
                            )
                        }
                    }
                },
            )
        }
    }
}

enum class IntUiThemes {
    Light,
    Dark,
    System,
    ;

    fun isDark() = (if (this == System) fromSystemTheme(currentSystemTheme) else this) == Dark

    companion object {
        fun fromSystemTheme(systemTheme: SystemTheme) = if (systemTheme == SystemTheme.LIGHT) Light else Dark
    }
}
