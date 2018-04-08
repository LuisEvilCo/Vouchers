package checker.util.luis.vouchers

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.EditText
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.content_settings.*
import org.jetbrains.anko.design.longSnackbar
import org.jetbrains.anko.design.snackbar


class SettingsActivity : AppCompatActivity() {

    companion object {
        private const val onBackDelay = 1000L //ms
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        setSupportActionBar(toolbar)

        val versionName = versionName as TextView
        val versionCode = versionCode as TextView
        val packageName = packageNameText as TextView

        versionName.text = "Name : ${BuildConfig.VERSION_NAME}"
        versionCode.text = "Code : ${BuildConfig.VERSION_CODE}"
        packageName.text = this.applicationContext.packageName

        val cardEditText = editTextCard as EditText

        val sharedPref: SharedPreferences = getSharedPreferences(
            getString(R.string.string_preference_file_key),
            Context.MODE_PRIVATE
        )
        val card: String = sharedPref.getString(getString(R.string.card), "")

        cardEditText.setText(card)

        fabSettings.setOnClickListener { view ->

            val cardString = cardEditText.text.toString()

            if (cardString.isEmpty()) {
                this.onBackPressed()
            } else {
                val editor: SharedPreferences.Editor = sharedPref.edit()
                editor.putString(getString(R.string.card), cardString)
                editor.apply()

                snackbar(view, "Saved")

                view.postDelayed({
                    finish()
                }, onBackDelay)
            }
        }
    }

    override fun onBackPressed() {

        val sharedPref: SharedPreferences = getSharedPreferences(
            getString(R.string.string_preference_file_key),
            Context.MODE_PRIVATE
        )

        val card: String = sharedPref.getString(getString(R.string.card), "")
        val coordinatorView = this.findViewById<View>(R.id.activity_Settings_Coordinator)

        if (card.isEmpty()) {

            val cardString = (editTextCard as EditText).text.toString()

            if (!cardString.isEmpty()) {

                val editor: SharedPreferences.Editor = sharedPref.edit()
                editor.putString(getString(R.string.card), cardString)
                editor.apply()

                snackbar(coordinatorView, getString(R.string.autoSaving))

                coordinatorView.postDelayed({
                    super.onBackPressed()
                }, onBackDelay)

            } else {
                snackbar(coordinatorView, getString(R.string.card_info))
            }
        } else {
            longSnackbar(coordinatorView, getString(R.string.discarding_changes))
            coordinatorView.postDelayed({
                super.onBackPressed()
            }, onBackDelay)
        }

    }
}