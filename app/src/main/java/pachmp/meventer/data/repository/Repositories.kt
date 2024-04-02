package pachmp.meventer.data.repository

import android.content.Context
import android.content.SharedPreferences
import android.content.res.AssetManager
import androidx.security.crypto.EncryptedSharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
data class Repositories @Inject constructor(
    val eventRepository: EventRepository,
    val userRepository: UserRepository,
    val fileRepository: FileRepository,
    val chatRepository: ChatRepository,
    val chatSocketRepository: ChatSocketRepository,
    val encryptedSharedPreferences: SharedPreferences,
    @ApplicationContext val appContext: Context
)