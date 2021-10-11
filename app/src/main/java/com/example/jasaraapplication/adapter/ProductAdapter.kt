
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
import com.example.jasaraapplication.activites.CheckNumberActivity
import com.example.jasaraapplication.activites.NumberValidationActivity
import com.example.jasaraapplication.fragments.CategoryProductsDtailsFragment
import com.example.jasaraapplication.model.Products
import com.example.jasaraapplication.model.ProductsDiscount
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.discount_product_item.view.*
import kotlinx.android.synthetic.main.discount_product_item.view.card_linear
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.android.synthetic.main.product_item.view.*
import org.json.JSONObject
import java.util.HashMap


class ProductAdapter(var activity: Activity, var data: List<Products>,var fragment : FragmentManager) :
    RecyclerView.Adapter<ProductAdapter.MyViewHolder>() {

    var amount = 0

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title = itemView.product_title
        val price = itemView.product_price
        val image = itemView.product_img
        val add = itemView.add
        val plus = itemView.plus
        val quantity = itemView.quantity
        val minus = itemView.minus
        val card = itemView.product_card

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(activity).inflate(R.layout.product_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.title.text =  data[position].title
        holder.price.text =  "$"+data[position].price
        Picasso.get().load(data[position].img).error(R.drawable.banana).into(holder.image)
        holder.add.setOnClickListener {
                       holder.add.visibility = View.GONE
                        holder.plus.visibility = View.VISIBLE
                       holder.minus.visibility = View.VISIBLE
                       holder.quantity.visibility = View.VISIBLE
                       activity.addToCartBtn.visibility = View.VISIBLE
        }


        activity.addToCartBtn.setOnClickListener {
            val prefs =
                activity.getSharedPreferences("userLogin", Context.MODE_PRIVATE)

            var login = prefs.getBoolean("login",false)
            if (login){
                addToCart(data[position].id)
            }else{
                var i= Intent(activity, CheckNumberActivity::class.java)
                activity.startActivity(i)
            }

        }

        holder.plus.setOnClickListener {
            amount+=1
            holder.quantity.text = amount.toString()
        }

        holder.minus.setOnClickListener {
            amount-=1
            holder.quantity.text = amount.toString()
        }



        holder.card.setOnClickListener {
            fragment.beginTransaction().replace(
                R.id.mainContainer,
                CategoryProductsDtailsFragment()
            ).addToBackStack(null).commit()

            val editor: SharedPreferences.Editor =
                activity.getSharedPreferences("sendCategoryDetails", Context.MODE_PRIVATE).edit()
            editor.putString("hName",data[position].title)
            editor.putString("hId",data[position].id)
            editor.putString("hImg",data[position].img)
            editor.putString("hPrice",data[position].price)
            editor.putString("hDesc",data[position].desc)
            editor.putString("from","home")
            editor.apply()
        }

    }

    private fun addToCart(productId:String){
        val url="https://jazara.applaab.com/api/addCart"
        val queue= Volley.newRequestQueue(activity)
        val request = object : StringRequest(
            Request.Method.POST,url,
            Response.Listener { response ->
                val data=  JSONObject(response)
                Log.e("addCart","$data")



            },
            Response.ErrorListener { error ->
                Toast.makeText(activity,error.message, Toast.LENGTH_SHORT).show()
            }){
            override fun getParams(): MutableMap<String, String> {
                var params = HashMap<String, String>()
                params["device_token"] = NumberValidationActivity.deviceToken
                params["product_id"] = productId!!
                params["amount"] = amount.toString()
                params["offer_id"] = ""

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