package com.example.shoppinglist.data

import androidx.room.Entity
import androidx.room.PrimaryKey

//данный класс создан по причине того, что domain слой является самым независимым и
//ничего не должен знать про data-слой
@Entity(tableName = "shop_items")
data class ShopItemDBModel(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
    val count: Int,
    val enabled: Boolean
)