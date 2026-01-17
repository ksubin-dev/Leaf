package com.subin.leafy.domain.usecase.post

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.repository.NoteRepository
import com.subin.leafy.domain.repository.PostRepository
import java.util.UUID

class ShareNoteAsPostUseCase(
    private val noteRepository: NoteRepository,
    private val postRepository: PostRepository,
) {
    suspend operator fun invoke(
        noteId: String,
        content: String,
        tags: List<String>,
        imageUrls: List<String>
    ): DataResourceResult<Unit> {

        val noteResult = noteRepository.getNoteDetail(noteId)
        if (noteResult !is DataResourceResult.Success) {
            return DataResourceResult.Failure(Exception("노트 정보를 찾을 수 없습니다."))
        }
        val note = noteResult.data

        val newPostId = UUID.randomUUID().toString()

        return postRepository.createPost(
            postId = newPostId,
            title = "${note.teaInfo.brand} ${note.teaInfo.name}",
            content = content,
            imageUrls = imageUrls,
            teaType = note.teaInfo.type.name,
            rating = note.rating.stars,
            tags = tags,
            brewingSummary = "${note.recipe.waterTemp}℃ · ${note.recipe.brewTimeSeconds}초",
            originNoteId = note.id
        )
    }
}