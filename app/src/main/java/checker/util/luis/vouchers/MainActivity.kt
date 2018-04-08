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
import android.view.Menu
import android.view.MenuItem
import checker.util.luis.vouchers.helpers.NotificationsHelper
import checker.util.luis.vouchers.helpers.SchedulerHelper
import checker.util.luis.vouchers.recyclerView.BalanceAdapter
import checker.util.luis.vouchers.viewModel.BalanceViewModel
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.core.CrashlyticsCore
import io.fabric.sdk.android.Fabric
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.singleTop


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

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        val adapter = BalanceAdapter(this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        mBalanceViewModel.getDesc().observe(
            this,
            Observer(adapter::updateAdapter)
        )


        fab.setOnClickListener { _ ->
            SchedulerHelper(this).schedulerSyncJob()
        }

        SchedulerHelper(this).schedulePeriodicJob()

    }

    override fun onResume() { // TODO , consider re - binding the view model here
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
