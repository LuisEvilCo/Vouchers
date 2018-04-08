package checker.util.luis.vouchers.services

import android.app.Service
import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Intent
import checker.util.luis.vouchers.helpers.NotificationsHelper
import checker.util.luis.vouchers.repository.BalanceRepository
import org.jetbrains.anko.doAsync
import java.util.*

class CheckBalanceService : JobService() {

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

        doAsync {
            val mNotificationsHelper = NotificationsHelper(mServiceContext)

            val mRepository = BalanceRepository(application)

            mRepository.fetchData(mServiceContext) // triggering network call / db update

            val latestRecord = mRepository.getLatest()

            if(latestRecord != null) {
                val mNotificationBuilder = mNotificationsHelper.getNotificationBalance(
                    title = latestRecord.amount,
                    body = latestRecord.name
                )
                mNotificationsHelper.notify(
                    id = Random().nextInt(),
                    notificationBuilder = mNotificationBuilder
                )
            } else {
                // TODO , add fabric logging of this event
            }
        }

        jobFinished(params, false)

        return true // the doAsync block it's still going
    }
}