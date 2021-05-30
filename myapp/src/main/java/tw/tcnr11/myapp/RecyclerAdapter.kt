package tw.tcnr11.myapp

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

class RecyclerAdapter     //--------------------------------------------1
    (private val mContext: Context, private val mData: ArrayList<Post>) :
    RecyclerView.Adapter<RecyclerAdapter.ViewHolder>(), View.OnClickListener {
    //    -------------------------------------------------------------------
    private var mOnItemClickListener: OnItemClickListener? = null

    //    -------------------------------------------------------------------
    fun setOnItemClickListener(mOnItemClickListener: OnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener
    }

    //-------------------------------------------------------------------2
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater
            .from(mContext) //呈現空間
            .inflate(R.layout.grid, parent, false) //自訂區塊
        val holder: ViewHolder = ViewHolder(view)
        holder.img = view.findViewById<View>(R.id.img) as ImageView
        holder.title = view.findViewById<View>(R.id.t000) as TextView
        view.setOnClickListener(this)
        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post = mData[position]
        holder.title!!.text = post.title

        GlobalScope.launch {
            withContext(Dispatchers.IO){
                var bit:Bitmap?=  getBitmapFromURL(post!!.url.toString())
                withContext(Dispatchers.Main){
                    holder.img!!.setImageBitmap(bit)
                }
            }
        }


//=================================
        holder.itemView.tag = position
    }



    override fun getItemCount(): Int {
        return mData.size
    }

    override fun onClick(v: View) {
        if (mOnItemClickListener != null) {
            //注意這裡使用getTag方法獲取position
            mOnItemClickListener!!.onItemClick(v, v.tag as Int)
        }
    }

    //define interface
    interface OnItemClickListener {
        fun onItemClick(view: View?, position: Int)
    }

    //======= sub class   ==================
    inner class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(
        itemView!!
    ) {
        var title: TextView? = null
        var img: ImageView? = null
    } //-----------------------------------------------

    companion object {
        //--------------------------------------------
        fun getBitmapFromURL(imageUrl: String): Bitmap?{
            return try {
                val url = URL(imageUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.doInput = true
                connection.connect()
                val input = connection.inputStream
                BitmapFactory.decodeStream(input)
            } catch (e: IOException) {
                e.printStackTrace()
                null
            }
        }
    }
}