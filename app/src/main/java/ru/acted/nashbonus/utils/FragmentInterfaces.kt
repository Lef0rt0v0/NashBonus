package ru.acted.nashbonus.utils

import androidx.fragment.app.Fragment

/**
 * Интерфейс для коллбека из фрагмента в активити. Надо реализовывать в activity
 */
interface ActivityFragmentInteractInterface {

    /**
     * Вызывать из фрагментов когда он завершает свою работу
     * @param fragment Фрагмент, который был завершён
     */
    fun onFinishFragment(fragment: Fragment)

    /**
     * Вызывать из фрагментов когда необходимо отобразить ошибку в приложении
     * @param text Текст ошибки
     */
    fun onShowError(text: String)
}

/**
 * Интерфейс для коллбека из активити в фрагмент
 */
interface FragmentActivityInteractInterface {

    /**
     * Вызывать из активити, когда необходимо
     * @param args Аргументы, которые необходимо передать
     */
    fun onGetArgs(vararg args: String)
}