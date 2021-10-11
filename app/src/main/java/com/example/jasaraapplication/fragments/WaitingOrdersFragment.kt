package com.example.jasaraapplication.fragments

import OrdersAdapter
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.jasaraapplication.R
import com.example.jasaraapplication.model.Orders
import com.example.jasaraapplication.model.Products
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.android.synthetic.main.fragment_waiting_orders.view.*


class WaitingOrdersFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var root = inflater.inflate(R.layout.fragment_waiting_orders, container, false)


        val product=mutableListOf<Orders>()
        product.add(Orders(" مشمش ",R.drawable.peache,"3","$7",4f))
        product.add(Orders(" مشمش ",R.drawable.apples,"3","$7",4f))
        product.add(Orders(" مشمش ",R.drawable.grap,"3","$7",4f))
        product.add(Orders(" مشمش ",R.drawable.peache,"3","$7",4f))



        root.rvWaitingOrders.layoutManager = LinearLayoutManager(this.context,
            LinearLayoutManager.VERTICAL,false)
        root.rvWaitingOrders.setHasFixedSize(true)
        val productAdapter = OrdersAdapter(this.requireActivity(),product,"waiting orders")
        root.rvWaitingOrders.adapter=productAdapter

        return root
    }


}