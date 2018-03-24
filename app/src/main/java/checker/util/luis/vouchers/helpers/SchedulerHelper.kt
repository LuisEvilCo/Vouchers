package checker.util.luis.vouchers.helpers

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.os.Build
import android.util.Log
import checker.util.luis.vouchers.services.CheckBalanceService
import java.util.concurrent.TimeUnit

class SchedulerHelper(val context: Context) {

    private companion object {
        const val jobID: Int = 1
        private const val TAG = "SchedulerHelper"
        lateinit var serviceComponent: ComponentName
        val periodicInterval: Long = 5 * TimeUnit.MINUTES.toMillis(1)
        val periodicFlex: Long = (periodicInterval * 0.7).toLong()  // 7 % of flex
    }

    init {
        serviceComponent = ComponentName(context, CheckBalanceService::class.java)
    }

    fun scheduleJob() {
        val builder = getJobBuilder()
        Log.d(TAG, "Scheduling Job")
        (context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler).schedule(builder.build())
    }

    private fun getJobBuilder(): JobInfo.Builder {
        val builder = JobInfo.Builder(jobID, serviceComponent)
            .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
            .setRequiresDeviceIdle(false)
            .setRequiresCharging(false)
            .setPersisted(true)

        builder.run {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                setRequiresBatteryNotLow(true)
                setPeriodic(periodicInterval, periodicFlex)

            } else {
                setPeriodic(periodicInterval)
            }
        }

        return builder
    }
}