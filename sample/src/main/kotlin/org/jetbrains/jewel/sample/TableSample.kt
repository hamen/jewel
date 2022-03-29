package org.jetbrains.jewel.sample

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.singleWindowApplication
import org.jetbrains.jewel.theme.intellij.IntelliJThemeDark
import org.jetbrains.jewel.theme.intellij.components.Surface
import org.jetbrains.jewel.theme.intellij.components.Table
import org.jetbrains.jewel.theme.intellij.components.TableModel
import org.jetbrains.jewel.theme.toolbox.components.Text

fun main() {
    singleWindowApplication {
        IntelliJThemeDark {
            Surface(modifier = Modifier.fillMaxSize()) {
                val modelContents = TableModel(30, 6) { i, j -> "Hello ${((i + 1) * (j + 1) - 1)}" }
                Table(modelContents, Modifier.matchParentSize(), 3.dp) { model, i, _ ->
                    Text(
                        model,
                        softWrap = false,
                        modifier = Modifier.background(
                            if (i % 2 == 0) Color.Red else Color.Blue
                        ).fillMaxSize()
                    )
                }
            }
        }
    }
}
