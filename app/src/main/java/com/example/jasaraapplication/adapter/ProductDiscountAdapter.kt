
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
import com.example.jasaraapplication.fragments.ShowsDetailsFragment
import com.example.jasaraapplication.model.ProductsDiscount
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.discount_product_item.view.*
import kotlinx.android.synthetic.main.discount_product_item.view.card_linear
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.android.synthetic.main.product_item.view.*
import org.json.JSONObject
import java.util.HashMap


class ProductDiscountAdapter(var activity: Activity, var data: MutableList<ProductsDiscount>,var fragment : FragmentManager) :
    RecyclerView.Adapter<ProductDiscountAdapter.MyViewHolder>() {
    var amount = 0


    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title = itemView.discount_title
        val subtitle = itemView.discount_subtitle
        val rate = itemView.rateNum
        val price = itemView.discount_price
        val image = itemView.discount_img
        val card = itemView.card_linear
        val product_card = itemView.offer_card
        val add = itemView.add_offer
        val plus = itemView.plus_offer
        val quantity = itemView.quantity_offer
        val minus = itemView.minus_offer
        //val addToCart = itemView.addToCart

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(activity).inflate(R.layout.discount_product_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.title.text =  data[position].title
        holder.subtitle.text =  data[position].subTitle
        holder.rate.text =  data[position].rate
        holder.price.text =  "$"+data[position].price

        Picasso.get().load(data[position].img).error(R.drawable.nuts).into(holder.image)
        holder.card.setBackgroundResource(data[position].style)

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




        holder.product_card.setOnClickListener {
            fragment.beginTransaction().replace(
                R.id.mainContainer,
                ShowsDetailsFragment()
            ).addToBackStack(null).commit()

            val editor: SharedPreferences.Editor =
                activity.getSharedPreferences("Shows Details", Context.MODE_PRIVATE).edit()
            editor.putString("hName",data[position].title)
            editor.putString("hId",data[position].id)
            editor.putString("hDate",data[position].date)
            editor.putString("hDesc",data[position].subTitle)
            editor.putString("hImg",data[position].img)
            editor.putString("hPrice",data[position].price)
            editor.putString("fav","home")
            editor.apply()
        }



    }

    private fun addToCart(offerId:String){
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
                params["product_id"] = ""
                params["amount"] = amount.toString()
                params["offer_id"] = offerId

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