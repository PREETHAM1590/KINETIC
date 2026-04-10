package com.kinetic.app.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.kinetic.app.data.repository.AuthRepository
import com.kinetic.app.data.repository.AuthRepositoryImpl
import com.kinetic.app.data.repository.DietRepository
import com.kinetic.app.data.repository.FirebaseDietRepository
import com.kinetic.app.data.repository.FirebaseMembershipRepository
import com.kinetic.app.data.repository.FirebaseProfileRepository
import com.kinetic.app.data.repository.FirebaseReportsRepository
import com.kinetic.app.data.repository.FirebaseWorkoutRepository
import com.kinetic.app.data.repository.MembershipRepository
import com.kinetic.app.data.repository.ProfileRepository
import com.kinetic.app.data.repository.ReportsRepository
import com.kinetic.app.data.repository.SettingsRepository
import com.kinetic.app.data.repository.SettingsRepositoryImpl
import com.kinetic.app.data.repository.WorkoutRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun bindWorkoutRepository(impl: FirebaseWorkoutRepository): WorkoutRepository

    @Binds
    @Singleton
    abstract fun bindDietRepository(impl: FirebaseDietRepository): DietRepository

    @Binds
    @Singleton
    abstract fun bindReportsRepository(impl: FirebaseReportsRepository): ReportsRepository

    @Binds
    @Singleton
    abstract fun bindMembershipRepository(impl: FirebaseMembershipRepository): MembershipRepository

    @Binds
    @Singleton
    abstract fun bindProfileRepository(impl: FirebaseProfileRepository): ProfileRepository

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(impl: SettingsRepositoryImpl): SettingsRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    companion object {
        @Provides
        @Singleton
        fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

        @Provides
        @Singleton
        fun provideFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()
    }
}
