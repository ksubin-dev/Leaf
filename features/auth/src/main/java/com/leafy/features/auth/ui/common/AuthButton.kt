package com.leafy.features.auth.ui.common

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.leafy.shared.ui.theme.LeafyTheme

@Composable
fun AuthButton(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = modifier.fillMaxWidth().height(56.dp),
        shape = RoundedCornerShape(28.dp),
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary
        )
    ) {
        Text(text, fontSize = 18.sp, fontWeight = FontWeight.Bold)
    }
}

@Preview(showBackground = true)
@Composable
fun AuthButtonPreview() {
    LeafyTheme {
        AuthButton(
            text = "로그인",
            onClick = {},
            enabled = true
        )
    }
}