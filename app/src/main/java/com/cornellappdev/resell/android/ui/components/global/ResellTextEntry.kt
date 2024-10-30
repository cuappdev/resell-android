package com.cornellappdev.resell.android.ui.components.global

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.cornellappdev.resell.android.ui.theme.AppDev
import com.cornellappdev.resell.android.ui.theme.Style
import com.cornellappdev.resell.android.ui.theme.Wash

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResellTextEntry(
    label: String? = null,
    text: String,
    onTextChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    inlineLabel: Boolean = true,
    singleLine: Boolean = true,
    maxLines: Int = 1,
    placeholder: String = "",
    multiLineHeight: Dp = 85.dp,
    leadingIcon: @Composable (() -> Unit)? = null,
) {

    val singleLineModifier = if (singleLine) {
        Modifier
            .height(41.dp)
    } else {
        Modifier
            .height(multiLineHeight)
    }

    val textField = @Composable { modifier: Modifier ->

        val interactionSource = remember { MutableInteractionSource() }
        BasicTextField(
            value = text,
            onValueChange = onTextChange,
            modifier = modifier
                .then(singleLineModifier)
                .fillMaxWidth(),
            interactionSource = interactionSource,
            singleLine = singleLine,
            maxLines = maxLines,
            textStyle = Style.body1,
        ) { innerTextField ->
            TextFieldDefaults.DecorationBox(
                value = text,
                innerTextField = innerTextField,
                singleLine = singleLine,
                interactionSource = interactionSource,
                contentPadding = PaddingValues(vertical = 10.dp, horizontal = 15.dp),
                enabled = true,
                visualTransformation = VisualTransformation.None,
                shape = RoundedCornerShape(10.dp),
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    disabledTextColor = Color.Black,
                    errorTextColor = Color.Black,
                    focusedContainerColor = Wash,
                    unfocusedContainerColor = Wash,
                    disabledContainerColor = Wash,
                    errorContainerColor = Wash,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    errorIndicatorColor = Color.Transparent
                ),
                placeholder = {
                    Text(
                        text = placeholder,
                        style = Style.body1,
                        color = AppDev,
                    )
                },
                leadingIcon = leadingIcon,
            )
        }
    }

    if (inlineLabel) {
        Row(
            modifier = modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (label != null) {
                Text(
                    text = label,
                    style = Style.title1,
                    modifier = Modifier.then(
                        if (!singleLine) {
                            Modifier
                                .align(Alignment.Top)
                                .padding(top = 6.dp)
                        } else {
                            Modifier
                        }
                    )
                )

                Spacer(
                    modifier = Modifier.weight(
                        if (singleLine) 0.5f else 0.1f
                    )
                )
            }

            textField(
                Modifier.weight(1f)
            )
        }
    } else {
        Column(
            modifier = modifier
                .fillMaxWidth()
        ) {
            if (label != null) {
                Text(
                    text = label,
                    style = Style.title1,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            textField(
                Modifier
            )
        }
    }
}

@Preview
@Composable
private fun TextEntryPreview() {
    var multiLineText by remember {
        mutableStateOf("AAAAAAAAAAAAAAHELPHELPAAAAAAAAAAAA")
    }
    Column(
        modifier = Modifier
            .padding(24.dp)
            .background(
                color = Color.White
            )
    ) {
        ResellTextEntry(
            label = "Email",
            text = "",
            onTextChange = {},
            placeholder = "Email goes here"
        )

        Spacer(modifier = Modifier.height(24.dp))

        ResellTextEntry(
            label = "Email",
            text = "Typed@test.com",
            onTextChange = {},
            placeholder = "Email goes here"
        )

        Spacer(modifier = Modifier.height(24.dp))

        ResellTextEntry(
            label = "Email",
            text = "Typedtooverfloooooow@test.com",
            onTextChange = {},
            placeholder = "Email goes here"
        )

        Spacer(modifier = Modifier.height(24.dp))

        ResellTextEntry(
            label = "Bio",
            text = "",
            onTextChange = {},
            inlineLabel = false,
            singleLine = false,
            placeholder = "Bio goes here",
        )

        Spacer(modifier = Modifier.height(24.dp))

        ResellTextEntry(
            label = "Bio inline",
            text = "",
            onTextChange = {},
            inlineLabel = true,
            singleLine = false,
            placeholder = "Bio goes here",
        )

        Spacer(modifier = Modifier.height(24.dp))

        ResellTextEntry(
            label = "Username",
            text = "",
            onTextChange = {},
            inlineLabel = false,
        )

        Spacer(modifier = Modifier.height(24.dp))

        ResellTextEntry(
            label = "Bio (Multiline)",
            text = multiLineText,
            onTextChange = {
                multiLineText = it
            },
            inlineLabel = false,
            singleLine = false,
            maxLines = 3,
        )
    }
}
