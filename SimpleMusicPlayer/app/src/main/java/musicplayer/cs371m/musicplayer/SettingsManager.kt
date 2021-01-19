package musicplayer.cs371m.musicplayer

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.content_settings.*


class SettingsManager : AppCompatActivity() {
    companion object {
        val doLoopKey = "doLoop"
        val songsPlayedKey = "songsPlayed"
        val loopState = "loopState"
    }

    // XXX probably want a member variable
    private var doLoop = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        // Get a support ActionBar corresponding to this toolbar and enable the Up button
        setSupportActionBar(settingsToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // XXX Write me
        val count = intent.getIntExtra(songsPlayedKey, 0)
        Log.d("count ...", "$count")
        numSongs.text = count.toString()
        val loop = intent.getBooleanExtra(loopState, false)
        //switch check
        Log.d("loop?", "$loop")
        switchBar.isChecked = loop
        doLoop = switchBar.isChecked

        switchBar.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){
                doLoop = true
                switchBar.isChecked = true

            } else {
                doLoop = false
                switchBar.isChecked = false
            }
        }

        ok_button.setOnClickListener {
            okButton()
        }

        cancel_button.setOnClickListener {
           cancelButton()
        }
    }

    private fun cancelButton() {
        // XXX Write me
        finish()
    }

    private fun okButton() {
        // XXX Write me
        Log.d("do loop in ok", "$doLoop")
        doFinish(doLoop)
    }

    // Return to MainActivity
    private fun doFinish(loop: Boolean) {
        // XXX Write me.  This function contains most of the "code" in this activity
        val returnIntent = Intent().putExtra(doLoopKey, loop)
        setResult(RESULT_OK, returnIntent)
        finish()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId

        return if (id == android.R.id.home) {
            // If user clicks "up", then it it as if they clicked OK.  We commit
            // changes and return to parent
            okButton()
            true
        } else super.onOptionsItemSelected(item)

    }
}
