package checker.util.luis.vouchers

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import checker.util.luis.vouchers.database.entity.BalanceEntity
import checker.util.luis.vouchers.helpers.NotificationsHelper
import checker.util.luis.vouchers.recyclerView.BalanceAdapter
import checker.util.luis.vouchers.viewModel.BalanceViewModel
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.core.CrashlyticsCore
import io.fabric.sdk.android.Fabric
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.singleTop
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var mNotificationsHelper: NotificationsHelper

    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        // Setting up the notificationsHelper
        mNotificationsHelper = NotificationsHelper(this)

        // Set up Crashlytics, disabled for debug builds
        val crashlyticsKit = Crashlytics.Builder()
            .core(CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build())
            .build()

        // Initialize Fabric with the debug-disabled crashlytics.
        Fabric.with(this, crashlyticsKit)

        // Have the View Model ready for onClickListeners
        val mBalanceViewModel: BalanceViewModel =
            ViewModelProviders.of(this)[BalanceViewModel::class.java]

        fab.setOnClickListener { _ ->
            mNotificationsHelper.notify(
                id = Random().nextInt(),
                notification = mNotificationsHelper.getNotificationBalance(
                    title = "title",
                    body = "hey jude"
                )
            )

            var balanceEntity: BalanceEntity?
            doAsync {
                val sharedPref: SharedPreferences = getSharedPreferences(getString(R.string.string_preference_file_key), Context.MODE_PRIVATE)
                val card: String = sharedPref.getString(getString(R.string.card), "")

                if(card.isNotEmpty()) {
                    balanceEntity = VoucherClient.getBalanceEntity(card)
                    balanceEntity?.let { notNullCall -> mBalanceViewModel.addRecord(notNullCall) }
                }
            }
        }
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        val adapter = BalanceAdapter(this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        mBalanceViewModel.getDesc().observe(this,
            Observer(adapter::updateAdapter)
        )

        doAsync {
            val delayMillis = 1500L
            mBalanceViewModel.deleteAll()
            Thread.sleep(delayMillis)

            val b1 = BalanceEntity(name = "test", amount = "hey jude")
            mBalanceViewModel.insert(b1)
            Thread.sleep(delayMillis)

            mBalanceViewModel.delete(b1)
            Thread.sleep(delayMillis)

            val b2 = BalanceEntity(name = "hey", amount = "jude")
            mBalanceViewModel.insert(b2)
            Thread.sleep(delayMillis)

            mBalanceViewModel.delete(b1)
            mBalanceViewModel.delete(b2)
            Thread.sleep(delayMillis)

            mBalanceViewModel.insert(b1,b2)
            Thread.sleep(delayMillis)

            mBalanceViewModel.deleteAll()
            Thread.sleep(delayMillis)

            mBalanceViewModel.insert(
                listOf(b1,b2)
            )

        }


    }

    override fun onResume() {
        val sharedPref: SharedPreferences = getSharedPreferences(
            getString(R.string.string_preference_file_key),
            Context.MODE_PRIVATE
        )
        val card: String = sharedPref.getString(getString(R.string.card), "")

        if (card.isEmpty()) {
            startActivity(intentFor<SettingsActivity>().singleTop())
        }

        super.onResume()
    }

    private fun doPost(cardNumber: String) {
        val url = "https://bd.finutil.com.mx:6443/FinutilSite/rest/cSaldos/actual"
        val client = OkHttpClient()

        val form = FormBody.Builder()
            .add("TARJETA", cardNumber)
            .build()

        val request = Request.Builder()
            .header("Content-Type", "application/x-www-form-urlencoded")
            .url(url)
            .post(form)
            .build()

        val response = client.newCall(request).execute()

        Log.d(TAG, response.body()?.string())

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
