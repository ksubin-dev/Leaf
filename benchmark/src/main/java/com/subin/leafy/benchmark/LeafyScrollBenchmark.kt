package com.subin.leafy.benchmark

import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.FrameTimingMetric
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Direction
import androidx.test.uiautomator.Until
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LeafyScrollBenchmark {

    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    private val targetPackage = "com.subin.leafy"

    @Test
    fun scrollFollowingFeed() = benchmarkRule.measureRepeated(
        packageName = targetPackage,
        metrics = listOf(FrameTimingMetric()),
        iterations = 5,
        startupMode = StartupMode.HOT,
        compilationMode = CompilationMode.None(),

        setupBlock = {
            pressHome()
            startActivityAndWait()

            val communityBottomTab = device.wait(Until.findObject(By.res("tab_community")), 5000)
            checkNotNull(communityBottomTab) { "❌ 에러: 'tab_community' 바텀 탭을 못 찾았습니다!" }
            communityBottomTab.click()
            device.waitForIdle()

            val followingTab = device.wait(Until.findObject(By.res("tab_following")), 5000)
            checkNotNull(followingTab) { "❌ 에러: 'tab_following' 상단 탭을 못 찾았습니다!" }
            followingTab.click()
            device.waitForIdle()

            val isListReady = device.wait(Until.hasObject(By.res("following_feed_list")), 5000)
            check(isListReady) { "❌ 에러: 'following_feed_list' 리스트가 나타나지 않음!" }
        }
    ) {
        val feedList = device.findObject(By.res("following_feed_list"))
        checkNotNull(feedList) { "❌ 스크롤할 피드 리스트 객체를 찾을 수 없음!" }

        feedList.setGestureMargin(device.displayHeight / 5)

        feedList.fling(Direction.DOWN)
        device.waitForIdle()

        feedList.fling(Direction.DOWN)
        device.waitForIdle()

        feedList.fling(Direction.UP)
        device.waitForIdle()
    }
}