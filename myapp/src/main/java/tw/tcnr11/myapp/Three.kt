package tw.tcnr11.myapp
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

class Three : AppCompatActivity() {
    var post:Post?=null
    var img:ImageView?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_three)
        val intent = intent
         post= getIntent().getSerializableExtra("post") as Post
        setupViewComponent()
    }

    private fun setupViewComponent() {
        val actionBar = supportActionBar
        actionBar!!.setDisplayHomeAsUpEnabled(true)
        actionBar.setTitle("Back")
        var t001: TextView = findViewById(R.id.t001)
        var t002 = findViewById<TextView>(R.id.t002)
        var t003 = findViewById<TextView>(R.id.t003)
        var t004 = findViewById<TextView>(R.id.t004)
         img = findViewById<ImageView>(R.id.img1)
        t001!!.text = setmonth(post!!.date.toString())
        t002!!.text=post!!.title
        t003!!.text=post!!.copyright.toString()
        t004!!.text=post!!.description.toString()

        ///.......................................................................
        GlobalScope.launch {
            withContext(Dispatchers.IO){
              var bit:Bitmap?=  getBitmapFromURL(post!!.hdurl.toString())
                setimg(bit)
            }
        }

    }
    private  suspend fun setimg(bit:Bitmap?){
        withContext(Dispatchers.Main){
            img!!.setImageBitmap(bit)
        }

    }
    fun setmonth(s: String): String {
        val str = s.split("-".toRegex()).toTypedArray()
        when (str[1]) {
            "1" -> str[1] = "Jan."
            "2" -> str[1] = "Feb."
            "3" -> str[1] = "Mar."
            "4" -> str[1] = "Apr."
            "5" -> str[1] = "May."
            "6" -> str[1] = "Jun."
            "7" -> str[1] = "Jul."
            "8" -> str[1] = "Aug."
            "9" -> str[1] = "Sep."
            "10" -> str[1] = "Oct."
            "11" -> str[1] = "Nov."
            "12" -> str[1] = "Dec."
        }
        return str[0] + " " + str[1] + " " + str[2]
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }
    companion object {
       fun getBitmapFromURL(imageUrl: String): Bitmap? {
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