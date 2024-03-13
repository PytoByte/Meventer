package pachmp.meventer.data.repository

import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
data class Repositories @Inject constructor(
    val authRepository: AuthRepository,
    val eventRepository: EventRepository,
    val userRepository: UserRepository,
    val encryptedSharedPreferences: SharedPreferences
)