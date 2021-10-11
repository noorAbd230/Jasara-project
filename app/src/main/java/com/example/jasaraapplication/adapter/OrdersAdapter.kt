
import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.example.jasaraapplication.R
import com.example.jasaraapplication.model.Cart
import com.example.jasaraapplication.model.Orders
import com.example.jasaraapplication.model.Products
import com.example.jasaraapplication.model.ProductsDiscount
import kotlinx.android.synthetic.main.cart_item.view.*
import kotlinx.android.synthetic.main.cart_item.view.card_cart
import kotlinx.android.synthetic.main.cart_item.view.checkBox
import kotlinx.android.synthetic.main.cart_item.view.dropdown_weight
import kotlinx.android.synthetic.main.discount_product_item.view.*
import kotlinx.android.synthetic.main.discount_product_item.view.card_linear
import kotlinx.android.synthetic.main.order_waiting_item.view.*
import kotlinx.android.synthetic.main.product_item.view.*


class OrdersAdapter(var activity: Activity, var data: MutableList<Orders>,var name:String) :
    RecyclerView.Adapter<OrdersAdapter.MyViewHolder>() {


    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title = itemView.order_product_name
        val weight = itemView.order_product_weight
        val price = itemView.order_price
        val status = itemView.order_status
        val image = itemView.order_img
        val rBar = itemView.rBar
        val repeatOrders = itemView.repeatOrders

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(activity).inflate(R.layout.order_waiting_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.title.text =  data[position].title
        holder.weight.text =  data[position].weight
        holder.price.text =  data[position].price
        holder.rBar.rating = data[position].rate
        holder.image.setImageResource(data[position].img)

        when (name) {
            "waiting orders" -> {
                holder.repeatOrders.visibility = View.GONE
                holder.status.text = "قيد الانتظار"
            }
            "done orders" -> {
                holder.repeatOrders.visibility = View.VISIBLE
                holder.status.text = "مكتملة"
            }
            "canceled orders" -> {
                holder.repeatOrders.visibility = View.GONE
                holder.status.text = "ملغاة"
            }
        }



    }


}