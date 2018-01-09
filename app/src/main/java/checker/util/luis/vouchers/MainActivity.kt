package checker.util.luis.vouchers

import android.app.PendingIntent.getActivity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*
import android.util.Log
import okhttp3.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            doAsync {
                val sharedPref : SharedPreferences = getSharedPreferences(getString(R.string.string_preference_file_key), Context.MODE_PRIVATE)
                val card : String = sharedPref.getString(getString(R.string.card), "")

                if(card.isNotEmpty()){
                    doPost(card)
                }
                uiThread {
                    Snackbar.make(view, "Updated", Snackbar.LENGTH_LONG)
                            //.setAction("Action", null)
                            .show()
                }
            }
        }
    }

    override fun onResume() {
        val sharedPref : SharedPreferences = getSharedPreferences(getString(R.string.string_preference_file_key), Context.MODE_PRIVATE)
        val card : String = sharedPref.getString(getString(R.string.card), "")

        if (card.isEmpty()){
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        super.onResume()
    }

    private fun doPost(cardNumber: String) {
        val url = "https://bd.finutil.com.mx:6443/FinutilSite/rest/cSaldos/actual"
        val client  = OkHttpClient()

        val form = FormBody.Builder()
                .add("TARJETA" , cardNumber)
                .build()

        val request = Request.Builder()
                .header("Content-Type", "application/x-www-form-urlencoded")
                .url(url)
                .post(form)
                .build()

        val response = client.newCall(request).execute()

        Log.d("RESPONSE", response.body()?.string())

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
