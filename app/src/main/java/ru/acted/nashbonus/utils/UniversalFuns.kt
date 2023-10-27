package ru.acted.nashbonus.utils

import android.app.Activity
import android.content.Context
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import ru.acted.nashbonus.R

data class PreferencesKeys (
    val IS_GUIDE_COMPLETE: String = "isGuideComplete",
    val TOKEN: String = "token"
)

class UniversalFuns {
    companion object {
        val phoneRegex = Regex("^\\+\\d{10,15}$")



        fun Fragment.showError(text: String) {
            if (requireActivity() is ActivityFragmentInteractInterface) {
                (requireActivity() as ActivityFragmentInteractInterface).onShowError(text)
            }
        }

        fun Fragment.finishWork() {
            if (requireActivity() is ActivityFragmentInteractInterface) {
                (requireActivity() as ActivityFragmentInteractInterface).onFinishFragment(this)
            }
        }

        fun TextView.makeClickableText(
            text: String,
            clickablePart: String = text,
            runnable: Runnable
        ) {
            if (!text.contains(clickablePart)) {
                throw IllegalArgumentException("Text does not contain clickable part")
            }

            val clickableSpan = object : ClickableSpan() {
                override fun onClick(widget: View) {
                    runnable.run()
                }

                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.color = resources.getColor(R.color.addtitional)
                    ds.isUnderlineText = true // Подчёркиваем текст
                }
            }
            val start = text.indexOf(clickablePart)
            val end = start + clickablePart.length

            movementMethod = LinkMovementMethod.getInstance()

            this.text = SpannableString(text).apply {
                setSpan(
                    clickableSpan,
                    start,
                    end,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
        }

        inline fun <reified T> Context.setSharedPreference(key: String, value: T) {
            val sharedPreferences = getSharedPreferences("default_prefs", Context.MODE_PRIVATE)
            with(sharedPreferences.edit()) {
                when (value) {
                    is String -> putString(key, value)
                    is Int -> putInt(key, value)
                    is Boolean -> putBoolean(key, value)
                    is Float -> putFloat(key, value)
                    is Long -> putLong(key, value)
                    else -> throw IllegalArgumentException("This type cannot be saved in SharedPreferences")
                }.apply()
            }
        }

        inline fun <reified T> Context.getSharedPreference(key: String, defaultValue: T): T {
            val sharedPreferences = getSharedPreferences("default_prefs", Context.MODE_PRIVATE)
            return with(sharedPreferences) {
                when (defaultValue) {
                    is String -> getString(key, defaultValue as? String) as T
                    is Int -> getInt(key, defaultValue as? Int ?: -1) as T
                    is Boolean -> getBoolean(key, defaultValue as? Boolean ?: false) as T
                    is Float -> getFloat(key, defaultValue as? Float ?: -1f) as T
                    is Long -> getLong(key, defaultValue as? Long ?: -1L) as T
                    else -> throw IllegalArgumentException("This type cannot be got from SharedPreferences")
                }
            }
        }

        fun AppCompatActivity.makeAnimatedFragmentTransaction(): FragmentTransaction =
            this.supportFragmentManager.beginTransaction().apply {
                setCustomAnimations(
                    R.anim.fragment_enter,  // Анимация появления нового фрагмента
                    R.anim.fragment_exit,   // Анимация затухания старого фрагмента
                    R.anim.fragment_enter,  // Анимация появления фрагмента при возврате назад (опционально)
                    R.anim.fragment_exit    // Анимация затухания фрагмента при возврате назад (опционально)
                )
            }
    }
}