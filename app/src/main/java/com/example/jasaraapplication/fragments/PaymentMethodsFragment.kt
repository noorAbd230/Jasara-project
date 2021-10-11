package com.example.jasaraapplication.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.jasaraapplication.R
import kotlinx.android.synthetic.main.fragment_more.view.*
import kotlinx.android.synthetic.main.fragment_payment_methods.*
import kotlinx.android.synthetic.main.fragment_payment_methods.view.*


class PaymentMethodsFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var root = inflater.inflate(R.layout.fragment_payment_methods, container, false)

        root.switchSave.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){
                tvSave.visibility = View.VISIBLE
            }else{
                tvSave.visibility = View.INVISIBLE
            }
        }


        root.payment_method_back.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction().replace(
                R.id.mainContainer,
                MoreFragment()
            ).commit()
        }

            return root
    }


}