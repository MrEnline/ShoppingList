package com.example.shoppinglist.presentation

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.shoppinglist.R
import com.example.shoppinglist.domain.ShopItem
import com.google.android.material.textfield.TextInputLayout

class ShopItemActivity : AppCompatActivity(), ShopItemFragment.Companion.OnEditingFinishedListener {

    private var screenMode = MODE_UNKNOWN
    private var shopItemId = ShopItem.UNDEFINE_ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shop_item)
        parseIntent()
        if (savedInstanceState == null)
            launchRightMode()   //если еще ни разу не была создана, то вызовем метод в котором создается фрагмент 1 раз и не больше
    }

    //данный метод вызывается в классе фрагмента после нажатия кнопки Save
    override fun onEditingFinished() {
        finish()
    }

    private fun launchRightMode() {
        val fragment = when(screenMode) {
                            MODE_ADD -> ShopItemFragment.addNewFragmentItem()
                            MODE_EDIT -> ShopItemFragment.editNewFragmentItem(shopItemId)
                            else -> throw RuntimeException("Unknown screenmode ${screenMode}")
        }
    //управление добавлением фрагмента на экран активити c помощью менеджера фрагментов
    supportFragmentManager.beginTransaction()
        //.add(R.id.shop_item_container, fragment)   //добавляем в контейнер созданный фрагмент выше
        .replace(R.id.shop_item_container, fragment) //вместо add лучше использовать replace, чтобы в контейнере не увеличивалось количество фрагментов
        .commit()
    }

    private fun parseIntent() {
        if (!intent.hasExtra(EXTRA_SCREEN_MODE)){
            throw RuntimeException("Param screen mode is absent")
        }
        val mode = intent.getStringExtra(EXTRA_SCREEN_MODE)
        if (mode != MODE_EDIT && mode != MODE_ADD) {
            throw RuntimeException("Unknown screen mode ${mode}")
        }
        screenMode = mode
        if (screenMode == MODE_EDIT) {
            if (!intent.hasExtra(EXTRA_SHOP_ITEM_ID)) {
                throw RuntimeException("Param shop item id is absent")
            }
            shopItemId = intent.getIntExtra(EXTRA_SHOP_ITEM_ID, ShopItem.UNDEFINE_ID)
        }
    }

    companion object {
        const val EXTRA_SCREEN_MODE = "extra_mode"
        const val EXTRA_SHOP_ITEM_ID = "extra_shop_item_id"
        const val MODE_EDIT = "mode_edit"
        const val MODE_ADD = "mode_add"
        const val MODE_UNKNOWN = ""

        fun addNewIntentItem(context: Context): Intent {
            val intent = Intent(context, ShopItemActivity::class.java)
            intent.putExtra(EXTRA_SCREEN_MODE, MODE_ADD)
            return intent
        }

        fun editNewIntentItem(context: Context, shopItemId: Int): Intent {
            val intent = Intent(context, ShopItemActivity::class.java)
            intent.putExtra(EXTRA_SCREEN_MODE, MODE_EDIT)
            intent.putExtra(EXTRA_SHOP_ITEM_ID, shopItemId)
            return intent
        }
    }
}