package pro.udeedit.devtools.pushestest.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import pro.udeedit.devtools.pushestest.domain.usecase.PublishNotificationUseCase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providePublishNotificationUseCase(
        @ApplicationContext context: Context

    ): PublishNotificationUseCase {

        return PublishNotificationUseCase(context)
    }
}
