package com.example.shoppinglist.presentation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.shoppinglist.data.ShopListRepositoryImpl
import com.example.shoppinglist.domain.*
import java.util.*

class ShopItemViewModel: ViewModel() {
    private val shopListRepository = ShopListRepositoryImpl

    private val getShopItemUseCase = GetShopItemUseCase(shopListRepository)
    private val addShopItemUseCase = AddShopItemUseCase(shopListRepository)
    private val editShopItemUseCase = EditShopItemUseCase(shopListRepository)

    //таким способом создают переменную, которую можно будет редактировать в другом классе
    private val _errorInputName = MutableLiveData<Boolean>()
    val errorInputName: LiveData<Boolean>   //переменная типа LiveData. На нее будем подписываться из Activity
        get() = _errorInputName

    private val _errorInputCount = MutableLiveData<Boolean>()
    val errorInputCount: LiveData<Boolean>  //переменная типа LiveData. На нее будем подписываться из Activity
        get() = _errorInputCount

    private val _shopItem = MutableLiveData<ShopItem>()
    val shopItem: LiveData<ShopItem>
        get() = _shopItem

    private val _shouldCloseScreen = MutableLiveData<Unit>()
    val shouldCloseScreen:LiveData<Unit>
        get() = _shouldCloseScreen

    fun addShopItem(inputName: String?, inputCount: String?) {
        val name = parseName(inputName)
        val count = parseCount(inputCount)
        val fieldValidate = validateInput(name, count)
        if (fieldValidate){
            addShopItemUseCase.addShopItem(ShopItem(name, count, true))
            finishWork()
        }
    }

    fun editShopItem(inputName: String?, inputCount: String?) {
        val name = parseName(inputName)
        val count = parseCount(inputCount)
        val fieldValidate = validateInput(name, count)
        if (fieldValidate){
            //будем получать объект для редактирования из MutableLiveData и создавать копию для редактирования
            _shopItem.value?.let {
                val item = it.copy(name = name, count = count)
                editShopItemUseCase.editShopItem(item)
                finishWork()
            }
        }
    }

    fun getShopItem(shopItemId: Int) {
        val item = getShopItemUseCase.getItemInShop(shopItemId)
        _shopItem.value = item
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
            _errorInputName.value = true
            result = false
        }
        if (count <= 0) {
            _errorInputCount.value = true
            result = false
        }
        return result
    }

    public fun resetInputErrorName() {
        _errorInputName.value = false
    }

    public fun resetInputErrorCount() {
        _errorInputCount.value = false
    }

    public fun finishWork() {
        _shouldCloseScreen.value = Unit
    }
}