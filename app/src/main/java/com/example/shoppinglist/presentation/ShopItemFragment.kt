package com.example.shoppinglist.presentation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.shoppinglist.R
import com.example.shoppinglist.databinding.FragmentShopItemBinding
import com.example.shoppinglist.domain.ShopItem
import com.google.android.material.textfield.TextInputLayout

class ShopItemFragment(): Fragment() {

    private lateinit var shopItemViewModel: ShopItemViewModel

    private lateinit var onEditingFinishedListener: OnEditingFinishedListener

    private var _binding: FragmentShopItemBinding? = null
    private val binding: FragmentShopItemBinding
        get() = _binding ?: throw RuntimeException("FragmentShopItemBinding = null")

    private var screenMode = MODE_UNKNOWN
    private var shopItemId = ShopItem.UNDEFINE_ID

    private var name: String = ""
    private var count: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parseParams()   //желательно проверить все данные до полного создания фрагмента как в данном случае
    }

    //данный метод вызывается, когда фрагмент привязывается к Активити
    //в нем желательно привязывать интерфейсы
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnEditingFinishedListener) {
            onEditingFinishedListener = context //по сути ссылка на активити, к которому привязывается фрагмент
        }else {
            throw RuntimeException("Don't impelements interface OnEditingFinishedListener")
        }
    }

    //метод для создания view на основе макета(layout)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentShopItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    //после того как view создастся в методе onCreateView и с ней можно будет работать,
    // ссылка на нее передастся в этот метод
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        shopItemViewModel = ViewModelProvider(this)[ShopItemViewModel::class.java]
        binding.shopViewModel = shopItemViewModel
        binding.lifecycleOwner = viewLifecycleOwner
        addTextChangeListener()
        launchRightMode()
        //observeErrorInput()
        observeCloseScreen()
    }

    fun addTextChangeListener() {
        binding.etName.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                shopItemViewModel.resetInputErrorName()
            }

            override fun afterTextChanged(p0: Editable?) {}
        })
        binding.etCount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                shopItemViewModel.resetInputErrorCount()
            }

            override fun afterTextChanged(p0: Editable?) {}
        })
    }

    fun launchRightMode() {
        when(screenMode) {
            MODE_ADD -> launchAddMode()
            MODE_EDIT -> launchEditMode()
        }
    }

    //в качестве параметра метода observe передаем жизненный цикл созданной выше view
    //т.к. жизненный цикл view и fragment отличается и view может закончить существование раньше
    fun observeCloseScreen() {
        shopItemViewModel.shouldCloseScreen.observe(viewLifecycleOwner) {
            onEditingFinishedListener.onEditingFinished()   //вызываем в активити метод закрытия активити
            //метод ниже не нужен, потому что в onEditingFinished() вызывается popBackStack
            //activity?.onBackPressed() //закрывается окно активити, когда нажата кнопка назад
        }
    }

//    fun observeErrorInput() {
//        shopItemViewModel.errorInputName.observe(viewLifecycleOwner) {
//            val message = if (it) {
//                getString(R.string.error_input_name)
//            }else {
//                null
//            }
//            binding.tilName.error = message
//        }
//        shopItemViewModel.errorInputCount.observe(viewLifecycleOwner) {
//            val message = if (it) {
//                getString(R.string.error_input_count)
//            }else {
//                null
//            }
//            binding.tilCount.error = message
//        }
//    }

    fun launchAddMode() {
        binding.saveButton.setOnClickListener {
            shopItemViewModel.addShopItem(binding.etName.text?.toString(), binding.etCount.text?.toString())
        }
    }

    fun launchEditMode() {
        //передаем id для получения элемента из data-слоя
        shopItemViewModel.getShopItem(shopItemId)
        //подписываемся на этот элемент и по изменению выполняем действия в фигурных скобках
        shopItemViewModel.shopItem.observe(viewLifecycleOwner){
            binding.etName.setText(it.name)
            binding.etCount.setText(it.count.toString())
        }
        binding.saveButton.setOnClickListener {
            shopItemViewModel.editShopItem(binding.etName.text?.toString(), binding.etCount.text?.toString())
        }
    }

    fun parseParams() {
        val args = requireArguments() //получаем аргументы из Активити, к которому прикрепрен фрагмент
        if (!args.containsKey(SCREEN_MODE)){
            throw RuntimeException("Param screen mode is absent")
        }
        val mode = args.getString(SCREEN_MODE)
        if (mode != MODE_EDIT && mode != MODE_ADD) {
            throw RuntimeException("Unknown screen mode ${mode}")
        }
        screenMode = mode
        if (screenMode == MODE_EDIT) {
            if (!args.containsKey(SHOP_ITEM_ID)) {
                throw RuntimeException("Param shop item id is absent")
            }
            shopItemId = args.getInt(SHOP_ITEM_ID)
        }
    }

    companion object {
        private const val SCREEN_MODE = "extra_mode"
        private const val SHOP_ITEM_ID = "extra_shop_item_id"
        private const val MODE_EDIT = "mode_edit"
        private const val MODE_ADD = "mode_add"
        private const val MODE_UNKNOWN = ""

        fun addNewFragmentItem(): ShopItemFragment {
            return ShopItemFragment().apply {
                arguments = Bundle().apply {
                    putString(SCREEN_MODE, MODE_ADD)
                }
            }
        }

        fun editNewFragmentItem(shopItemId: Int): ShopItemFragment {
            return ShopItemFragment().apply {
                arguments = Bundle().apply {
                    putString(SCREEN_MODE, MODE_EDIT)
                    putInt(SHOP_ITEM_ID, shopItemId)
                }
            }
        }

        interface  OnEditingFinishedListener {
            fun onEditingFinished()
        }
    }
}