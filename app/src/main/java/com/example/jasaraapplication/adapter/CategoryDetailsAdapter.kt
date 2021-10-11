
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.jasaraapplication.R
import com.example.jasaraapplication.activites.FormActivity
import com.example.jasaraapplication.activites.HomeActivity
import com.example.jasaraapplication.activites.NumberValidationActivity
import com.example.jasaraapplication.fragments.CategoryDetailsFragment
import com.example.jasaraapplication.fragments.CategoryProductsDtailsFragment
import com.example.jasaraapplication.fragments.HomeFragment
import com.example.jasaraapplication.model.Category
import com.example.jasaraapplication.model.CategoryDetails
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_form.*
import kotlinx.android.synthetic.main.category_details_item.view.*
import kotlinx.android.synthetic.main.category_item.view.*
import kotlinx.android.synthetic.main.category_item.view.card_category
import kotlinx.android.synthetic.main.discount_product_item.view.*
import kotlinx.android.synthetic.main.product_item.view.*


class CategoryDetailsAdapter(var activity: Activity, var data: MutableList<CategoryDetails>,var fragment : FragmentManager) :
    RecyclerView.Adapter<CategoryDetailsAdapter.MyViewHolder>() {

    var favorite = 0
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title = itemView.category_details_Name
        val image = itemView.category_details_Img
        val price = itemView.category_price
        val card = itemView.card_category_details
        val favorite = itemView.product_favorite


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(activity).inflate(R.layout.category_details_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.title.text =  data[position].title
        holder.price.text =  data[position].price+"$/KG"
        Picasso.get().load(data[position].img).error(R.drawable.banana).into(holder.image)
        holder.card.setBackgroundColor(Color.parseColor(data[position].color))
        holder.card.setOnClickListener {
            fragment.beginTransaction().replace(
                R.id.mainContainer,
                CategoryProductsDtailsFragment()
            ).addToBackStack(null).commit()

            val editor: SharedPreferences.Editor =
                activity.getSharedPreferences("sendCategoryDetails", Context.MODE_PRIVATE).edit()
            editor.putString("name",data[position].title)
            editor.putString("id",data[position].id)
            editor.putString("img",data[position].img)
            editor.putString("price",data[position].price)
            editor.putInt("favorite",favorite)
            editor.putString("from","category")
            editor.putString("desc",data[position].description)
            editor.apply()

        }

        favorite = 0
        if (favorite==0){
            holder.favorite.setImageResource(R.drawable.ic_baseline_favorite_border_24)
        }else{
            holder.favorite.setImageResource(R.drawable.ic_baseline_favorite_24)

        }

        holder.favorite.setOnClickListener {
            if (favorite==0){
                favorite = 1
                addFavorite(data[position].id)
                holder.favorite.setImageResource(R.drawable.ic_baseline_favorite_24)
            }else{
                favorite = 0
                holder.favorite.setImageResource(R.drawable.ic_baseline_favorite_border_24)
            }

        }




    }

    private fun addFavorite(id:String){

        val url="https://jazara.applaab.com/api/addFigure"


        val queue= Volley.newRequestQueue(activity)
        val request = object : StringRequest(
            Request.Method.POST,url,
            Response.Listener { response ->
                Log.e("add Favorite", response)
            },
            Response.ErrorListener { error ->
                Toast.makeText(activity,"${error.message}", Toast.LENGTH_SHORT).show()
            }){

            override fun getParams(): MutableMap<String, String> {

                var params = HashMap<String, String>()

                params["offer_id"] = ""
                params["product_id"] = id

                return params
            }



            override fun getHeaders(): MutableMap<String, String> {
                val headers =
                    HashMap<String, String>()
                headers["Authorization"] = "Bearer "+ NumberValidationActivity.token
                headers["Accept"] = "application/json"

                return headers
            }


        }
        queue.add(request)

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
                    java.util.HashMap<String, String>()

                headers["Authorization"] = "Bearer "+NumberValidationActivity.token
                headers["Accept"] = "application/json"
                return headers
            }


        }



        queue.add(request)

    }


}