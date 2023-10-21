package ru.acted.nashbonus.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.core.content.res.ResourcesCompat
import ru.acted.nashbonus.R
import ru.acted.nashbonus.databinding.ViewNashabuttonBinding

/**
 * Это наша кнопка
 * @property isBold Жирный ли шрифт
 * @property disabled Выключена ли кнопка
 * @property icon Id ресурса иконки (null - нет иконки)
 * @property setOnClickListener Функция для установки действия по нажатию
 */
class NashaButtonView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): LinearLayout(context, attrs, defStyleAttr) {

    //Поля
    private val binding: ViewNashabuttonBinding
    private var runnable: Runnable? = null

    //Свойства
    var isBold = false
        set(value) {
            binding.buttonText.typeface =
                if (value) ResourcesCompat.getFont(context, R.font.zen_medium)
                else ResourcesCompat.getFont(context, R.font.zen_regular)
            field = value
        }
    var text: String = "Button"
        set(value) {
            binding.buttonText.text = value
            field = value
        }
    var disabled: Boolean = false
        set(value) {
            binding.buttonText.alpha = if (value) 0.8f else 1.0f
            field = value
        }
    var icon: Int? = null
        set(value) {
            field = value

            value?.let {
                binding.buttonIcon.setImageResource(it)
                binding.buttonIcon.visibility = View.VISIBLE
            } ?: {
                binding.buttonIcon.visibility = View.GONE
            }
        }

    init {
        val inflater = LayoutInflater.from(context)
        binding = ViewNashabuttonBinding.inflate(inflater, this, true)

        val attributes = context.obtainStyledAttributes(attrs, R.styleable.NashaButtonView, defStyleAttr, 0)

        attributes.getResourceId(R.styleable.NashaButtonView_button_text, -1).let { resourceId ->
            if (resourceId == -1) attributes.getString(R.styleable.NashaButtonView_button_text)?.let { stringValue -> text = stringValue }
            else text = context.getText(resourceId).toString()
        }
        attributes.getResourceId(R.styleable.NashaButtonView_button_icon, -1).let { if (it != -1) icon = it }
        disabled = attributes.getBoolean(R.styleable.NashaButtonView_button_is_disabled, false)
        isBold = attributes.getBoolean(R.styleable.NashaButtonView_button_is_bold, false)

        attributes.recycle()

        binding.buttonContent.setOnClickListener {
            if (!disabled) runnable?.run()
        }
    }

    /**
     * Функция для установки действия по нажатию
     * @param code Лямбда-функция, типа са скобачками просто вот так: { doSomething() }
     */
    fun setOnClickListener(code: Runnable) {
        runnable = code
    }
}