package ru.acted.nashbonus.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.acted.nashbonus.R
import ru.acted.nashbonus.databinding.ItemCardBinding
import ru.acted.nashbonus.utils.Card

class CardsAdapter(private var items: MutableList<Card>, private val listener: ((pos: Int) -> Unit)? = null): RecyclerView.Adapter<CardsAdapter.ItemHolder>() {
    class ItemHolder(item: View): RecyclerView.ViewHolder(item) {
        val binding = ItemCardBinding.bind(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ItemHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_card, parent, false))
    override fun getItemCount() = items.size
    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        holder.binding.apply {
            items[position].let {
                cardTitle.text = it.brand
                cardNum.text = it.number.toString()
            }
            card.setOnClickListener {
                listener?.invoke(position)
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setItems(newItems: MutableList<Card>){
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }
}