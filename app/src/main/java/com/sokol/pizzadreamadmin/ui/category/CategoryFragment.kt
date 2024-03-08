package com.sokol.pizzadreamadmin.ui.category

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sokol.pizzadreamadmin.Adapter.CategoryAdapter
import com.sokol.pizzadreamadmin.Common.Common
import com.sokol.pizzadreamadmin.EventBus.AddCategoryClick
import com.sokol.pizzadreamadmin.EventBus.CategoryClick
import com.sokol.pizzadreamadmin.Model.FoodModel
import com.sokol.pizzadreamadmin.R
import org.greenrobot.eventbus.EventBus
import java.util.Locale

class CategoryFragment : Fragment() {
    private lateinit var categoryRecycler: RecyclerView
    private lateinit var btnCreate: Button
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val categoryViewModel = ViewModelProvider(this)[CategoryViewModel::class.java]
        val root = inflater.inflate(R.layout.fragment_category, container, false)
        initView(root)
        categoryViewModel.categoryList.observe(viewLifecycleOwner) {
            val adapter = CategoryAdapter(it, requireContext())
            categoryRecycler.adapter = adapter
        }
        return root
    }

    private fun initView(root: View) {
        btnCreate = root.findViewById(R.id.create)
        btnCreate.setOnClickListener {
            EventBus.getDefault().postSticky(AddCategoryClick(true))
        }
        categoryRecycler = root.findViewById(R.id.category_recycler)
        categoryRecycler.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        categoryRecycler.layoutManager = layoutManager
        categoryRecycler.addItemDecoration(
            DividerItemDecoration(
                requireContext(), layoutManager.orientation
            )
        )
    }
}