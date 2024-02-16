package com.sokol.pizzadreamadmin.ui.news

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sokol.pizzadreamadmin.Adapter.NewsAdapter
import com.sokol.pizzadreamadmin.Common.Common
import com.sokol.pizzadreamadmin.R

class NewsFragment : Fragment() {
    private lateinit var newsRecycler: RecyclerView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val newsViewModel = ViewModelProvider(this).get(NewsViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_news, container, false)
        initView(root)
        if (Common.isConnectedToInternet(requireContext())) {
            newsViewModel.newsList.observe(viewLifecycleOwner) {
                val listData = it
                val newsAdapter = NewsAdapter(listData, requireContext())
                newsRecycler.adapter = newsAdapter
            }
        } else {
            Toast.makeText(
                requireContext(),
                "Будь ласка, перевірте своє з'єднання!",
                Toast.LENGTH_SHORT
            ).show()
        }
        return root
    }

    private fun initView(root: View) {
        newsRecycler = root.findViewById(R.id.news_recycler)
        newsRecycler.setHasFixedSize(true)
        newsRecycler.layoutManager =
            LinearLayoutManager(context, RecyclerView.VERTICAL, false)
    }
}