package com.sokol.pizzadreamadmin.ui.resumes

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
import com.sokol.pizzadreamadmin.Adapter.ResumeAdapter
import com.sokol.pizzadreamadmin.Common.Common
import com.sokol.pizzadreamadmin.R

class ResumesFragment:Fragment() {
    private lateinit var resumesRecycler: RecyclerView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val resumesViewModel = ViewModelProvider(this).get(ResumesViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_resumes, container, false)
        initView(root)
        if (Common.isConnectedToInternet(requireContext())) {
            resumesViewModel.resumes.observe(viewLifecycleOwner) {
                val listData = it
                val adapter = ResumeAdapter(listData, requireContext())
                resumesRecycler.adapter = adapter
            }
        } else {
            Toast.makeText(
                requireContext(), "Будь ласка, перевірте своє з'єднання!", Toast.LENGTH_SHORT
            ).show()
        }
        return root
    }

    private fun initView(root: View) {
        resumesRecycler = root.findViewById(R.id.resumes_recycler)
        resumesRecycler.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        resumesRecycler.layoutManager = layoutManager
        resumesRecycler.addItemDecoration(
            DividerItemDecoration(
                requireContext(), layoutManager.orientation
            )
        )
    }
}