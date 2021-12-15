package com.example.shoppinglist.domain

class GetShopItemUseCase(private val shopListRepository: ShopListRepository) {

    fun getItemInShop(shopItemId: Int): ShopItem{
        return shopListRepository.getShopItem(shopItemId)
    }
}