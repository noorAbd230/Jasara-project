
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.jasaraapplication.R
import com.example.jasaraapplication.activites.FormActivity
import com.example.jasaraapplication.activites.NumberValidationActivity
import com.example.jasaraapplication.fragments.CartFragment
import com.example.jasaraapplication.fragments.MapFragment
import com.example.jasaraapplication.fragments.SettingsFragment
import com.example.jasaraapplication.fragments.UserAddressesFragment
import com.example.jasaraapplication.model.Addresses
import com.example.jasaraapplication.model.Cart
import com.example.jasaraapplication.model.Products
import com.example.jasaraapplication.model.ProductsDiscount
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.cart_item.view.*
import kotlinx.android.synthetic.main.discount_product_item.view.*
import kotlinx.android.synthetic.main.discount_product_item.view.card_linear
import kotlinx.android.synthetic.main.fragment_category_products_dtails.view.*
import kotlinx.android.synthetic.main.product_item.view.*
import kotlinx.android.synthetic.main.user_address_item.view.*
import org.json.JSONObject
import java.util.HashMap


class AddressesAdapter(var activity: Activity, var data: MutableList<Addresses>,var fragment : FragmentManager) :
    RecyclerView.Adapter<AddressesAdapter.MyViewHolder>(){

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title = itemView.address_title
        val edit = itemView.update_address
        val delete = itemView.delete_address
        val card = itemView.addresses_card


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(activity).inflate(R.layout.user_address_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {


        holder.title.text =  data[position].title

        holder.edit.setOnClickListener {
            val editor: SharedPreferences.Editor =
                activity.getSharedPreferences("editAddress", Context.MODE_PRIVATE).edit()
            editor.putString("id",data[position].id)
            editor.putString("title",data[position].title)
            editor.putString("lat",data[position].lat)
            editor.putString("lang",data[position].lang)
            editor.putString("edit","Yes")
            editor.apply()
            fragment.beginTransaction().replace(
                R.id.mainContainer,
                MapFragment()
            ).commit()
        }
        holder.delete.setOnClickListener {
            deleteAddress(data[position].id,position,holder)
        }

        holder.card.setOnClickListener {
            fragment.beginTransaction().replace(
                R.id.mainContainer,
                CartFragment()
            ).commit()

            val pEditor: SharedPreferences.Editor =
                activity.getSharedPreferences("fragmentsPref", Context.MODE_PRIVATE).edit()
            pEditor.putString("from","cart")
            pEditor.apply()
            val editor: SharedPreferences.Editor =
                activity.getSharedPreferences("UserAddress", Context.MODE_PRIVATE).edit()
            editor.putString("id",data[position].id)
            editor.putString("lat",data[position].lat)
            editor.putString("lang",data[position].lang)
            editor.putBoolean("FromUserAddress",true)
            editor.apply()
        }


    }



    private fun deleteAddress(id:String,position: Int,holder: MyViewHolder){
        val url="https://jazara.applaab.com/api/addresses/$id"
        val queue= Volley.newRequestQueue(activity)
        val request = object : StringRequest(
            Request.Method.DELETE,url,
            Response.Listener { response ->
                // val data=  JSONObject(response)
                Log.e("deleteAddress","$response")
                data.removeAt(position)
                notifyItemRemoved(position)
                notifyItemRangeChanged(holder.adapterPosition,data.size)

            },
            Response.ErrorListener { error ->
                Toast.makeText(activity,error.message, Toast.LENGTH_SHORT).show()
            }){

            override fun getHeaders(): MutableMap<String, String> {
                val headers =
                    HashMap<String, String>()

                headers["Authorization"] = "Bearer "+NumberValidationActivity.token
                headers["Accept"] = "application/json"
                return headers
            }


        }



        queue.add(request)

    }

}