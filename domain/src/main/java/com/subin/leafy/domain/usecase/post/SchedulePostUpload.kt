package com.subin.leafy.domain.usecase.post

import com.subin.leafy.domain.repository.PostRepository
import javax.inject.Inject

class SchedulePostUpload @Inject constructor(
    private val repository: PostRepository
) {
    operator fun invoke(
        title: String,
        content: String,
        tags: List<String>,
        imageUriStrings: List<String>,
        linkedNoteId: String?,
        linkedTeaType: String?,
        linkedRating: Int?
    ) {
        repository.schedulePostUpload(
            title = title,
            content = content,
            tags = tags,
            imageUriStrings = imageUriStrings,
            linkedNoteId = linkedNoteId,
            linkedTeaType = linkedTeaType,
            linkedRating = linkedRating
        )
    }
}