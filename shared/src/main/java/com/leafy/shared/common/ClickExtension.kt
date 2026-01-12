package com.leafy.shared.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.semantics.Role

private const val DEFAULT_DEBOUNCE_TIME = 500L

/**
 * [방법 1] Modifier용 확장 함수
 * Box, Text, Image, Row 등을 클릭할 때 사용합니다.
 * 내부적으로 0.5초(기본값) 동안의 중복 클릭을 무시합니다.
 */
fun Modifier.clickableSingle(
    enabled: Boolean = true,
    onClickLabel: String? = null,
    role: Role? = null,
    debounceTime: Long = DEFAULT_DEBOUNCE_TIME,
    onClick: () -> Unit
): Modifier = composed(
    inspectorInfo = debugInspectorInfo {
        name = "clickableSingle"
        properties["enabled"] = enabled
        properties["onClickLabel"] = onClickLabel
        properties["role"] = role
        properties["debounceTime"] = debounceTime
        properties["onClick"] = onClick
    }
) {
    val multipleEventsCutter = remember { MultipleEventsCutter.get(debounceTime) }
    Modifier.clickable(
        enabled = enabled,
        onClickLabel = onClickLabel,
        onClick = { multipleEventsCutter.processEvent { onClick() } },
        role = role,
        indication = androidx.compose.foundation.LocalIndication.current,
        interactionSource = remember { MutableInteractionSource() }
    )
}

/**
 * [방법 2] 람다 래퍼용 Composable 함수
 * Button, IconButton, FloatingActionButton 처럼 'onClick' 파라미터가 따로 있는 경우 사용합니다.
 * * 사용법:
 * Button(onClick = singleClick { viewModel.save() }) { ... }
 */
@Composable
fun singleClick(
    debounceTime: Long = DEFAULT_DEBOUNCE_TIME,
    onClick: () -> Unit
): () -> Unit {
    val multipleEventsCutter = remember { MultipleEventsCutter.get(debounceTime) }
    return { multipleEventsCutter.processEvent { onClick() } }
}

// --- 내부 로직 처리 클래스 ---
private class MultipleEventsCutter internal constructor(
    private val delay: Long
) {
    private val now: Long
        get() = System.currentTimeMillis()

    private var lastEventTimeMs: Long = 0

    fun processEvent(event: () -> Unit) {
        if (now - lastEventTimeMs >= delay) {
            event.invoke()
        }
        lastEventTimeMs = now
    }

    companion object {
        fun get(delay: Long): MultipleEventsCutter = MultipleEventsCutter(delay)
    }
}