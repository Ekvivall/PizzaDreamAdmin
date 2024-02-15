package com.sokol.pizzadreamadmin.ui.appointAdmin

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
import com.sokol.pizzadreamadmin.Adapter.AppointAdminAdapter
import com.sokol.pizzadreamadmin.Common.Common
import com.sokol.pizzadreamadmin.R

class AppointAdminFragment : Fragment() {
    private lateinit var usersRecycler: RecyclerView
    private var appointAdminAdapter: AppointAdminAdapter? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val appointAdminViewModel = ViewModelProvider(this)[AppointAdminViewModel::class.java]
        val root = inflater.inflate(R.layout.fragment_appoint_admin, container, false)
        initView(root)
        if (Common.isConnectedToInternet(requireContext())) {
            appointAdminViewModel.users.observe(viewLifecycleOwner) {
                val listData = it
                appointAdminAdapter = AppointAdminAdapter(listData, requireContext())
                usersRecycler.adapter = appointAdminAdapter
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
        usersRecycler = root.findViewById(R.id.recycler)
        usersRecycler.setHasFixedSize(true)
        val layoutManager =
            LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        usersRecycler.layoutManager = layoutManager
        usersRecycler.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                layoutManager.orientation
            )
        )
    }
}