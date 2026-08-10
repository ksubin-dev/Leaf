package com.subin.leafy.data.datasource.local.impl

import com.google.common.truth.Truth.assertThat
import com.subin.leafy.data.datasource.local.room.dao.UploadQueueDao
import com.subin.leafy.data.datasource.local.room.entity.UploadQueueEntity
import com.subin.leafy.domain.model.UploadQueue
import com.subin.leafy.domain.model.UploadStatus
import com.subin.leafy.domain.model.UploadTargetType
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test

class UploadQueueDataSourceImplTest {

    @Test
    fun `복구 대상 조회는 targetType과 status enum을 Room 문자열 조건으로 변환한다`() = runTest {
        val dao = mockk<UploadQueueDao>()
        val dataSource = UploadQueueDataSourceImpl(dao)
        coEvery {
            dao.getByTargetTypeAndStatuses(
                targetType = UploadTargetType.NOTE.name,
                statuses = listOf(UploadStatus.PENDING.name, UploadStatus.RETRYING.name)
            )
        } returns listOf(uploadQueueEntity(id = "NOTE_note-1", status = UploadStatus.RETRYING))

        val result = dataSource.getByTargetTypeAndStatuses(
            targetType = UploadTargetType.NOTE,
            statuses = listOf(UploadStatus.PENDING, UploadStatus.RETRYING)
        )

        assertThat(result).hasSize(1)
        assertThat(result.single().id).isEqualTo("NOTE_note-1")
        assertThat(result.single().targetType).isEqualTo(UploadTargetType.NOTE)
        assertThat(result.single().status).isEqualTo(UploadStatus.RETRYING)
    }

    @Test
    fun `복구 대상 status가 비어 있으면 Room IN 쿼리를 호출하지 않고 빈 목록을 반환한다`() = runTest {
        val dao = mockk<UploadQueueDao>(relaxed = true)
        val dataSource = UploadQueueDataSourceImpl(dao)

        val result = dataSource.getByTargetTypeAndStatuses(
            targetType = UploadTargetType.COMMUNITY,
            statuses = emptyList()
        )

        assertThat(result).isEmpty()
        coVerify(exactly = 0) { dao.getByTargetTypeAndStatuses(any(), any()) }
    }

    @Test
    fun `상태 업데이트는 enum 상태와 attempt message error를 Room에 저장한다`() = runTest {
        val dao = mockk<UploadQueueDao>()
        val dataSource = UploadQueueDataSourceImpl(dao)
        coEvery {
            dao.updateStatus(
                id = "COMMUNITY_post-1",
                status = UploadStatus.FAILED.name,
                attempt = 3,
                message = "업로드에 실패했어요",
                lastError = "network timeout",
                updatedAt = any()
            )
        } returns Unit

        dataSource.updateStatus(
            id = "COMMUNITY_post-1",
            status = UploadStatus.FAILED,
            attempt = 3,
            message = "업로드에 실패했어요",
            lastError = "network timeout"
        )

        coVerify(exactly = 1) {
            dao.updateStatus(
                id = "COMMUNITY_post-1",
                status = UploadStatus.FAILED.name,
                attempt = 3,
                message = "업로드에 실패했어요",
                lastError = "network timeout",
                updatedAt = any()
            )
        }
    }

    @Test
    fun `업로드 큐 저장은 domain 값을 Room entity로 변환해 upsert한다`() = runTest {
        val dao = mockk<UploadQueueDao>()
        val dataSource = UploadQueueDataSourceImpl(dao)
        val captured = slot<UploadQueueEntity>()
        coEvery { dao.upsert(capture(captured)) } returns Unit

        dataSource.upsert(
            UploadQueue(
                id = "NOTE_note-2",
                targetType = UploadTargetType.NOTE,
                targetId = "note-2",
                status = UploadStatus.PENDING,
                attempt = 1,
                maxAttempts = 3,
                payload = """{"noteId":"note-2"}""",
                message = "백그라운드 업로드 대기 중입니다.",
                lastError = null,
                createdAt = 10L,
                updatedAt = 20L,
                lastNotifiedAt = 30L
            )
        )

        assertThat(captured.captured.id).isEqualTo("NOTE_note-2")
        assertThat(captured.captured.targetType).isEqualTo(UploadTargetType.NOTE.name)
        assertThat(captured.captured.status).isEqualTo(UploadStatus.PENDING.name)
        assertThat(captured.captured.payload).isEqualTo("""{"noteId":"note-2"}""")
        assertThat(captured.captured.lastNotifiedAt).isEqualTo(30L)
    }

    @Test
    fun `최신 표시 상태 관찰은 Room entity를 domain으로 변환한다`() = runTest {
        val dao = mockk<UploadQueueDao>()
        val dataSource = UploadQueueDataSourceImpl(dao)
        every { dao.observeLatestVisible() } returns flowOf(
            uploadQueueEntity(
                id = "COMMUNITY_post-2",
                targetType = UploadTargetType.COMMUNITY,
                targetId = "post-2",
                status = UploadStatus.AUTH_REQUIRED,
                message = "다시 로그인해 주세요"
            )
        )

        val result = dataSource.observeLatestVisible().first()

        assertThat(result?.id).isEqualTo("COMMUNITY_post-2")
        assertThat(result?.targetType).isEqualTo(UploadTargetType.COMMUNITY)
        assertThat(result?.status).isEqualTo(UploadStatus.AUTH_REQUIRED)
        assertThat(result?.message).isEqualTo("다시 로그인해 주세요")
    }

    private fun uploadQueueEntity(
        id: String,
        targetType: UploadTargetType = UploadTargetType.NOTE,
        targetId: String = "note-1",
        status: UploadStatus = UploadStatus.PENDING,
        message: String? = null
    ): UploadQueueEntity {
        return UploadQueueEntity(
            id = id,
            targetType = targetType.name,
            targetId = targetId,
            status = status.name,
            attempt = 0,
            maxAttempts = 3,
            payload = null,
            message = message,
            lastError = null,
            createdAt = 1L,
            updatedAt = 2L,
            lastNotifiedAt = null
        )
    }
}
