package com.example.shoppinglist.presentation

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.shoppinglist.R
import com.example.shoppinglist.domain.ShopItem
import java.lang.RuntimeException

class ShopListAdapter: ListAdapter<ShopItem, ShopItemViewHolder>(ShopItemDiffCallback()) {

//    var count: Int = 0

// 1-й метод сравнения списков при изменении элементов recyclerView
//    var shopItemList = listOf<ShopItem>()
//        set(value) {
//            val callback = ShopListDiffCallback(shopItemList, value)
//            val diffResult = DiffUtil.calculateDiff(callback) //производится сравнение двух массивов
//            diffResult.dispatchUpdatesTo(this)
//            field = value
//        }

    var onShopItemLongClickListener: ((ShopItem) -> Unit)? = null
    var onShopItemClickListener: ((ShopItem) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShopItemViewHolder {
        //на основе viewType, который передается из метода getItemViewType создаем view из макета
        val layout = when (viewType) {
            VIEW_TYPE_ENABLED -> R.layout.item_shop_enabled
            VIEW_TYPE_DISABLED -> R.layout.item_shop_disabled
            else -> throw RuntimeException("Unknown viewType: ${viewType}")
        }
        val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)
        return ShopItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ShopItemViewHolder, position: Int) {
//        Log.d("onBindViewHolder", "${++count}")
//        val shopItem = shopItemList[position]
        val shopItem = getItem(position)
        holder.view.setOnLongClickListener {
            onShopItemLongClickListener?.invoke(shopItem)
            true
        }
        holder.view.setOnClickListener {
            onShopItemClickListener?.invoke((shopItem))
        }
        holder.tvName.text = "${shopItem.name}"
        holder.tvCount.text = "${shopItem.count}"
        holder.tvName.setTextColor(ContextCompat.getColor(holder.view.context, android.R.color.holo_red_dark))
    }

//    override fun getItemCount(): Int {
//        return shopItemList.size
//    }

    override fun getItemViewType(position: Int): Int {
        val view = getItem(position)
        return if (view.enabled) {
            VIEW_TYPE_ENABLED
        } else {
            VIEW_TYPE_DISABLED
        }
    }

//    override fun onViewRecycled(holder: ShopItemViewHolder) {
//        super.onViewRecycled(holder)
//        holder.tvName.text = ""
//        holder.tvCount.text = ""
//        holder.tvName.setTextColor(ContextCompat.getColor(holder.view.context, android.R.color.white))
//    }

//    class ShopItemViewHolder(val view: View): RecyclerView.ViewHolder (view) {
//        val tvName = view.findViewById<TextView>(R.id.tv_name)
//        val tvCount = view.findViewById<TextView>(R.id.tv_count)
//    }

//    interface OnShopItemLongClickListener {
//        fun onShopItemLongClick(shopItem: ShopItem)
//    }

    companion object {
        const val VIEW_TYPE_ENABLED = 100
        const val VIEW_TYPE_DISABLED = 101

        const val MAX_POOL_SIZE = 15
    }
}