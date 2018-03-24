package checker.util.luis.vouchers.services

import android.app.Service
import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Intent
import checker.util.luis.vouchers.helpers.NotificationsHelper
import java.util.*

class CheckBalanceService : JobService() {

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        return Service.START_STICKY // vs START_NO_STICKY ?
    }

    override fun onStopJob(params: JobParameters): Boolean {
        val mNotificationsHelper = NotificationsHelper(this)

        mNotificationsHelper.notify(
            id = Random().nextInt(),
            notification = mNotificationsHelper.getNotificationBalance(
                title = "onStopJob",
                body = "bye"
            )
        )

        return true
    }

    override fun onStartJob(params: JobParameters): Boolean {
        val mNotificationsHelper = NotificationsHelper(this)

        mNotificationsHelper.notify(
            id = Random().nextInt(),
            notification = mNotificationsHelper.getNotificationBalance(
                title = "onStartJob",
                body = "hello"
            )
        )

        jobFinished(params, true)

        return false
    }
}