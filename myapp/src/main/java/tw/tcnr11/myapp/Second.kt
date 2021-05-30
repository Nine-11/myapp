package tw.tcnr11.myapp
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONArray
import org.json.JSONException
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.MalformedURLException
import java.net.URL
import java.util.*
import java.util.concurrent.ExecutionException

class Second : AppCompatActivity() {
    private var mList: ArrayList<Map<String, Any>>? = null
    private var recyclerView: RecyclerView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)
        setupViewComponent()
    }

    private fun setupViewComponent() {
        recyclerView = findViewById<View>(R.id.recyclerView) as RecyclerView
        setDatatolist()
        val actionBar = supportActionBar
        actionBar!!.setDisplayHomeAsUpEnabled(true)
        actionBar.title = "Back"
    }

    private fun setDatatolist() { // 放JSON到recycleview
        //==================================
        u_importopendata() //下載Opendata (or MySQL)
        //==================================
        //設定Adapter (to listview, recycleview)
        val mData = ArrayList<Post>()
        for (m in mList!!) {
            if (m != null) {
                var title1= m["title"].toString().trim { it <= ' ' }
                val url1 = m["url"].toString().trim { it <= ' ' }
                val hdurl1 = m["hdurl"].toString().trim { it <= ' ' }
                val date1 = m["date"].toString().trim { it <= ' ' }
                val copyright1= m["copyright"].toString().trim { it <= ' ' }
                val description1= m["description"].toString().trim { it <= ' ' }
                //************************************************************
                //************************************************************
                mData!!.add(Post(title1, url1, hdurl1, date1, copyright1, description1)) // 帶幾個參數就寫幾個

                //************************************************************
            } else {
                return
            }
        }
        val adapter = RecyclerAdapter(this, mData)
        recyclerView!!.layoutManager = GridLayoutManager(this, 4)
        // ************************************
        // ************************************
        adapter.setOnItemClickListener( object :RecyclerAdapter.OnItemClickListener {
            override fun onItemClick(view: View?, position: Int) {
                val post = mData[position]
                var intent = Intent(this@Second, Three::class.java)
                intent.putExtra("post", post)
                startActivity(intent)
            }
        })


        //********************************* ****
        recyclerView!!.adapter = adapter
    }

    //==========================================================
    private fun u_importopendata() { //下載Opendata
        try {
            val Task_opendata: String = TransTask()
                .execute("https://raw.githubusercontent.com/cmmobile/NasaDataSet/main/apod.json")
                .get()
            mList = ArrayList()
            val info = JSONArray(Task_opendata)
            for (i in 0 until info.length()) {
                val item: MutableMap<String, Any> = HashMap()
                val title = info.getJSONObject(i).getString("title") //標題
                val url = info.getJSONObject(i).getString("url") //小圖
                val hdurl = info.getJSONObject(i).getString("hdurl") //大景圖
                val date = info.getJSONObject(i).getString("date") //時間
                val copyright = info.getJSONObject(i).getString("copyright") //版权,
                val description = info.getJSONObject(i).getString("description") //描素,
                item["title"] = title
                item["url"] = url
                item["hdurl"] = hdurl
                item["date"] = date
                item["copyright"] = copyright
                item["description"] = description
                mList!!.add(item)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        } catch (e: ExecutionException) {
            e.printStackTrace()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    //  inner class---------------------------------------------------------------------
    //*********************************************************************
   protected inner class TransTask :
        AsyncTask<String?, Void?, String>() {
        var ans: String? = null
        override  fun doInBackground(vararg params: String?): String? {
            val sb = StringBuilder()
            try {
                val url = URL(params[0])
                val `in` = BufferedReader(
                    InputStreamReader(url.openStream())
                )
                var line = `in`.readLine()

                // 直接過濾寫在這裡(URL條件寫在這邊)
                while (line != null) {
                    sb.append(line)
                    line = `in`.readLine()
                }
            } catch (e: MalformedURLException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            ans = sb.toString()
            //------------
            return ans
        }
        override fun onPostExecute(s: String) {
            super.onPostExecute(s)
            parseJson(s)
        }

        private fun parseJson(s: String) {}

    }
}