package pachmp.meventer.data.validators

import androidx.core.text.isDigitsOnly
import pachmp.meventer.data.DTO.UserRegister
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class UserValidator {
    val minPasswordLength = 6

    fun emailValidate(email: String): Boolean {
        return Regex("""([a-zA-Z0-9._-]+@[a-zA-Z0-9._-]+\.[a-zA-Z0-9_-]+)""").matches(email) && email.isNotBlank()
    }

    fun nameValidate(name: String): Boolean {
        return name.isNotBlank()
    }

    fun nickValidate(nick: String): Boolean {
        return nick.isNotBlank()
    }

    fun codeValidate(code: String): Boolean {
        return code.isDigitsOnly() && code.isNotBlank()
    }

    fun passwordValidate(password: String): Boolean {
        return password.length >= minPasswordLength
    }

    fun birthdayValidate(birthday: String): Boolean {
        return LocalDate.parse(
            birthday,
            DateTimeFormatter.ofPattern("yyyy-MM-dd")
        ) < LocalDate.now()
    }

    fun birthdayValidate(birthday: LocalDate): Boolean {
        return birthday < LocalDate.now()
    }

    fun userRegisterValidate(userRegister: UserRegister): Boolean {
        return codeValidate(userRegister.code) &&
                passwordValidate(userRegister.password) &&
                nameValidate(userRegister.name) &&
                emailValidate(userRegister.email) &&
                nickValidate(userRegister.nickname) &&
                birthdayValidate(userRegister.dateOfBirth)
    }
}