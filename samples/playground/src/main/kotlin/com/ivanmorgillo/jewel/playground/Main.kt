package com.ivanmorgillo.jewel.playground

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.rememberTextFieldState
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
import org.jetbrains.jewel.intui.standalone.theme.default
import org.jetbrains.jewel.intui.standalone.theme.lightThemeDefinition
import org.jetbrains.jewel.intui.window.decoratedWindow
import org.jetbrains.jewel.ui.ComponentStyling
import org.jetbrains.jewel.ui.Outline
import org.jetbrains.jewel.ui.component.ComboBox
import org.jetbrains.jewel.ui.component.Dropdown
import org.jetbrains.jewel.ui.component.Text
import org.jetbrains.jewel.ui.component.separator
import org.jetbrains.jewel.window.DecoratedWindow

fun main() {
    application {
        val textStyle = JewelTheme.createDefaultTextStyle(fontFamily = FontFamily.Inter)
        val editorStyle = JewelTheme.createEditorTextStyle(fontFamily = FontFamily.JetBrainsMono)

        val themeDefinition =
            JewelTheme.lightThemeDefinition(defaultTextStyle = textStyle, editorTextStyle = editorStyle)

        IntUiTheme(theme = themeDefinition, styling = ComponentStyling.default().decoratedWindow()) {
            DecoratedWindow(
                onCloseRequest = { exitApplication() },
                title = "Jewel playground",
                state =
                    rememberWindowState(size = DpSize(1000.dp, 600.dp), position = WindowPosition(Alignment.Center)),
                content = {
                    val items = remember {
                        listOf(
                            "Cat",
                            "Elephant",
                            "Sun",
                            "Book",
                            "Laughter",
                            "Whisper",
                            "Ocean",
                            "Serendipity lorem ipsum",
                            "Umbrella",
                            "Joy",
                        )
                    }
                    var selected: String? by remember { mutableStateOf(items.first()) }
                    val inputTextFieldState = rememberTextFieldState(items.first())

                    Column(
                        Modifier.background(Color(0xfff7f8fa)).fillMaxSize().padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(text = "Selected item: $selected")
                        Text(text = "Input text: ${inputTextFieldState.text}")
                        Text(text = "ComboBox")
                        ComboBox(
                            inputTextFieldState = inputTextFieldState,
                            menuContent = {
                                items.forEach {
                                    if (it == "---") {
                                        separator()
                                    } else {
                                        selectableItem(
                                            selected = selected == it,
                                            onClick = {
                                                selected = it
                                                inputTextFieldState.edit { replace(0, length, it) }
                                            },
                                        ) {
                                            Text(it)
                                        }
                                    }
                                }
                            },
                        )

                        Spacer(Modifier.height(8.dp))
                        Text(text = "ComboBox")
                        ComboBox(
                            outline = Outline.Warning,
                            inputTextFieldState = inputTextFieldState,
                            menuContent = {
                                items.forEach {
                                    if (it == "---") {
                                        separator()
                                    } else {
                                        selectableItem(
                                            selected = selected == it,
                                            onClick = {
                                                selected = it
                                                inputTextFieldState.edit { replace(0, length, it) }
                                            },
                                        ) {
                                            Text(it)
                                        }
                                    }
                                }
                            },
                        )

                        Spacer(Modifier.height(8.dp))
                        Text(text = "ComboBox")
                        ComboBox(
                            outline = Outline.Error,
                            inputTextFieldState = inputTextFieldState,
                            menuContent = {
                                items.forEach {
                                    if (it == "---") {
                                        separator()
                                    } else {
                                        selectableItem(
                                            selected = selected == it,
                                            onClick = {
                                                selected = it
                                                inputTextFieldState.edit { replace(0, length, it) }
                                            },
                                        ) {
                                            Text(it)
                                        }
                                    }
                                }
                            },
                        )

                        Spacer(Modifier.height(8.dp))
                        Text(text = "Dropdown")
                        Dropdown(
                            menuContent = {
                                items.forEach {
                                    if (it == "---") {
                                        separator()
                                    } else {
                                        selectableItem(selected = selected == it, onClick = { selected = it }) {
                                            Text(it)
                                        }
                                    }
                                }
                            }
                        ) {
                            Text(selected ?: "---")
                        }
                    }
                },
            )
        }
    }
}
