package com.leafy.features.community.presentation.screen.write

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.leafy.features.community.presentation.screen.write.components.LinkedNoteCard
import com.leafy.features.community.presentation.screen.write.components.NoteSelectionSheet
import com.leafy.features.community.presentation.common.model.NoteSelectionUiModel
import com.leafy.features.community.presentation.screen.write.section.PostImageSection
import com.leafy.features.community.presentation.screen.write.section.TagInputSection
import com.leafy.shared.R
import com.leafy.shared.common.singleClick
import com.leafy.shared.ui.component.LoadingOverlay
import com.leafy.shared.ui.theme.LeafyTheme // 테마가 있다면 import


@Composable
fun CommunityWriteRoute(
    viewModel: CommunityWriteViewModel,
    onNavigateBack: () -> Unit,
    onPostSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    CommunityWriteContent(
        uiState = uiState,
        myNotes = uiState.myNotes,
        onNavigateBack = onNavigateBack,
        onPostSuccess = onPostSuccess,
        onEvent = { event ->
            when (event) {
                is CommunityWriteEvent.Upload -> viewModel.uploadPost()
                is CommunityWriteEvent.AddImages -> viewModel.addImages(event.uris)
                is CommunityWriteEvent.RemoveImage -> viewModel.removeImage(event.uri)
                is CommunityWriteEvent.UpdateTitle -> viewModel.updateTitle(event.text)
                is CommunityWriteEvent.UpdateContent -> viewModel.updateContent(event.text)
                is CommunityWriteEvent.UpdateTagInput -> viewModel.updateTagInput(event.text)
                is CommunityWriteEvent.RemoveTag -> viewModel.removeTag(event.tag)
                is CommunityWriteEvent.SelectNote -> viewModel.onNoteSelected(event.noteId)
                is CommunityWriteEvent.ClearNote -> viewModel.clearLinkedNote()
                is CommunityWriteEvent.ErrorMessageShown -> viewModel.userMessageShown()
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityWriteContent(
    uiState: CommunityWriteUiState,
    myNotes: List<NoteSelectionUiModel>,
    onNavigateBack: () -> Unit,
    onPostSuccess: () -> Unit,
    onEvent: (CommunityWriteEvent) -> Unit
) {
    val scrollState = rememberScrollState()
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(5),
        onResult = { uris -> onEvent(CommunityWriteEvent.AddImages(uris)) }
    )

    LaunchedEffect(uiState.isPostSuccess) {
        if (uiState.isPostSuccess) onPostSuccess()
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            onEvent(CommunityWriteEvent.ErrorMessageShown)
        }
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            NoteSelectionSheet(
                notes = myNotes,
                onNoteClick = { noteId ->
                    onEvent(CommunityWriteEvent.SelectNote(noteId))
                    showBottomSheet = false
                },
                onDismissRequest = { showBottomSheet = false }
            )
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets.statusBars,

        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("새 게시물", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = singleClick { onNavigateBack() }) {
                        Icon(Icons.Rounded.Close, contentDescription = "닫기")
                    }
                },
                actions = {
                    Button(
                        onClick = singleClick { onEvent(CommunityWriteEvent.Upload) },
                        enabled = uiState.isPostValid && !uiState.isLoading,
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = Color.White,
                            disabledContainerColor = MaterialTheme.colorScheme.primary,
                            disabledContentColor = Color.White
                        ),
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .height(32.dp)
                    ) {
                        Text("공유", fontWeight = FontWeight.Bold)
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues).imePadding()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            PostImageSection(
                selectedUris = uiState.selectedImageUris,
                onAddImage = {
                    photoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
                onRemoveImage = { uri -> onEvent(CommunityWriteEvent.RemoveImage(uri)) }
            )

            Box(modifier = Modifier.padding(16.dp)) {
                if (uiState.linkedNoteId == null) {
                    OutlinedButton(
                        onClick = singleClick { showBottomSheet = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_nav_note),
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("작성한 시음 노트 불러오기")
                    }
                } else {
                    LinkedNoteCard(
                        title = uiState.linkedNoteTitle ?: "",
                        teaType = uiState.linkedTeaType ?: "",
                        date = uiState.linkedDate ?: "",
                        thumbnailUri = uiState.linkedThumbnailUri,
                        rating = uiState.linkedRating,
                        onClear = { onEvent(CommunityWriteEvent.ClearNote) }
                    )
                }
            }

            HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)

            Column(modifier = Modifier.padding(16.dp)) {
                BasicTextField(
                    value = uiState.title,
                    onValueChange = { if (it.length <= 50) onEvent(CommunityWriteEvent.UpdateTitle(it)) },
                    textStyle = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    decorationBox = { innerTextField ->
                        if (uiState.title.isEmpty()) {
                            Text(
                                "제목을 입력하세요",
                                style = MaterialTheme.typography.titleLarge.copy(color = Color.LightGray)
                            )
                        }
                        innerTextField()
                    }
                )

                Spacer(Modifier.height(16.dp))

                BasicTextField(
                    value = uiState.content,
                    onValueChange = { onEvent(CommunityWriteEvent.UpdateContent(it)) },
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier.heightIn(min = 150.dp),
                    decorationBox = { innerTextField ->
                        if (uiState.content.isEmpty()) {
                            Text(
                                "어떤 맛과 향을 느끼셨나요? 자유롭게 기록해주세요.",
                                style = MaterialTheme.typography.bodyLarge.copy(color = Color.LightGray)
                            )
                        }
                        innerTextField()
                    }
                )
            }

            TagInputSection(
                tags = uiState.tags,
                currentInput = uiState.currentTagInput,
                onValueChange = { onEvent(CommunityWriteEvent.UpdateTagInput(it)) },
                onRemoveTag = { onEvent(CommunityWriteEvent.RemoveTag(it)) }
            )

            Spacer(Modifier.height(50.dp))
        }
            LoadingOverlay(
                isLoading = uiState.isLoading,
                message = "게시글을 업로드 중입니다..."
            )
        }
    }
}

sealed interface CommunityWriteEvent {
    object Upload : CommunityWriteEvent
    data class AddImages(val uris: List<Uri>) : CommunityWriteEvent
    data class RemoveImage(val uri: Uri) : CommunityWriteEvent
    data class UpdateTitle(val text: String) : CommunityWriteEvent
    data class UpdateContent(val text: String) : CommunityWriteEvent
    data class UpdateTagInput(val text: String) : CommunityWriteEvent
    data class RemoveTag(val tag: String) : CommunityWriteEvent
    data class SelectNote(val noteId: String) : CommunityWriteEvent
    object ClearNote : CommunityWriteEvent
    object ErrorMessageShown : CommunityWriteEvent
}

@Preview(showBackground = true)
@Composable
fun CommunityWriteScreenPreview() {
    val dummyState = CommunityWriteUiState(
        title = "오늘 마신 녹차",
        content = "향이 정말 좋네요. 추천합니다!",
        tags = listOf("#녹차", "#티타임"),
        linkedNoteId = "123",
        linkedNoteTitle = "오설록 세작",
        linkedTeaType = "녹차",
        linkedDate = "2024.01.14",
        linkedRating = 5
    )

    val dummyNotes = listOf(
        NoteSelectionUiModel("1", "세작", "녹차", "2024.01.01", 5),
        NoteSelectionUiModel("2", "우전", "녹차", "2024.01.02", 4)
    )

    LeafyTheme {
        CommunityWriteContent(
            uiState = dummyState,
            myNotes = dummyNotes,
            onNavigateBack = {},
            onPostSuccess = {},
            onEvent = {}
        )
    }
}