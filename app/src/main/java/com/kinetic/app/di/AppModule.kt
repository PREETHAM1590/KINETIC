package com.kinetic.app.di

import com.google.firebase.auth.FirebaseAuth
import com.kinetic.app.data.repository.AuthRepository
import com.kinetic.app.data.repository.AuthRepositoryImpl
import com.kinetic.app.data.repository.DietRepository
import com.kinetic.app.data.repository.FakeDietRepository
import com.kinetic.app.data.repository.FakeMembershipRepository
import com.kinetic.app.data.repository.FakeProfileRepository
import com.kinetic.app.data.repository.FakeReportsRepository
import com.kinetic.app.data.repository.FakeWorkoutRepository
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
    abstract fun bindWorkoutRepository(impl: FakeWorkoutRepository): WorkoutRepository

    @Binds
    @Singleton
    abstract fun bindDietRepository(impl: FakeDietRepository): DietRepository

    @Binds
    @Singleton
    abstract fun bindReportsRepository(impl: FakeReportsRepository): ReportsRepository

    @Binds
    @Singleton
    abstract fun bindMembershipRepository(impl: FakeMembershipRepository): MembershipRepository

    @Binds
    @Singleton
    abstract fun bindProfileRepository(impl: FakeProfileRepository): ProfileRepository

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
    }
}
