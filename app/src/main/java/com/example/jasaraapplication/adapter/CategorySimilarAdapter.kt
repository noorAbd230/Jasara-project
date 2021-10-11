import android.app.Activity
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.example.jasaraapplication.R
import com.example.jasaraapplication.fragments.CategoryDetailsFragment
import com.example.jasaraapplication.model.Category
import com.example.jasaraapplication.model.CategorySimilar
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.category_item.view.*
import kotlinx.android.synthetic.main.discount_product_item.view.*
import kotlinx.android.synthetic.main.product_item.view.*
import kotlinx.android.synthetic.main.shows_recommended_item.view.*
import kotlinx.android.synthetic.main.similar_category_item.view.*


class CategorySimilarAdapter(var activity: Activity, var data: MutableList<CategorySimilar>,var name : String) :
    RecyclerView.Adapter<CategorySimilarAdapter.MyViewHolder>() {


    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val image = itemView.category_similar_Img
        val showImage = itemView.recommendedImg
        val card = itemView.card_similar_category

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return if(name== "CategorySimilar"){
            val itemView = LayoutInflater.from(activity).inflate(R.layout.similar_category_item, parent, false)
            MyViewHolder(itemView)

        }else{
            val itemView = LayoutInflater.from(activity).inflate(R.layout.shows_recommended_item, parent, false)
            MyViewHolder(itemView)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        if (name== "CategorySimilar"){
            Picasso.get().load(data[position].img).error(R.drawable.category1).into(holder.image)
            holder.card.setOnClickListener {

            }
        }else{
            Picasso.get().load(data[position].img).error(R.drawable.category1).into(holder.showImage)

        }


//
//        holder.card.setOnLongClickListener {
//            holder.card.setCardBackgroundColor(Color.parseColor("#8C2F6964"))
//             true
//        }



    }


}