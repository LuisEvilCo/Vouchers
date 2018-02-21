package checker.util.luis.vouchers

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.widget.EditText
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.content_settings.*


class SettingsActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) { //TODO : ding into View Models
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        setSupportActionBar(toolbar)

        val versionName = versionName as TextView
        val versionCode = versionCode as TextView

        versionName.text = "Name : ${BuildConfig.VERSION_NAME}"
        versionCode.text = "Code : ${BuildConfig.VERSION_CODE}"

        val cardEditText = editTextCard as EditText

        val sharedPref : SharedPreferences = getSharedPreferences(getString(R.string.string_preference_file_key), Context.MODE_PRIVATE)
        val card : String = sharedPref.getString(getString(R.string.card), "")

        cardEditText.setText(card)

        fabSettings.setOnClickListener { view ->
            val editor: SharedPreferences.Editor = sharedPref.edit()
            editor.putString(getString(R.string.card), cardEditText.text.toString())
            editor.apply()

            Snackbar.make(view, "Saved", Snackbar.LENGTH_LONG)
                    //.setAction("Action", null)
                    .show()
            finish()
        }
    }
}