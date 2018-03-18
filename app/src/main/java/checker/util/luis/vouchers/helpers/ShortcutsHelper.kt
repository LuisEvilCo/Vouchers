package checker.util.luis.vouchers.helpers

import android.content.Context
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import android.os.Build
import checker.util.luis.vouchers.MainActivity
import checker.util.luis.vouchers.R
import java.util.*

internal class ShortcutsHelper(context: Context) {

    private val mContext: Context = context

    fun createShorcut() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val shortcutManager = mContext.getSystemService(ShortcutManager::class.java)

            val shortcut = ShortcutInfo.Builder(mContext, "id1")
                .setShortLabel("Short Label")
                .setLongLabel("Hey man, here you have a long label")
                .setIcon(Icon.createWithResource(mContext, R.drawable.ic_credit_card_black_48px))
                .setIntent(Intent(mContext, MainActivity::class.java).setAction("Main Activity"))
                .build()
            shortcutManager.dynamicShortcuts = Arrays.asList(shortcut)
        }
    }
}