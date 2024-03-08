package com.sokol.pizzadreamadmin.ui.viewOrders

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sokol.pizzadreamadmin.Adapter.FoodAdapter
import com.sokol.pizzadreamadmin.Adapter.OrderAdapter
import com.sokol.pizzadreamadmin.Common.Common
import com.sokol.pizzadreamadmin.EventBus.MenuClick
import com.sokol.pizzadreamadmin.Model.OrderModel
import com.sokol.pizzadreamadmin.R
import org.greenrobot.eventbus.EventBus

class ViewOrdersFragment : Fragment() {
    private lateinit var viewOrderViewModel: ViewOrdersViewModel
    private lateinit var layoutEmptyOrders: LinearLayout
    private lateinit var ordersView: LinearLayout
    private lateinit var orderRecycler: RecyclerView
    private lateinit var btnGoToMenu: Button
    private lateinit var spnStatus: Spinner
    private lateinit var layoutAnimatorController: LayoutAnimationController
    private var orderList: List<OrderModel> = ArrayList()
    private var orderAdapter: OrderAdapter? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewOrderViewModel = ViewModelProvider(this).get(ViewOrdersViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_view_orders, container, false)
        initView(root)
        viewOrderViewModel.orders.observe(viewLifecycleOwner, Observer {
            if (it == null || it.isEmpty()) {
                ordersView.visibility = View.GONE
                layoutEmptyOrders.visibility = View.VISIBLE
            } else {
                orderList = it
                ordersView.visibility = View.VISIBLE
                layoutEmptyOrders.visibility = View.GONE
                updateOrderList(it)
            }
        })
        return root
    }

    private fun initView(root: View) {
        ordersView = root.findViewById(R.id.orders)
        spnStatus = root.findViewById(R.id.spn_order_status)
        val adapterStatus = ArrayAdapter(
            requireContext(), android.R.layout.simple_spinner_dropdown_item, Common.STATUSES
        )
        adapterStatus.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spnStatus.adapter = adapterStatus
        spnStatus.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                val filterOrderList =
                    orderList.filter { x -> x.status == Common.STATUSES[position] }
                updateOrderList(filterOrderList)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
        layoutAnimatorController =
            AnimationUtils.loadLayoutAnimation(context, R.anim.layout_item_from_left)
        layoutEmptyOrders = root.findViewById(R.id.empty_orders)
        orderRecycler = root.findViewById(R.id.orders_recycler)
        orderRecycler.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(requireContext())
        orderRecycler.layoutManager = layoutManager
        btnGoToMenu = root.findViewById(R.id.btn_go_to_menu)
        btnGoToMenu.setOnClickListener {
            if (Common.isConnectedToInternet(requireContext())) {
                EventBus.getDefault().postSticky(MenuClick(true))
            } else {
                Toast.makeText(
                    requireContext(), "Будь ласка, перевірте своє з'єднання!", Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun updateOrderList(filterOrderList: List<OrderModel>) {
        orderAdapter = OrderAdapter(filterOrderList, requireContext())
        orderRecycler.adapter = orderAdapter
        orderRecycler.layoutAnimation = layoutAnimatorController
    }
}