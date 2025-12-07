package com.leafy.features.note.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.text.font.FontWeight
import com.leafy.shared.ui.theme.LeafyTheme

@Composable
fun LeafyFieldLabel(text: String) {
    val colors = MaterialTheme.colorScheme

    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall.copy(
            fontWeight = FontWeight.Medium
        ),
        color = colors.primary
    )
}

@Preview(showBackground = true)
@Composable
private fun LeafyFieldLabelPreview() {
    LeafyTheme {
        LeafyFieldLabel(text = "Example Field Label")
    }
}