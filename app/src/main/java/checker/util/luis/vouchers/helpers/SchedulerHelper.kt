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
        const val syncJobID: Int = 1
        const val periodicJobID: Int = 2
        private const val TAG = "SchedulerHelper"
        lateinit var serviceComponent: ComponentName
        val periodicInterval: Long = 15 * TimeUnit.MINUTES.toMillis(1)
        val periodicFlex: Long = (periodicInterval * 0.7).toLong()  // 7 % of flex
    }

    init {
        serviceComponent = ComponentName(context, CheckBalanceService::class.java)
    }

    fun schedulerSyncJob() {
        val jobService = (context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler)
        val builder = getSyncJobBuilder()
        Log.d(TAG, "Scheduling Sync Job")
        jobService.schedule(builder.build())
        Log.d(TAG, jobService.allPendingJobs.size.toString())
    }

    fun schedulePeriodicJob() {
        val jobService = (context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler)
        val builder = getPeriodicJobBuilder()
        Log.d(TAG, "Scheduling Periodic Job") // TODO  log this to fabric ?
        jobService.schedule(builder.build())
        Log.d(TAG, jobService.allPendingJobs.size.toString())
    }

    private fun getSyncJobBuilder(): JobInfo.Builder {
        val builder = JobInfo.Builder(syncJobID, serviceComponent)
            .setOverrideDeadline(1 * TimeUnit.MINUTES.toMillis(1))
            .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
            .setRequiresDeviceIdle(false)
            .setRequiresCharging(false)
            .setPersisted(true)

        builder.run {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                setRequiresBatteryNotLow(true)
            }
        }
        return builder
    }

    private fun getPeriodicJobBuilder(): JobInfo.Builder {
        val builder = JobInfo.Builder(periodicJobID, serviceComponent)
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