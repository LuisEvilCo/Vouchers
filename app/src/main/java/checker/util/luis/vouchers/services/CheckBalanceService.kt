package checker.util.luis.vouchers.services

import android.app.Service
import android.app.job.JobParameters
import android.app.job.JobService
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import checker.util.luis.vouchers.helpers.NotificationsHelper
import checker.util.luis.vouchers.repository.BalanceRepository
import checker.util.luis.vouchers.viewModel.BalanceViewModel
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

        return true
    }

    override fun onStartJob(params: JobParameters): Boolean {

        val mNotificationsHelper = NotificationsHelper(this)

        val mRepository = BalanceRepository(application)

        mRepository.fetchData(this) // triggering background job to update from the server

        val latestRecord = mRepository.getLatest() // always null 

        val mNotificationBuilder = if ( latestRecord != null ) {
            mNotificationsHelper.getNotificationBalance(
                title = latestRecord.amount,
                body = latestRecord.name
            )
        } else {
            mNotificationsHelper.getNotificationBalance(
                title = "empty record",
                body = "oh oh"
            )
        }

        mNotificationsHelper.notify(
            id = Random().nextInt(),
            notificationBuilder = mNotificationBuilder
        )

        jobFinished(params, true)

        return false
    }
}