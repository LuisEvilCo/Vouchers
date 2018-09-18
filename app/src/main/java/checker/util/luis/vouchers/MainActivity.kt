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
import android.view.animation.AnimationUtils
import checker.util.luis.vouchers.activities.SettingsActivity
import checker.util.luis.vouchers.helpers.NotificationsHelper
import checker.util.luis.vouchers.helpers.SchedulerHelper
import checker.util.luis.vouchers.recyclerView.BalanceAdapter
import checker.util.luis.vouchers.viewModel.BalanceViewModel
import checker.util.luis.vouchers.vo.Status
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.core.CrashlyticsCore
import io.fabric.sdk.android.Fabric
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.singleTop


class MainActivity : AppCompatActivity() {


    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        // Setting up the notificationsHelper
        NotificationsHelper(this)

        // Set up Crashlytics, disabled for debug builds
        val crashlyticsKit = Crashlytics.Builder()
            .core(CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build())
            .build()

        // Initialize Fabric with the debug-disabled crashlytics.
        val fabricConfig = Fabric.Builder(this)
            .kits(crashlyticsKit)
            .debuggable(true)
            .build()
        Fabric.with(fabricConfig)

        SchedulerHelper(this).schedulePeriodicJob()

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

        // Have the View Model ready for onClickListeners
        val mBalanceViewModel: BalanceViewModel =
            ViewModelProviders.of(this)[BalanceViewModel::class.java]

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val adapter = BalanceAdapter(this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        mBalanceViewModel.getDescResource().observe(
            this,
            Observer { response ->
                response?.let { resource ->
                    when (resource.status) {
                        Status.LOADING -> {
                            // decide if we should display loading screen
                        }
                        Status.ERROR -> {
                            // show error
                        }
                        Status.SUCCESS -> {
                            adapter.updateAdapter(resource.data)
                        }
                    }
                } ?: Log.e(TAG, "Null response")
            }
        )


        fab.setOnClickListener { _ ->
            SchedulerHelper(this).schedulerSyncJob()
            fab.startAnimation(AnimationUtils.loadAnimation(applicationContext, R.anim.rotate))
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
