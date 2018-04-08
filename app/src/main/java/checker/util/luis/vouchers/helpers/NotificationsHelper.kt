package checker.util.luis.vouchers.helpers

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Color
import android.os.Build
import checker.util.luis.vouchers.MainActivity
import checker.util.luis.vouchers.R

// we are doing conditionally protected code execution in the constructor
// but the IDE does not give a f*ck
@SuppressLint("NewApi")
internal class NotificationsHelper(context: Context) : ContextWrapper(context) {

    companion object {
        const val BALANCE_CHANNEL = "balance_channel"
    }

    private val mNotificationManager by lazy {
        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    // constructor.Kt
    init {
        /**
         * Registers notification channels, which can be used later by individual notifications.
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val balanceChannel = NotificationChannel(
                BALANCE_CHANNEL,
                getString(R.string.notification_channel_balance),
                NotificationManager.IMPORTANCE_HIGH
            )

            balanceChannel.lightColor = Color.CYAN
            balanceChannel.setShowBadge(true)

            mNotificationManager.createNotificationChannel(balanceChannel)

        }
    }

    /**
     * Send a notificationBuilder.
     *
     * @param id           The ID of the notificationBuilder
     * *
     * @param notificationBuilder The notificationBuilder object
     */
    fun notify(id: Int, notificationBuilder: Notification.Builder) {
        mNotificationManager.notify(id, notificationBuilder.build())
    }


    /**
     * Get a balance notification
     *
     * Provide the builder rather than the notification it's self as useful for making
     * notification changes.

     * @param title the title of the notification
     * *
     * @param body  the body text for the notification
     * *
     * @return A Notification.Builder configured with the selected channel and details
     */
    @Suppress("DEPRECATION")
    fun getNotificationBalance(title: String, body: String): Notification.Builder {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(applicationContext, BALANCE_CHANNEL)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(smallIcon)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
        } else {
            Notification.Builder(applicationContext)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(smallIcon)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
        }
    }

    /**
     * Create a PendingIntent for opening up the MainActivity when the notification is pressed

     * @return A PendingIntent that opens the MainActivity
     */
    private // The stack builder object will contain an artificial back stack for the
    // started Activity.
    // This ensures that navigating backward from the Activity leads out of
    // your application to the Home screen.
    // Adds the back stack for the Intent (but not the Intent itself)
    // Adds the Intent that starts the Activity to the top of the stack
    val pendingIntent: PendingIntent
        get() {
            val openMainIntent = Intent(this, MainActivity::class.java)
            val stackBuilder = TaskStackBuilder.create(this)
            stackBuilder.addParentStack(MainActivity::class.java)
            stackBuilder.addNextIntent(openMainIntent)
            return stackBuilder.getPendingIntent(0, PendingIntent.FLAG_ONE_SHOT)
        }

    /**
     * Get the small icon for this app

     * @return The small icon resource id
     */
    private val smallIcon: Int
        get() = android.R.drawable.stat_notify_chat

}
