package checker.util.luis.vouchers.services

import android.app.Service
import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Context
import android.content.Intent
import android.util.Log
import checker.util.luis.vouchers.R
import checker.util.luis.vouchers.VoucherClient
import checker.util.luis.vouchers.helpers.NotificationsHelper
import checker.util.luis.vouchers.repository.BalanceRepository
import org.jetbrains.anko.doAsync
import java.util.*

class CheckBalanceService : JobService() {

    private companion object {
        const val TAG = "CheckBalanceService"
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        return Service.START_STICKY // vs START_NO_STICKY ?
    }

    override fun onStopJob(params: JobParameters): Boolean {
        //Answers.getInstance().logCustom(CustomEvent(TAG).putCustomAttribute("onStop","Job Stopped"))
        return true
    }

    override fun onStartJob(params: JobParameters): Boolean {
        val mServiceContext = this

        doAsync {
            val sharedPref = mServiceContext.getSharedPreferences(
                mServiceContext.getString(R.string.string_preference_file_key),
                Context.MODE_PRIVATE
            )
            val card: String = sharedPref.getString(mServiceContext.getString(R.string.card), "")

            if (card.isNotEmpty()) {
                val balanceEntity = VoucherClient.getBalanceEntity(card)

                balanceEntity?.let { netWorkData ->
                    val mRepository = BalanceRepository(application)
                    val latestRecord = mRepository.getLatest()

                    mRepository.addRecord(netWorkData)

                    val mNotificationsHelper = NotificationsHelper(mServiceContext)

                    if (latestRecord?.hasChange(netWorkData) == true) {
                        val mNotificationBuilder = mNotificationsHelper.getNotificationBalance(
                            title = netWorkData.amount,
                            body = netWorkData.name
                        )

                        mNotificationsHelper.notify(
                            id = Random().nextInt(),
                            notificationBuilder = mNotificationBuilder
                        )
                    } else {
                        Log.d(TAG, "no change has been detected on the balance")

                        val dev = sharedPref.getBoolean(
                            mServiceContext.getString(R.string.devSwitch),
                            false
                        )

                        if (dev) {
                            latestRecord?.let { db ->
                                val mNotificationBuilder =
                                    mNotificationsHelper.getNotificationBalance(
                                        title = db.amount,
                                        body = db.name
                                    )

                                mNotificationsHelper.notify(
                                    id = 42,
                                    notificationBuilder = mNotificationBuilder
                                )
                            }
                        }
                    }
                } //?: Answers.getInstance().logCustom(CustomEvent(TAG).putCustomAttribute("change", "network fail"))
            }
        }

        jobFinished(params, false)

        return true // the doAsync block it's still going
    }
}