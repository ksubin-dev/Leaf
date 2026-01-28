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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.leafy.features.community.presentation.common.model.NoteSelectionUiModel
import com.leafy.features.community.presentation.screen.write.components.LinkedNoteCard
import com.leafy.features.community.presentation.screen.write.components.NoteSelectionSheet
import com.leafy.features.community.presentation.screen.write.section.PostImageSection
import com.leafy.features.community.presentation.screen.write.section.TagInputSection
import com.leafy.shared.R
import com.leafy.shared.common.singleClick
import com.leafy.shared.ui.component.LoadingOverlay
import com.leafy.shared.ui.theme.LeafyTheme

@Composable
fun CommunityWriteRoute(
    onNavigateBack: () -> Unit,
    onPostSuccess: () -> Unit,
    viewModel: CommunityWriteViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.sideEffect.collect { effect ->
            when (effect) {
                is CommunityWriteSideEffect.PostSuccess -> {
                    onPostSuccess()
                }
                is CommunityWriteSideEffect.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(effect.message.asString(context))
                }
            }
        }
    }

    CommunityWriteContent(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        myNotes = uiState.myNotes,
        onNavigateBack = onNavigateBack,
        onUpload = viewModel::uploadPost,
        onAddImages = viewModel::addImages,
        onRemoveImage = viewModel::removeImage,
        onUpdateTitle = viewModel::updateTitle,
        onUpdateContent = viewModel::updateContent,
        onUpdateTagInput = viewModel::updateTagInput,
        onRemoveTag = viewModel::removeTag,
        onSelectNote = viewModel::onNoteSelected,
        onClearNote = viewModel::clearLinkedNote
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityWriteContent(
    uiState: CommunityWriteUiState,
    snackbarHostState: SnackbarHostState,
    myNotes: List<NoteSelectionUiModel>,
    onNavigateBack: () -> Unit,
    onUpload: () -> Unit,
    onAddImages: (List<Uri>) -> Unit,
    onRemoveImage: (Uri) -> Unit,
    onUpdateTitle: (String) -> Unit,
    onUpdateContent: (String) -> Unit,
    onUpdateTagInput: (String) -> Unit,
    onRemoveTag: (String) -> Unit,
    onSelectNote: (String) -> Unit,
    onClearNote: () -> Unit
) {
    val scrollState = rememberScrollState()
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(5),
        onResult = { uris -> if (uris.isNotEmpty()) onAddImages(uris) }
    )

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            NoteSelectionSheet(
                notes = myNotes,
                onNoteClick = { noteId ->
                    onSelectNote(noteId)
                    showBottomSheet = false
                },
                onDismissRequest = { showBottomSheet = false }
            )
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets.statusBars,
        snackbarHost = { SnackbarHost(snackbarHostState) },
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
                        onClick = singleClick { onUpload() },
                        enabled = uiState.isPostValid && !uiState.isLoading,
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = Color.White,
                            disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                            disabledContentColor = Color.White.copy(alpha = 0.5f)
                        ),
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .height(32.dp)
                    ) {
                        Text("공유", fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .imePadding()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
            ) {
                PostImageSection(
                    selectedUris = uiState.selectedImageUris,
                    onAddImage = singleClick {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    onRemoveImage = onRemoveImage
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
                            onClear = onClearNote
                        )
                    }
                }

                HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)

                Column(modifier = Modifier.padding(16.dp)) {
                    BasicTextField(
                        value = uiState.title,
                        onValueChange = { if (it.length <= 50) onUpdateTitle(it) },
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
                        onValueChange = onUpdateContent,
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
                    onValueChange = onUpdateTagInput,
                    onRemoveTag = onRemoveTag
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
            snackbarHostState = remember { SnackbarHostState() },
            myNotes = dummyNotes,
            onNavigateBack = {},
            onUpload = {},
            onAddImages = {},
            onRemoveImage = {},
            onUpdateTitle = {},
            onUpdateContent = {},
            onUpdateTagInput = {},
            onRemoveTag = {},
            onSelectNote = {},
            onClearNote = {}
        )
    }
}