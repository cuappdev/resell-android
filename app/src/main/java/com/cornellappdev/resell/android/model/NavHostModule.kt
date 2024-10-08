package com.cornellappdev.resell.android.model

import android.content.Context
import androidx.navigation.NavHostController
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.DialogNavigator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RootNav

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class MainNav

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class OnboardingNav

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class NewPostNav

@Module
@InstallIn(SingletonComponent::class)
object NavHostModule {

    @Provides
    @RootNav
    @Singleton
    fun provideRootNavController(@ApplicationContext context: Context) =
        NavHostController(context).apply {
            navigatorProvider.addNavigator(ComposeNavigator())
            navigatorProvider.addNavigator(DialogNavigator())
        }

    @Provides
    @MainNav
    @Singleton
    fun provideMainNavController(@ApplicationContext context: Context) =
        NavHostController(context).apply {
            navigatorProvider.addNavigator(ComposeNavigator())
            navigatorProvider.addNavigator(DialogNavigator())
        }

    @Provides
    @OnboardingNav
    @Singleton
    fun provideOnboardingNavController(@ApplicationContext context: Context) =
        NavHostController(context).apply {
            navigatorProvider.addNavigator(ComposeNavigator())
            navigatorProvider.addNavigator(DialogNavigator())
        }

    @Provides
    @NewPostNav
    @Singleton
    fun provideNewPostNavController(@ApplicationContext context: Context) =
        NavHostController(context).apply {
            navigatorProvider.addNavigator(ComposeNavigator())
            navigatorProvider.addNavigator(DialogNavigator())
        }

}
