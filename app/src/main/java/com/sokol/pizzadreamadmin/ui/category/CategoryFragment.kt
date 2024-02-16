package com.sokol.pizzadreamadmin.ui.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sokol.pizzadreamadmin.Adapter.CategoryAdapter
import com.sokol.pizzadreamadmin.R

class CategoryFragment : Fragment() {
    private lateinit var categoryRecycler: RecyclerView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val categoryViewModel = ViewModelProvider(this).get(CategoryViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_category, container, false)
        initView(root)
        categoryViewModel.categoryList.observe(viewLifecycleOwner) {
            val adapter = CategoryAdapter(it, requireContext())
            categoryRecycler.adapter = adapter
        }
        return root
    }

    private fun initView(root: View) {
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