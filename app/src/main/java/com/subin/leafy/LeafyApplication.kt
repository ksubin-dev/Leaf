package com.subin.leafy

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.firestoreSettings
import com.google.firebase.firestore.persistentCacheSettings
import com.leafy.shared.di.ApplicationContainer
import com.leafy.shared.di.ApplicationContainerProvider
import com.subin.leafy.di.ApplicationContainerImpl

class LeafyApplication : Application(), ApplicationContainerProvider, ImageLoaderFactory {

    private lateinit var appContainer: ApplicationContainer

    override fun onCreate() {
        super.onCreate()
        leafyApplication = this

        setUpFirestoreCache()
        // 2. ★ 핵심 수정: 여기서 'this' (Context)를 넘겨줍니다!
        // 아까 AppContainerImpl 생성자에 Context를 추가했기 때문에 여기서 에러가 날 텐데,
        // 이렇게 (this)를 넣어주면 해결됩니다.
        appContainer = ApplicationContainerImpl(this)
    }

    override fun provideAppContainer(): ApplicationContainer {
        return appContainer
    }

    // 3. Coil 전역 설정 구현 (newImageLoader)
    // 이렇게 해두면 앱 어디서든 AsyncImage를 쓸 때 이 설정이 자동 적용됩니다.
    // ★ [수정됨] Coil 전역 설정
    override fun newImageLoader(): ImageLoader {
        // ImageLoader.Builder(context) 패턴을 사용해야 합니다.
        return ImageLoader.Builder(this)
            .crossfade(true) // 페이드 인 애니메이션
            // .placeholder(R.drawable.placeholder) // 필요하면 기본 이미지 설정 가능
            // .error(R.drawable.error) // 필요하면 에러 이미지 설정 가능
            .build()
    }

    private fun setUpFirestoreCache() {
        try {
            val settings = firestoreSettings {
                setLocalCacheSettings(persistentCacheSettings {
                    // 캐시 크기 100MB로 설정 (너무 크면 용량 차지하니까 적당히)
                    setSizeBytes(100L * 1024 * 1024)
                })
            }
            Firebase.firestore.firestoreSettings = settings
        } catch (e: Exception) {
            // 이미 초기화되었거나 에러 발생 시 무시
        }
    }

    companion object {
        private lateinit var leafyApplication: LeafyApplication
        fun getAppContext() = leafyApplication
    }
}