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
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import android.graphics.Bitmap
import android.graphics.Color
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
import ru.acted.nashbonus.utils.UniversalFuns.Companion.showError

class CardFormFragment(private val id: String = ""): FrameFragment() {

    private suspend fun generateBarcode(content: String, width: Int, height: Int): Bitmap? {
        return try {
            val bitMatrix: BitMatrix = MultiFormatWriter().encode(
                content,
                BarcodeFormat.CODE_128,
                width,
                height,
                hashMapOf(EncodeHintType.MARGIN to 1)
            )

            yield()

            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
            for (x in 0 until width) {
                for (y in 0 until height) {
                    bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
                    yield()
                }
            }
            bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

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

    private var generatingJob: Job? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            rootCardInfo.setOnClickListener {  }

            brandNameEdit.addTextChangedListener {
                brandName.text = if (it.isNullOrEmpty())
                    "Магазин..."
                else
                    it.toString()
            }
            cardNumberEdit.addTextChangedListener {
                cardNumber.text = if (it.isNullOrEmpty())
                    "Номер карты..."
                else
                    it.toString()
                generatingJob?.cancel()
                cardBarcode.post {
                    generatingJob = lifecycleScope.launch {
                        var barcodeNumber = ""
                        if (!it.isNullOrEmpty()) {
                            barcodeNumber = it.toString()
                        }
                        val barcodeBitmap = generateBarcode(barcodeNumber, cardBarcode.width, cardBarcode.height)
                        cardBarcode.setImageBitmap(barcodeBitmap)
                    }
                }
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
                    try {
                        runBlocking {
                            card.insertCard(
                                Card(
                                    id = id.ifEmpty { card.generateId(CardManager.TablePrefix.CARDS) },
                                    brand = brandName.text.toString(),
                                    number = cardNumber.text.toString()
                                )
                            )
                        }
                        finishWork(true)
                    } catch (e: java.lang.Exception) {
                        showError("Вы ввели некорректный номер карты")
                    }
                }
                else
                    showError("Введите номер и бренд")
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