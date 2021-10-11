import android.app.Activity
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.example.jasaraapplication.R
import com.example.jasaraapplication.fragments.CategoryDetailsFragment
import com.example.jasaraapplication.model.Category
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.category_item.view.*
import kotlinx.android.synthetic.main.discount_product_item.view.*
import kotlinx.android.synthetic.main.product_item.view.*
import java.io.ByteArrayInputStream
import java.io.InputStream


class CategoryAdapter(var activity: Activity, var data: MutableList<Category>,var fragment : FragmentManager) :
    RecyclerView.Adapter<CategoryAdapter.MyViewHolder>() {


    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title = itemView.category_Name
        val image = itemView.category_Img
        val card = itemView.card_category

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(activity).inflate(R.layout.category_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.title.text =  data[position].title
        var url = "https://jazara.applaab.com/storage/"
        Picasso.get().load(url+data[position].img).error(R.drawable.category1).into(holder.image)


        holder.card.setOnClickListener {

            fragment.beginTransaction().replace(
                R.id.mainContainer,
                CategoryDetailsFragment()
            ).addToBackStack(null).commit()
            val editor: SharedPreferences.Editor =
                activity.getSharedPreferences("sendName", MODE_PRIVATE).edit()
                editor.putString("name",data[position].title)
               editor.apply()

        }

        holder.card.setOnLongClickListener {
            holder.card.setCardBackgroundColor(Color.parseColor("#8C2F6964"))
             true
        }



    }


}