/*
 * Copyright (C) 2020-2021 Sandip Vaghela
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.afterroot.ui.common.compose.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.afterroot.watchdone.utils.State

/**
 * [TextField] with Validation
 */
@OptIn(ExperimentalComposeUiApi::class)
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
    onError: (String) -> Unit = {},
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
            keyboardActions = keyboardActions,
        )
        AnimatedVisibility(visible = error) {
            Text(
                text = errorText,
                modifier = Modifier
                    .paddingFromBaseline(top = 16.dp)
                    .padding(horizontal = (16.dp * 2)),
                maxLines = 1,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

/**
 * [OutlinedTextField] with Validation
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun OutlinedTextInput(
    // Default Parameters
    modifier: Modifier = Modifier,
    onValueChange: ((String) -> Unit)? = null,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = LocalTextStyle.current,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    prefix: @Composable (() -> Unit)? = null,
    suffix: @Composable (() -> Unit)? = null,
    supportingText: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    singleLine: Boolean = false,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    shape: Shape = OutlinedTextFieldDefaults.shape,
    colors: TextFieldColors = OutlinedTextFieldDefaults.colors(),
    // Added Parameters
    labelText: String? = null,
    hint: String = "",
    prefillValue: String = "",
    keyboardController: SoftwareKeyboardController? = LocalSoftwareKeyboardController.current,
    keyboardOptions: KeyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
    keyboardActions: KeyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
    validate: (String) -> State<Boolean> = { State.success(true) },
    onChange: (String) -> Unit,
    onError: (String) -> Unit = {},
) {
    var value by rememberSaveable { mutableStateOf(prefillValue) }
    LocalLogger.current.d {
        "OutlinedTextInput $prefillValue $value"
    }
    var error by rememberSaveable { mutableStateOf(false) }
    var errorText by rememberSaveable { mutableStateOf("") }
    Column {
        OutlinedTextField(
            enabled = enabled,
            readOnly = readOnly,
            textStyle = textStyle,
            prefix = prefix,
            suffix = suffix,
            supportingText = supportingText,
            minLines = minLines,
            visualTransformation = visualTransformation,
            interactionSource = interactionSource,
            shape = shape,
            colors = colors,
            value = value,
            maxLines = maxLines,
            singleLine = singleLine,
            modifier = modifier,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            leadingIcon = leadingIcon,
            placeholder = placeholder ?: { Text(text = hint) },
            onValueChange = onValueChange ?: {
                value = it // always update state
                when (val validation = validate(it)) {
                    is State.Failed -> {
                        error = true
                        errorText = validation.message
                        onError(it)
                    }

                    is State.Success -> {
                        if (validation.data || it.isBlank()) {
                            error = false
                            errorText = ""
                            onChange(it)
                        }
                    }

                    is State.Loading -> {} // NOTHING
                }
            },
            isError = error,
            label = {
                if (labelText != null) {
                    Text(text = labelText)
                } else {
                    label?.invoke()
                }
            },
            trailingIcon = trailingIcon ?: {
                if (value.isNotBlank()) {
                    IconButton(onClick = {
                        value = ""
                    }) {
                        Icon(imageVector = Icons.Rounded.Clear, contentDescription = "Clear")
                    }
                }
            },
        )
        AnimatedVisibility(visible = error) {
            Text(
                text = errorText,
                modifier = Modifier
                    .paddingFromBaseline(top = 16.dp)
                    .padding(horizontal = (16.dp * 2)),
                maxLines = 1,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}
