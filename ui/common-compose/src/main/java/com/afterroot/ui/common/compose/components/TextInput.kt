/*
 * 2021 AfterROOT
 */
package com.afterroot.ui.common.compose.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp

/**
 * [TextField] with Validation
 */
@OptIn(ExperimentalComposeUiApi::class, ExperimentalAnimationApi::class)
@Composable
fun TextInput(
    modifier: Modifier = Modifier,
    label: String,
    maxLines: Int = 1,
    errorText: String = "",
    keyboardController: SoftwareKeyboardController? = LocalSoftwareKeyboardController.current,
    keyboardOptions: KeyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
    keyboardActions: KeyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
    validate: (String) -> Boolean = { true },
    onChange: (String) -> Unit,
    onError: (String) -> Unit = {}
) {
    var value by remember { mutableStateOf("") }
    var error by remember { mutableStateOf(false) }
    Column {
        TextField(
            value = value,
            onValueChange = {
                value = it // always update state
                when {
                    validate(it) || it.isBlank() -> {
                        error = false
                        onChange(it)
                    }
                    else -> {
                        error = true
                        onError(it)
                    }
                }
            },
            isError = error,
            label = { Text(text = label) },
            maxLines = maxLines,
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(top = 8.dp),
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions
        )
        AnimatedVisibility(visible = error) {
            Text(
                text = errorText,
                modifier = Modifier
                    .paddingFromBaseline(top = 16.dp)
                    .padding(horizontal = (16.dp * 2)),
                maxLines = 1,
                color = MaterialTheme.colors.error,
                style = MaterialTheme.typography.caption
            )
        }
    }
}
