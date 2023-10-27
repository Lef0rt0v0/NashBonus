package ru.acted.nashbonus

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.fragment.app.Fragment
import ru.acted.nashbonus.databinding.ActivityMainBinding
import ru.acted.nashbonus.fragments.GuideFragment
import ru.acted.nashbonus.fragments.WelcomeFragment
import ru.acted.nashbonus.utils.ActivityFragmentInteractInterface
import ru.acted.nashbonus.utils.AuthManager
import ru.acted.nashbonus.utils.AuthManager.Companion.LoginState.NOT_LOGGED_IN
import ru.acted.nashbonus.utils.AuthManager.Companion.LoginState.LOGGED_IN
import ru.acted.nashbonus.utils.AuthManager.Companion.LoginState.GUEST
import ru.acted.nashbonus.utils.PreferencesKeys
import ru.acted.nashbonus.utils.UniversalFuns.Companion.getSharedPreference
import ru.acted.nashbonus.utils.UniversalFuns.Companion.makeAnimatedFragmentTransaction

class MainActivity : AppCompatActivity(), ActivityFragmentInteractInterface {

    private val mainframeTag = "mainframe"
    private lateinit var binding: ActivityMainBinding
    private val animationInterpolator = DecelerateInterpolator(2f)
    private val animationDuration = 300L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            errorView.setOnClickListener {
                errorContainer.animate().apply {
                    interpolator = animationInterpolator
                    translationY(errorContainer.height.toFloat())
                    duration = animationDuration
                    withEndAction {
                        errorView.visibility = View.GONE
                    }
                    start()
                }
            }
        }

        loadApplication()
    }

    private fun loadApplication() {
        //Логинимся, если не авторизованы
        if (AuthManager.checkUserAuth(this) == NOT_LOGGED_IN) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.mainFrame, WelcomeFragment(), mainframeTag)
                .commit()
            return
        }

        //Показываем гайд, если не показывали
        if (getSharedPreference(PreferencesKeys().IS_GUIDE_COMPLETE, "no") == "no") {
            makeAnimatedFragmentTransaction().replace(R.id.mainFrame, GuideFragment(), mainframeTag).commit()
            return
        }
    }

    override fun onFinishFragment(fragment: Fragment) {
        makeAnimatedFragmentTransaction().remove(fragment).commit()
        when (fragment) {
            is WelcomeFragment -> loadApplication()
        }
    }

    override fun onShowError(text: String) {
        binding.apply {
            errorView.visibility = View.INVISIBLE
            errorText.text = text
            errorView.post {
                binding.apply {
                    errorContainer.translationY = errorContainer.height.toFloat()
                    errorHighlight.translationY = errorContainer.height.toFloat()
                    errorView.visibility = View.VISIBLE

                    errorContainer.animate().apply {
                        interpolator = animationInterpolator
                        translationY(0f)
                        duration = animationDuration
                        start()
                    }

                    errorHighlight.animate().apply {
                        interpolator = animationInterpolator
                        translationY(errorContainer.height.toFloat() * -1)
                        duration = animationDuration * 2
                        start()
                    }
                }
            }
        }
    }
}