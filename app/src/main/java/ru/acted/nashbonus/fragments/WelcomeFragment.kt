package ru.acted.nashbonus.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import ru.acted.nashbonus.databinding.ScreenWelcomeBinding
import ru.acted.nashbonus.utils.AuthManager
import ru.acted.nashbonus.utils.MainFrameFragment
import ru.acted.nashbonus.utils.UniversalFuns.Companion.finishWork
import ru.acted.nashbonus.utils.UniversalFuns.Companion.makeClickableText
import ru.acted.nashbonus.utils.UniversalFuns.Companion.phoneRegex
import ru.acted.nashbonus.utils.UniversalFuns.Companion.showError

class WelcomeFragment: MainFrameFragment() {
    private lateinit var binding: ScreenWelcomeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ScreenWelcomeBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            phoneEdit.addTextChangedListener {
                it?.let {
                    if (!it.matches(phoneRegex)) {
                        phoneEdit.error = "Неверный формат номера телефона"
                    } else {
                        phoneEdit.error = null
                    }
                }
            }

            getCodeButton.setOnClickAction {
                showError("В настоящее время сервис недоступен. Пожалуйста, проверьте интернет-соединение или попробуйте позже")
            }

            tosCheck.makeClickableText("Я принимаю Условия использования", "Условия использования") {
                tosCheck.isChecked = !tosCheck.isChecked
                showError("Не удалось загрузить Условия использования. Пожалуйста, хватит пытаться найти проблемы в нашем идеальном приложении")
            }

            guestLogin.makeClickableText("Гостевой вход") {
                AuthManager.authorizeUser(requireContext(), "guest")
                finishWork()
            }
        }
    }

}