import android.app.Activity
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.jasaraapplication.R
import com.example.jasaraapplication.activites.NumberValidationActivity
import com.example.jasaraapplication.fragments.CategoryDetailsFragment
import com.example.jasaraapplication.fragments.ShowsDetailsFragment
import com.example.jasaraapplication.model.Category
import com.example.jasaraapplication.model.Shows
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.category_details_item.view.*
import kotlinx.android.synthetic.main.category_item.view.*
import kotlinx.android.synthetic.main.category_item.view.card_category
import kotlinx.android.synthetic.main.discount_product_item.view.*
import kotlinx.android.synthetic.main.product_item.view.*
import kotlinx.android.synthetic.main.shows_item.view.*


class ShowsAdapter(var activity: Activity, var data: MutableList<Shows>, var fragment : FragmentManager,var from :String) :
    RecyclerView.Adapter<ShowsAdapter.MyViewHolder>() {

    var favorite = 0

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title = itemView.show_Name
        val image = itemView.show_Img
        val card = itemView.card_show
        val cTitle = itemView.category_details_Name
        val cImage = itemView.category_details_Img
        val price = itemView.category_price
        val cCard = itemView.card_category_details
        val favorite = itemView.product_favorite
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return if (from=="favorite screen"){
            val itemView = LayoutInflater.from(activity).inflate(R.layout.category_details_item, parent, false)
            MyViewHolder(itemView)
        }else {
            val itemView = LayoutInflater.from(activity).inflate(R.layout.shows_item, parent, false)
            MyViewHolder(itemView)
        }

        }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        if (from=="favorite screen"){
            holder.favorite.setImageResource(R.drawable.ic_baseline_favorite_24)
            holder.favorite.setOnClickListener {
                if (favorite==0){
                    favorite = 1
                    addFavorite(data[position].id)
                    holder.favorite.setImageResource(R.drawable.ic_baseline_favorite_24)
                }else{
                    favorite = 0
                    holder.favorite.setImageResource(R.drawable.ic_baseline_favorite_border_24)
                    data.removeAt(position)
                    notifyItemRemoved(position)
                    notifyItemRangeChanged(holder.adapterPosition,data.size)
                }

            }
            holder.cTitle.text = data[position].title
            Picasso.get().load(data[position].img).error(R.drawable.category1).into(holder.cImage)
//        holder.image.setImageResource(data[position].img)
            holder.cCard.setOnClickListener {

                fragment.beginTransaction().replace(
                    R.id.mainContainer,
                    ShowsDetailsFragment()
                ).addToBackStack(null).commit()
                val editor: SharedPreferences.Editor =
                    activity.getSharedPreferences("Shows Details", MODE_PRIVATE).edit()
                editor.putString("fName", data[position].title)
                editor.putString("fImg", data[position].img)
                editor.putString("fPrice", data[position].price)
                editor.putString("fDate", data[position].date)
                editor.putString("fDesc", data[position].desc)
                editor.putString("fId", data[position].id)
                editor.putInt("favorite", favorite)
                editor.putString("fav", "true")
                editor.apply()
            }
        }else {
            holder.title.text = data[position].title
            Picasso.get().load(data[position].img).error(R.drawable.category1).into(holder.image)
//        holder.image.setImageResource(data[position].img)
            holder.card.setOnClickListener {

                fragment.beginTransaction().replace(
                    R.id.mainContainer,
                    ShowsDetailsFragment()
                ).addToBackStack(null).commit()
                val editor: SharedPreferences.Editor =
                    activity.getSharedPreferences("Shows Details", MODE_PRIVATE).edit()
                editor.putString("name", data[position].title)
                editor.putString("img", data[position].img)
                editor.putString("price", data[position].price)
                editor.putString("date", data[position].date)
                editor.putString("desc", data[position].desc)
                editor.putString("id", data[position].id)
                editor.putString("fav", "false")
                editor.apply()
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


}