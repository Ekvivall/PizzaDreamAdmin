package com.sokol.pizzadreamadmin.ui.reviewsPizzeria

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sokol.pizzadreamadmin.Adapter.ReviewPizzeriaAdapter
import com.sokol.pizzadreamadmin.Common.Common
import com.sokol.pizzadreamadmin.R

class ReviewsPizzeriaFragment: Fragment() {
    private lateinit var commentsRecycler: RecyclerView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val reviewsPizzeriaViewModel = ViewModelProvider(this).get(ReviewsPizzeriaViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_reviews_pizzeria, container, false)
        initView(root)
        if (Common.isConnectedToInternet(requireContext())) {
            reviewsPizzeriaViewModel.comments.observe(viewLifecycleOwner) {
                val listData = it
                val adapter = ReviewPizzeriaAdapter(listData, requireContext())
                commentsRecycler.adapter = adapter
            }
        } else {
            Toast.makeText(
                requireContext(), "Будь ласка, перевірте своє з'єднання!", Toast.LENGTH_SHORT
            ).show()
        }
        return root
    }

    private fun initView(root: View) {
        commentsRecycler = root.findViewById(R.id.reviews_pizzeria_recycler)
        commentsRecycler.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        commentsRecycler.layoutManager = layoutManager
        commentsRecycler.addItemDecoration(
            DividerItemDecoration(
                requireContext(), layoutManager.orientation
            )
        )
    }
}