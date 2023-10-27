package ru.acted.nashbonus.utils

import android.content.Context
import ru.acted.nashbonus.utils.UniversalFuns.Companion.getSharedPreference
import ru.acted.nashbonus.utils.UniversalFuns.Companion.setSharedPreference

class AuthManager {
    companion object {
        enum class LoginState {
            NOT_LOGGED_IN,
            GUEST,
            LOGGED_IN
        }

        //TODO Реализовать функции для авторизации
        fun checkUserAuth(context: Context): LoginState {
            return when (context.getSharedPreference(PreferencesKeys().TOKEN, "none")) {
                "none" -> LoginState.NOT_LOGGED_IN
                "guest" -> LoginState.GUEST
                else -> LoginState.LOGGED_IN
            }
        }

        fun authorizeUser(context: Context, token: String) {
            context.setSharedPreference(PreferencesKeys().TOKEN, token)
        }
    }
}