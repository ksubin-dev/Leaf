package com.leafy.features.auth.ui.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AuthTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isError: Boolean = false,
    errorMessage: String? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(text = placeholder, color = Color.Gray) },
            modifier = Modifier.fillMaxWidth(),
            isError = isError,
            visualTransformation = visualTransformation,
            shape = RoundedCornerShape(8.dp)
        )

        if (isError && errorMessage != null) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
            )
        }
    }
}