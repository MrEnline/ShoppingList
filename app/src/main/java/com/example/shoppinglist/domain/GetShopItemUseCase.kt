package com.example.shoppinglist.domain

class GetShopItemUseCase(private val shopListRepository: ShopListRepository) {

    suspend fun getItemInShop(shopItemId: Int): ShopItem{
        return shopListRepository.getShopItem(shopItemId)
    }
}