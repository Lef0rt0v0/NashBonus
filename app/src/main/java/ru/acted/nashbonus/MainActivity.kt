package ru.acted.nashbonus

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.runBlocking
import ru.acted.nashbonus.adapters.CardsAdapter
import ru.acted.nashbonus.databinding.ActivityMainBinding
import ru.acted.nashbonus.fragments.CardFormFragment
import ru.acted.nashbonus.fragments.GuideFragment
import ru.acted.nashbonus.fragments.WelcomeFragment
import ru.acted.nashbonus.utils.ActivityFragmentInteractInterface
import ru.acted.nashbonus.utils.AuthManager
import ru.acted.nashbonus.utils.AuthManager.Companion.LoginState.NOT_LOGGED_IN
import ru.acted.nashbonus.utils.Card
import ru.acted.nashbonus.utils.CardManager
import ru.acted.nashbonus.utils.PreferencesKeys
import ru.acted.nashbonus.utils.UniversalFuns.Companion.getSharedPreference
import ru.acted.nashbonus.utils.UniversalFuns.Companion.makeAnimatedFragmentTransaction

class MainActivity : AppCompatActivity(), ActivityFragmentInteractInterface {

    private val mainframeTag = "mainframe"

    private lateinit var binding: ActivityMainBinding
    private lateinit var cardManager: CardManager
    private lateinit var cardListAdapter: CardsAdapter
    private var cardFormFragment: CardFormFragment? = null

    //Важные глобальные поля
    private var cardItems = listOf<Card>()
    private val animationInterpolator = DecelerateInterpolator(2f)
    private val animationDuration = 300L

    private fun openCardEditPage(_cardFormFragment: CardFormFragment) = with (binding) {
        popupTint.visibility = View.VISIBLE
        cardFormFragment = _cardFormFragment
        popupContainer.visibility = View.INVISIBLE
        supportFragmentManager.beginTransaction().replace(R.id.popupFrame, cardFormFragment!!).commit()
        popupContainer.post {
            popupContainer.translationY = popupContainer.height.toFloat()
            popupContainer.visibility = View.VISIBLE
            popupContainer.animate().apply {
                interpolator = DecelerateInterpolator(2f)
                duration = 350
                translationY(0f)
                start()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cardManager = CardManager(this)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            newCardButton.setOnClickAction {
                openCardEditPage(CardFormFragment())
            }
            cardListAdapter = CardsAdapter(mutableListOf()) {
                openCardEditPage(CardFormFragment(cardItems[it].id))
            }

            cardList.layoutManager = LinearLayoutManager(this@MainActivity)
            cardList.adapter = cardListAdapter

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

            popupTint.visibility = View.GONE
            popupTint.setOnClickListener {
                cardFormFragment?.let {
                    onFinishFragment(it, true)
                }
            }
            popupContainer.visibility = View.GONE
        }

        loadApplication()
    }

    private fun loadApplication() {
        //Логинимся, если не авторизованы
        binding.mainWindow.visibility = View.VISIBLE
        if (AuthManager.checkUserAuth(this) == NOT_LOGGED_IN) {
            binding.mainWindow.visibility = View.GONE
            supportFragmentManager.beginTransaction()
                .replace(R.id.mainFrame, WelcomeFragment(), mainframeTag)
                .commit()
            return
        }

        //Показываем гайд, если не показывали
        if (getSharedPreference(PreferencesKeys().IS_GUIDE_COMPLETE, "false") == "false") {
            binding.mainWindow.visibility = View.GONE
            makeAnimatedFragmentTransaction().replace(R.id.mainFrame, GuideFragment(), mainframeTag).commit()
            return
        }

        //Загружаем список карточек
        refreshCardsList()
    }

    private fun refreshCardsList() = runBlocking {
        cardItems = cardManager.getCards()
        cardListAdapter.setItems(cardItems.toMutableList())
    }

    override fun onFinishFragment(fragment: Fragment, withoutAnimation: Boolean) {
        when (fragment) {
            is WelcomeFragment -> loadApplication()
            is GuideFragment -> loadApplication()
            is CardFormFragment -> {
                binding.apply {
                    popupTint.visibility = View.GONE
                    popupContainer.visibility = View.GONE
                }
                refreshCardsList()
            }
        }
        if (withoutAnimation)
            supportFragmentManager.beginTransaction().remove(fragment).commit()
        else
            makeAnimatedFragmentTransaction().remove(fragment).commit()
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