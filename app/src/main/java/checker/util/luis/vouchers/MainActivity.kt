package checker.util.luis.vouchers

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import checker.util.luis.vouchers.model.Balance
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jetbrains.anko.design.longSnackbar
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.singleTop
import org.jetbrains.anko.uiThread


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        var balance : Balance? = null

        fab.setOnClickListener { view ->
            doAsync {
                val sharedPref : SharedPreferences = getSharedPreferences(getString(R.string.string_preference_file_key), Context.MODE_PRIVATE)
                val card : String = sharedPref.getString(getString(R.string.card), "")

                if(card.isNotEmpty()) {
                    //doPost(card)
                    balance = VoucherClient.getBalance(card)
                }
                uiThread {
                    longSnackbar(view, "Updated " + balance?.amount)
                    MainText.text = "${balance?.name} : ${balance?.amount}"
                }
            }
        }
    }

    override fun onResume() {
        val sharedPref : SharedPreferences = getSharedPreferences(getString(R.string.string_preference_file_key), Context.MODE_PRIVATE)
        val card : String = sharedPref.getString(getString(R.string.card), "")

        if (card.isEmpty()){
            startActivity(intentFor<SettingsActivity>().singleTop())
        }

        // all db work should be off the main thread
//        val db = Room.databaseBuilder(
//            applicationContext,
//            AppDatabase::class.java, "vouchers"
//        ).build()

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
