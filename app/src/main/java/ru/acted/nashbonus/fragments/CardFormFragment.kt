package ru.acted.nashbonus.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import kotlinx.coroutines.runBlocking
import ru.acted.nashbonus.databinding.CardAddBinding
import ru.acted.nashbonus.utils.Card
import ru.acted.nashbonus.utils.CardManager
import ru.acted.nashbonus.utils.FrameFragment
import ru.acted.nashbonus.utils.UniversalFuns.Companion.finishWork

class CardFormFragment(private val id: String = ""): FrameFragment() {

    private lateinit var binding: CardAddBinding
    private lateinit var card: CardManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = CardAddBinding.inflate(inflater)
        card = CardManager(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            brandNameEdit.addTextChangedListener {
                it?.let { brandName.text = it.toString() }
            }
            cardNumberEdit.addTextChangedListener {
                it?.let { cardNumber.text = it.toString() }
            }

            if (id.isNotEmpty()) {
                runBlocking {
                    card.getCard(id).apply {
                        brandNameEdit.setText(brand)
                        cardNumberEdit.setText(number.toString())
                    }
                }
            }
            deleteButton.visibility = if (id.isNotEmpty()) View.VISIBLE else View.GONE

            readyButton.setOnClickAction {
                brandNameEdit.error = null
                if (brandNameEdit.text.isNotEmpty() && cardNumberEdit.text.isNotEmpty()) {
                    runBlocking {
                        card.insertCard(
                            Card(
                                id = id.ifEmpty { card.generateId(CardManager.TablePrefix.CARDS) },
                                brand = brandName.text.toString(),
                                number = cardNumber.text.toString().toInt()
                            )
                        )
                    }
                    finishWork(true)
                }
                else
                    brandNameEdit.error = "Введите номер и бренд"
            }

            deleteButton.setOnClickAction {
                runBlocking {
                    card.deleteCard(id)
                }
                finishWork(true)
            }
        }
    }
}