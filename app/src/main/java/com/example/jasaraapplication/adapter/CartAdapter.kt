
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
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.jasaraapplication.R
import com.example.jasaraapplication.activites.NumberValidationActivity
import com.example.jasaraapplication.model.Cart
import com.example.jasaraapplication.model.Products
import com.example.jasaraapplication.model.ProductsDiscount
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.cart_item.view.*
import kotlinx.android.synthetic.main.discount_product_item.view.*
import kotlinx.android.synthetic.main.discount_product_item.view.card_linear
import kotlinx.android.synthetic.main.fragment_cart.*
import kotlinx.android.synthetic.main.fragment_cart.view.*
import kotlinx.android.synthetic.main.fragment_category_products_dtails.view.*
import kotlinx.android.synthetic.main.product_item.view.*
import org.json.JSONObject
import java.util.HashMap


class CartAdapter(var activity: Activity, var data: MutableList<Cart>) :
    RecyclerView.Adapter<CartAdapter.MyViewHolder>(){
    var amount = 0
    var sum = 0.0
    companion object{
       lateinit var cartId :String

    }
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title = itemView.cart_product_name
        val weight = itemView.cart_product_weight
        val price = itemView.cart_product_price
        val category = itemView.cart_category_name
        val image = itemView.cart_img
        val add = itemView.cart_add
        val dropdownWeight = itemView.dropdown_weight
        val quantity = itemView.cart_quantity
        val minus = itemView.cart_minus
        val card = itemView.card_cart
        val checkBox = itemView.checkBox

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(activity).inflate(R.layout.cart_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {


        cartId = data[position].id
        holder.title.text =  data[position].title
        holder.weight.text =  data[position].weight+"kg"
        holder.category.text =  data[position].category
        holder.price.text =  "$"+data[position].price
        Picasso.get().load(data[position].img).error(R.drawable.category1).into(holder.image)
        sum+= data[position].weight.toDouble() * data[position].price.toDouble()

        activity.totalSell.text = "$$sum"

        holder.add.setOnClickListener {
            amount+=1
            holder.quantity.text = amount.toString()
            holder.price.text =  "$"+(data[position].price.toInt() * amount)
            sum+=(data[position].price.toInt() * amount)
            updateCart()
            activity.totalSell.text = "$$sum"
        }

        holder.minus.setOnClickListener {
            amount-=1
            holder.quantity.text = amount.toString()
            holder.price.text =  "$"+(data[position].price.toInt() * amount)
            sum-=(data[position].price.toInt() * amount)
            updateCart()
            activity.totalSell.text = "$$sum"
        }

        holder.card.setOnLongClickListener {
            holder.checkBox.visibility = View.VISIBLE
            true
        }

        holder.card.setOnClickListener {
            holder.checkBox.visibility = View.INVISIBLE
        }


    }


    private fun updateCart(){
        val url="https://jazara.applaab.com/api/updateCart"
        val queue= Volley.newRequestQueue(this.activity)
        val request = object : StringRequest(
            Request.Method.POST,url,
            Response.Listener { response ->
                Log.e("updateAddress","$response")

            },
            Response.ErrorListener { error ->
                Toast.makeText(activity,error.message, Toast.LENGTH_SHORT).show()
            }){
            override fun getParams(): MutableMap<String, String> {
                var params = HashMap<String, String>()
                params["cart_id"] = cartId
                params["amount"] = amount.toString()
                params["device_token"] = NumberValidationActivity.deviceToken
                return params
            }
            override fun getHeaders(): MutableMap<String, String> {
                val headers =
                    HashMap<String, String>()

                headers["User-Agent"] = "Mozilla/5.0"
                return headers
            }


        }



        queue.add(request)

    }

}