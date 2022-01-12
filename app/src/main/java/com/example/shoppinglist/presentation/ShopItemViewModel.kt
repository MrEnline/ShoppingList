package com.example.shoppinglist.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.shoppinglist.data.ShopListRepositoryImpl
import com.example.shoppinglist.domain.*

class ShopItemViewModel: ViewModel() {
    private val shopListRepository = ShopListRepositoryImpl

    private val getShopItemUseCase = GetShopItemUseCase(shopListRepository)
    private val addShopItemUseCase = AddShopItemUseCase(shopListRepository)
    private val editShopItemUseCase = EditShopItemUseCase(shopListRepository)

    fun addShopItem(inputName: String?, inputCount: String?) {
        val name = parseName(inputName)
        val count = parseCount(inputCount)
        val fieldValidate = validateInput(name, count)
        if (fieldValidate)
            addShopItemUseCase.addShopItem(ShopItem(name, count, true))
    }

    fun editShopItem(inputName: String?, inputCount: String?) {
        val name = parseName(inputName)
        val count = parseCount(inputCount)
        val fieldValidate = validateInput(name, count)
        if (fieldValidate)
            editShopItemUseCase.editShopItem(ShopItem(name, count, true))
    }

    fun getShopItem(shopItemId: Int) {
        getShopItemUseCase.getItemInShop(shopItemId)
    }

    fun changeEnableState(shopItem: ShopItem) {
        val newItem = shopItem.copy(enabled = !shopItem.enabled)
        editShopItemUseCase.editShopItem(newItem)
    }

    fun parseName(inputName: String?): String {
        return inputName?.trim() ?: ""
    }

    fun parseCount(inputCount: String?): Int {
        return try {
            inputCount?.trim()?.toInt() ?: 0
        }
        catch(e: Exception){
            Log.d("Error", e.message.toString())
        }
    }

    fun validateInput(name: String, count: Int): Boolean {
        var result = true
        if (name.isBlank()) {
            //TODO: show error input name
            result = false
        }
        if (count <= 0) {
            //TODO: show error input count
            result = false
        }
        return result
    }
}