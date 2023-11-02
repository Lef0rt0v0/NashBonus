package ru.acted.nashbonus.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.acted.nashbonus.R
import ru.acted.nashbonus.databinding.ScreenGuideBinding
import ru.acted.nashbonus.utils.FrameFragment
import ru.acted.nashbonus.utils.PreferencesKeys
import ru.acted.nashbonus.utils.UniversalFuns.Companion.finishWork
import ru.acted.nashbonus.utils.UniversalFuns.Companion.setSharedPreference

class GuideFragment: FrameFragment() {
    data class GuideItem (
        val title: String,
        val description: String,
        val image: Int
    )

    private val guideItems = mutableListOf<GuideItem>()

    init {
        guideItems.add(GuideItem(title = "Что это такое", description = "Все дисконтные карты ваших любимых магазинов в одном приложении!", image = R.drawable.pic_card_hint))
        guideItems.add(GuideItem(title = "Карты сохраняются", description = "Ваши карты надёжно сохранены в облаке и привязаны к номеру телефона!", image = R.drawable.pic_cloud_hint))
        guideItems.add(GuideItem(title = "Как использовать", description = "Чтобы использовать карту, выберите её и покажите кассиру", image = R.drawable.pic_usage_hint))
    }

    private lateinit var binding: ScreenGuideBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ScreenGuideBinding.inflate(layoutInflater)
        return binding.root
    }

    private fun showGuideItem(pos: Int) = with(binding) {
        if (pos !in guideItems.indices)
            throw IllegalArgumentException("Guide item is out of bounds")

        guideItems[pos].let { guide ->
            headerText.text = "Информация ${pos + 1}/${guideItems.size}"
            titleText.text = guide.title
            descriptionText.text = guide.description
            image.setImageResource(guide.image)
        }

        if (pos == guideItems.lastIndex)
            proceedNextButton.text = "Хорошо!"
    }

    private var currentGuidePos = 0
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        showGuideItem(currentGuidePos)

        binding.apply {
            proceedNextButton.setOnClickAction {
                if (currentGuidePos == guideItems.lastIndex) {
                    finishWork()
                    requireActivity().setSharedPreference(PreferencesKeys().IS_GUIDE_COMPLETE, "true")
                    return@setOnClickAction
                }

                currentGuidePos++
                showGuideItem(currentGuidePos)
            }
        }
    }
}