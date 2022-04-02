package com.example.shoppinglist.presentation

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.example.shoppinglist.data.ShopListRepositoryImpl
import com.example.shoppinglist.domain.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.util.*

//ViewModel работает с domain-слоем и обрабатывает логику на экране - ошибки ввода данных
class ShopItemViewModel(application: Application): AndroidViewModel(application) {
    private val shopListRepository = ShopListRepositoryImpl(application)

    private val getShopItemUseCase = GetShopItemUseCase(shopListRepository)
    private val addShopItemUseCase = AddShopItemUseCase(shopListRepository)
    private val editShopItemUseCase = EditShopItemUseCase(shopListRepository)

    //private val scope = CoroutineScope(Dispatchers.IO)    //можем создавать свой scope для работы с корутинами

    //т.к. нам надо изменять значения в данном классе, то сделаем мутабельной _errorInputName
    //для того чтобы работать с ней
    //но если нам надо, чтобы ее можно было получить в другом классе и при этом нельзя было бы изменить
    //для этого создадим errorInputName немутабельным и будем возвращать для нее _errorInputName
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
            viewModelScope.launch {
                addShopItemUseCase.addShopItem(ShopItem(name, count, true))
                finishWork()
            }
        }
    }

    fun editShopItem(inputName: String?, inputCount: String?) {
        val name = parseName(inputName)
        val count = parseCount(inputCount)
        val fieldValidate = validateInput(name, count)
        if (fieldValidate){
            //будем получать объект для редактирования из MutableLiveData и создавать копию для редактирования
            _shopItem.value?.let {
                viewModelScope.launch {
                    val item = it.copy(name = name, count = count)
                    editShopItemUseCase.editShopItem(item)
                    finishWork()
                }
            }
        }
    }

    fun getShopItem(shopItemId: Int) {
        viewModelScope.launch {
            val item = getShopItemUseCase.getItemInShop(shopItemId)
            _shopItem.value = item
        }
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

    fun resetInputErrorName() {
        _errorInputName.value = false
    }

    fun resetInputErrorCount() {
        _errorInputCount.value = false
    }

    fun finishWork() {
        //_shouldCloseScreen.value = Unit
        _shouldCloseScreen.value = Unit
    }

    //при наличии viewMovelScope данный метод не нужен, т.к. viewModelScope будет отменяться автоматически
//    override fun onCleared() {
//        super.onCleared()
//        scope.cancel()
//    }
}