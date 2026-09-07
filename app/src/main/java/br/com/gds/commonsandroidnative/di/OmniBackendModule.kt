package br.com.gds.commonsandroidnative.di

import br.wgc.omnibackend.core.repository.AnalyticsRepository
import br.wgc.omnibackend.core.repository.AuthRepository
import br.wgc.omnibackend.core.repository.FirestoreRepository
import br.wgc.omnibackend.core.repository.RealtimeDatabaseRepository
import br.wgc.omnibackend.core.repository.RemoteConfigRepository
import br.wgc.omnibackend.core.repository.StorageRepository
import br.wgc.omnibackend.core.repository.realtime.GeolocationRepository
import br.wgc.omnibackend.core.repository.realtime.MessageRepository
import br.wgc.omnibackend.core.repository.realtime.PresenceRepository
import br.wgc.omnibackend.firebase.OmniFirebase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object OmniBackendModule {

    @Provides
    @Singleton
    fun provideAuthRepository(): AuthRepository = OmniFirebase.auth

    @Provides
    @Singleton
    fun provideFirestoreRepository(): FirestoreRepository = OmniFirebase.firestore

    @Provides
    @Singleton
    fun provideStorageRepository(): StorageRepository = OmniFirebase.storage

    @Provides
    @Singleton
    fun provideRealtimeDatabaseRepository(): RealtimeDatabaseRepository = OmniFirebase.realtimeDatabase

    @Provides
    @Singleton
    fun provideMessageRepository(): MessageRepository = OmniFirebase.message

    @Provides
    @Singleton
    fun provideGeolocationRepository(): GeolocationRepository = OmniFirebase.geolocation

    @Provides
    @Singleton
    fun providePresenceRepository(): PresenceRepository = OmniFirebase.presence

    @Provides
    @Singleton
    fun provideRemoteConfigRepository(): RemoteConfigRepository = OmniFirebase.remoteConfig

    @Provides
    @Singleton
    fun provideAnalyticsRepository(): AnalyticsRepository = OmniFirebase.analytics
}
