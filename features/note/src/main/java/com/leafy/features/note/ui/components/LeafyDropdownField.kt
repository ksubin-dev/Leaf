package com.leafy.features.note.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.material3.ExposedDropdownMenuAnchorType.Companion.PrimaryNotEditable
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.leafy.shared.ui.theme.LeafyTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeafyDropdownField(
    modifier: Modifier = Modifier,
    label: String,
    options: List<String>,
    selected: String,
    onSelectedChange: (String) -> Unit
) {

    val colors = MaterialTheme.colorScheme
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        LeafyFieldLabel(text = label)

        Spacer(modifier = Modifier.height(4.dp))

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(PrimaryNotEditable, true),
                value = selected,
                onValueChange = { /* readOnly */ },
                readOnly = true,
                singleLine = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colors.primary,
                    cursorColor = colors.primary,
                    focusedTextColor = colors.onSurface,
                    unfocusedTextColor = colors.onSurface,
                    focusedContainerColor = colors.surface,
                    unfocusedContainerColor = colors.surface
                )
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onSelectedChange(option)
                            expanded = false
                        },
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LeafyDropdownFieldPreview() {
    LeafyTheme {
        LeafyDropdownField(
            label = "Tea Type",
            options = listOf("Black", "Green", "Oolong"),
            selected = "Black",
            onSelectedChange = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}