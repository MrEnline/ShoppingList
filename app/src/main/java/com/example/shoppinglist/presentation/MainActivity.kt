package com.example.shoppinglist.presentation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.shoppinglist.R
import com.example.shoppinglist.domain.ShopItem
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity(), ShopItemFragment.Companion.OnEditingFinishedListener {

    private lateinit var viewModel: MainViewModel
    private lateinit var shopListAdapter: ShopListAdapter

    private var shopItemContainer: FragmentContainerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        shopItemContainer = findViewById(R.id.shop_item_container)
        setupRecyclerView()
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        viewModel.shopList.observe(this) {
            //shopListAdapter.shopItemList = it
            shopListAdapter.submitList(it) //в отдельном потоке заполняет адаптер
        }
        val buttonAddItem = findViewById<FloatingActionButton>(R.id.button_add_shop_item)
        buttonAddItem.setOnClickListener {
            if (isOnePanelMode()) {
                val intent = ShopItemActivity.addNewIntentItem(this)
                startActivity(intent)
            } else {
                launchFragment(ShopItemFragment.addNewFragmentItem())
            }
        }
    }
    //данный метод вызывается в классе фрагмента после нажатия кнопки Save
    override fun onEditingFinished() {
        Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show()
        supportFragmentManager.popBackStack() //удаляем ссылку на фрагмент из бэкстэка
    }

    //если экран не перевернут, тогда режим однопанельный
    private fun isOnePanelMode(): Boolean {
        return shopItemContainer == null
    }

    private fun launchFragment(fragment: Fragment) {
        supportFragmentManager.popBackStack() //удаляет ссылки на предыдущие фрагменты из бэкстэка
        //управление добавлением фрагмента на экран активити c помощью менеджера фрагментов
        supportFragmentManager.beginTransaction()
            //.add(R.id.shop_item_container, fragment)   //добавляем в контейнер созданный фрагмент выше
            .replace(R.id.shop_item_container, fragment) //вместо add лучше использовать replace, чтобы в контейнере не увеличивалось количество фрагментов
            .addToBackStack(null) //добавляет ссылку на фрагмент в бэкстэк
            .commit()
    }

    private fun setupRecyclerView() {
        val rvShopList = findViewById<RecyclerView>(R.id.rv_shop_list);
        shopListAdapter = ShopListAdapter()
        with(rvShopList) {
            adapter = shopListAdapter
            recycledViewPool.setMaxRecycledViews(ShopListAdapter.VIEW_TYPE_DISABLED, ShopListAdapter.MAX_POOL_SIZE)
            recycledViewPool.setMaxRecycledViews(ShopListAdapter.VIEW_TYPE_ENABLED, ShopListAdapter.MAX_POOL_SIZE)
        }
        setupLongClickListener()
        setupClickListener()
        setupSwipeListener(rvShopList)
    }

    private fun setupSwipeListener(rvShopList: RecyclerView) {
        val callback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT
                                                                or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder): Boolean {
                TODO("Not yet implemented")
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                //val item = shopListAdapter.shopItemList[viewHolder.adapterPosition]   //старая реализация
                val item = shopListAdapter.currentList[viewHolder.adapterPosition]  //получаем объект, который свайпаем
                viewModel.deleteShopItem(item)  //удаляем из данных элемент, который свайпнули
            }
        }

        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(rvShopList)    //прикрепляем данную реализацию к RecyclerView
    }

    private fun setupClickListener() {
        shopListAdapter.onShopItemClickListener = {
            if (isOnePanelMode()) {
                val intent = ShopItemActivity.editNewIntentItem(this, it.id)
                startActivity(intent)
            } else {
               launchFragment(ShopItemFragment.editNewFragmentItem(it.id))
            }
        }
    }

    private fun setupLongClickListener() {
        shopListAdapter.onShopItemLongClickListener = {
            viewModel.changeEnableState(it)
        }
    }
}