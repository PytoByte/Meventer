package pachmp.meventer

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Provides
    @Singleton
    fun encryptedSharedPreferencesProvider(@ApplicationContext appContext: Context): SharedPreferences {
        return appContext.getSharedPreferences("token", MODE_PRIVATE)
    }

    @Nav
    @Provides
    @Singleton
    fun navigatorProvider() = Navigator()

    @RootNav
    @Provides
    @Singleton
    fun rootNavigatorProvider() = Navigator()
}