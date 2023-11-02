package ru.acted.nashbonus.utils

import android.os.Bundle
import androidx.fragment.app.Fragment

open class FrameFragment: Fragment(), FragmentActivityInteractInterface {
    companion object {
        /**
         * Функция для создания нового экземпляра фрагмента
         * @property args аргументы, передаваемые в фрагмент
         */
        fun newInstance(vararg args: Pair<String, String>) = FrameFragment().apply {
            if (args.isNotEmpty())
                arguments = Bundle().apply {
                    args.forEach { argument ->
                        putString(argument.first, argument.second)
                    }
                }
        }
    }

    override fun onGetArgs(vararg args: String) {
        if (args.isEmpty()) throw IllegalArgumentException("Sent empty arguments to our fragment")
    }
}