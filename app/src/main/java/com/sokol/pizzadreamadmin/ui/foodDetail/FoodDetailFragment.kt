package com.sokol.pizzadreamadmin.ui.fooddetail

import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sokol.pizzadreamadmin.Adapter.AddonAdapter
import com.sokol.pizzadreamadmin.Adapter.AddonCategoryAdapter
import com.sokol.pizzadreamadmin.Common.Common
import com.sokol.pizzadreamadmin.Common.SpaceItemDecoration
import com.sokol.pizzadreamadmin.EventBus.AddonCategoryClick
import com.sokol.pizzadreamadmin.EventBus.AddonClick
import com.sokol.pizzadreamadmin.EventBus.CommentsClick
import com.sokol.pizzadreamadmin.Model.AddonModel
import com.sokol.pizzadreamadmin.Model.FoodModel
import com.sokol.pizzadreamadmin.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class FoodDetailFragment : Fragment() {
    private lateinit var categoryRecycler: RecyclerView
    private lateinit var addonRecycler: RecyclerView
    private lateinit var foodName: TextView
    private lateinit var foodImgLayout: ConstraintLayout
    private lateinit var foodDesc: TextView
    private lateinit var foodPrice: TextView
    private lateinit var ratingBar: RatingBar
    private lateinit var btnShowComment: Button
    private lateinit var radioGroupSize: RadioGroup
    private lateinit var rating: TextView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val foodDetailViewModel = ViewModelProvider(this).get(FoodDetailViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_food_detail, container, false)
        initView(root)
        val actionBar = (activity as AppCompatActivity).supportActionBar
        foodDetailViewModel.getFoodDetailMutableLiveData().observe(viewLifecycleOwner) {
            displayInfo(it)
            actionBar?.title = it.name ?: ""
        }
        if (Common.isConnectedToInternet(requireContext())) {
            foodDetailViewModel.categoryList.observe(viewLifecycleOwner, Observer {
                val listData = it
                if (it.isEmpty()) {
                    root.findViewById<View>(R.id.view_before_addon).visibility = View.GONE
                    root.findViewById<TextView>(R.id.addon_text).visibility = View.GONE
                } else {
                    root.findViewById<View>(R.id.view_before_addon).visibility = View.VISIBLE
                    root.findViewById<TextView>(R.id.addon_text).visibility = View.VISIBLE
                    val adapter = AddonCategoryAdapter(listData, requireContext())
                    categoryRecycler.adapter = adapter
                    Common.addonCategorySelected = it[0]
                    Common.foodSelected!!.userSelectedAddon = ArrayList()
                    EventBus.getDefault().postSticky(AddonCategoryClick(true))
                }
            })
        } else {
            Toast.makeText(
                requireContext(), "Будь ласка, перевірте своє з'єднання!", Toast.LENGTH_SHORT
            ).show()
        }
        return root
    }

    private fun displayInfo(it: FoodModel) {
        val foodImg = ImageView(requireContext())
        foodImg.id = View.generateViewId()
        foodImg.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
        Glide.with(requireContext()).load(it.image).into(foodImg)
        foodImgLayout.addView(foodImg)
        foodName.text = it.name!!
        foodDesc.text = Html.fromHtml(it.description!!, Html.FROM_HTML_MODE_LEGACY)
        val ratingAverage = it.ratingSum.toFloat() / it.ratingCount
        ratingBar.rating = ratingAverage
        rating.text = if (it.ratingCount == 0L) "0" else String.format("%.1f", ratingAverage)
        for (sizeModel in it.size) {
            val radioButton = RadioButton(context)
            radioButton.setOnCheckedChangeListener { compoundButton, b ->
                if (b) {
                    Common.foodSelected?.userSelectedSize = sizeModel
                }
                calculateTotalPrice()
            }
            val params = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1.0f)
            radioButton.layoutParams = params
            radioButton.text = sizeModel.name
            radioButton.tag = sizeModel.price
            radioGroupSize.addView(radioButton)
        }
        if (radioGroupSize.childCount > 0) {
            val radioButton = radioGroupSize.getChildAt(0) as RadioButton
            radioButton.isChecked = true
        }
    }

    private fun initView(root: View) {
        categoryRecycler = root.findViewById(R.id.addon_category_recycler)
        categoryRecycler.setHasFixedSize(true)
        categoryRecycler.layoutManager =
            LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        addonRecycler = root.findViewById(R.id.addon_recycler)
        addonRecycler.setHasFixedSize(true)
        val layoutManager = GridLayoutManager(context, 2)
        layoutManager.orientation = RecyclerView.VERTICAL
        addonRecycler.layoutManager = layoutManager
        addonRecycler.addItemDecoration(SpaceItemDecoration(8))
        foodImgLayout = root.findViewById(R.id.food_img)
        foodName = root.findViewById(R.id.food_name)
        foodDesc = root.findViewById(R.id.food_desc)
        foodPrice = root.findViewById(R.id.food_price)
        ratingBar = root.findViewById(R.id.ratingBar)
        btnShowComment = root.findViewById(R.id.btnShowComment)
        btnShowComment.setOnClickListener {
            EventBus.getDefault().postSticky(CommentsClick(true))
        }
        radioGroupSize = root.findViewById(R.id.radio_group_size)
        rating = root.findViewById(R.id.rating)
    }

    var totalPrice = 0.0
    private fun calculateTotalPrice() {
        totalPrice = Common.foodSelected?.userSelectedSize?.price?.toDouble()!!
        if (Common.foodSelected!!.userSelectedAddon != null && Common.foodSelected!!.userSelectedAddon!!.size > 0) {
            for (addonModel in Common.foodSelected!!.userSelectedAddon!!) {
                totalPrice = totalPrice.plus(addonModel.price.toDouble() * addonModel.userCount)
            }
        }
        val displayPrice = Math.round(totalPrice * 100.0) / 100.0
        foodPrice.text = StringBuilder("Всього: ").append(Common.formatPrice(displayPrice)).toString()
    }

    private fun addImageIngredient(addonModel: AddonModel, pos: Int) {
        CoroutineScope(Dispatchers.Main).launch {
            val foodImg = ImageView(requireContext())
            foodImg.id = View.generateViewId()
            foodImg.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
            )
            val imageBitmap = withContext(Dispatchers.IO) {
                Glide.with(requireContext()).asBitmap().load(addonModel.imageFill).submit().get()
            }
            foodImg.setImageBitmap(imageBitmap)
            foodImgLayout.addView(foodImg, pos)
        }
    }

    private fun removeImageIngredient(pos: Int) {
        val viewToRemove = foodImgLayout.getChildAt(pos)
        foodImgLayout.removeView(viewToRemove)
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun onAddonCategorySelected(event: AddonCategoryClick) {
        if (event.isSuccess) {
            addonRecycler.adapter =
                AddonAdapter(Common.addonCategorySelected!!.items, requireContext())
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun onAddonSelected(event: AddonClick) {
        if (Common.foodSelected?.id?.contains("constructor") == true) {
            if (event.isSuccess) {
                addImageIngredient(event.addon, event.pos)
            } else {
                removeImageIngredient(event.pos)
            }
        }
        calculateTotalPrice()
    }

    override fun onStart() {
        super.onStart()
        if (!EventBus.getDefault().isRegistered(this)) EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        Common.foodSelected?.userSelectedAddon = null
        if (EventBus.getDefault().isRegistered(this)) EventBus.getDefault().unregister(this)
        Common.addonCategorySelected = null
    }

    override fun onResume() {
        super.onResume()
        if (!EventBus.getDefault().isRegistered(this)) EventBus.getDefault().register(this)
    }
}

