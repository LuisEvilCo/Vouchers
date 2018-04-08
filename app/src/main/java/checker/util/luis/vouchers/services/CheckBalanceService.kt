package checker.util.luis.vouchers.services

import android.app.Service
import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Context
import android.content.Intent
import android.util.Log
import checker.util.luis.vouchers.R
import checker.util.luis.vouchers.VoucherClient
import checker.util.luis.vouchers.database.entity.BalanceEntity
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
        val mNotificationsHelper = NotificationsHelper(this)

        mNotificationsHelper.notify(
            id = Random().nextInt(),
            notificationBuilder = mNotificationsHelper.getNotificationBalance(
                title = "onStopJob",
                body = "bye"
            )
        )

        // TODO, add fabric logging to this event
        return true
    }

    override fun onStartJob(params: JobParameters): Boolean {

        val mServiceContext = this
        var balanceEntity: BalanceEntity?

        doAsync {
            val sharedPref = mServiceContext.getSharedPreferences(
                mServiceContext.getString(R.string.string_preference_file_key),
                Context.MODE_PRIVATE
            )
            val card: String = sharedPref.getString(mServiceContext.getString(R.string.card), "")

            if (card.isNotEmpty()) {
                balanceEntity = VoucherClient.getBalanceEntity(card)

                balanceEntity?.let { netWorkData ->
                    val mRepository = BalanceRepository(application)
                    val latestRecord = mRepository.getLatest()

                    if (latestRecord?.hasChange(netWorkData) == true) {
                        mRepository.addRecord(netWorkData)
                        val mNotificationsHelper = NotificationsHelper(mServiceContext)

                        mRepository.addRecord(netWorkData)
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
                    }
                } ?: Log.w(TAG, "no data fetched from the network")
            }
        }

        jobFinished(params, false)

        return true // the doAsync block it's still going
    }
}