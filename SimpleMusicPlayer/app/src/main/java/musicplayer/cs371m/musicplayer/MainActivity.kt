package musicplayer.cs371m.musicplayer

import android.content.Intent
import android.graphics.Color
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.coroutines.*
import java.util.*
import kotlin.collections.HashMap
import kotlin.math.floor
import kotlin.properties.Delegates


class MainActivity
    : AppCompatActivity(),
    CoroutineScope by MainScope()
{
    // A repository can be a local database or the network
    //  or a combination of both
    private val repository = Repository()
    private var songResources = repository.fetchData()
    // Create a list from the keys, which are song names
    private var songList = songResources.keys.toMutableList()
    private var songInfo = songResources.values.toMutableList()


    private lateinit var player: MediaPlayer
    private lateinit var adapter: RVDiffAdapter

    //You may need some variables to record the music player's state.
    companion object {
        val songsPlayedKey = "songsPlayed"
        val doLoopKey = "doLoop"
        val loopState = "loopState"
    }

    private var position = 0
    var count = 0
    var enableLoop = false

    private fun initRecyclerViewDividers(rv: RecyclerView) {
        // Remarkably complex to get dividers in a Recyclerview
        val itemDecor = DividerItemDecoration(rv.context, LinearLayoutManager.VERTICAL)
        // Note the use of !! here.  We know that even though getDrawable can return
        // null, it won't in this case.
        itemDecor.setDrawable(ContextCompat.getDrawable(this, (R.drawable.divider))!!)
        rv.addItemDecoration(itemDecor)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(mainToolbar)

        //XXX Write me. Setup adapter.
        adapter = RVDiffAdapter{ clickedView, songName ->
            val index = songList.indexOf(songName)
            if(player.isPlaying){
                player.reset()
                val data = resources.openRawResourceFd(songInfo[index].rawId)
                player.setDataSource(data.fileDescriptor, data.startOffset, data.declaredLength)
                player.prepare()
                player.start()
                data.close()
            }
            else{
                player.reset()
                val data = resources.openRawResourceFd(songInfo[index].rawId)
                player.setDataSource(data.fileDescriptor, data.startOffset, data.declaredLength)
                player.prepare()
                data.close()
            }
            position = index
            count += 1
            player.isLooping = enableLoop
            currentSong.text = songList[position]
            if(position < songList.size -1 ) {
                nextSong.text =  songList[position + 1]
            }
            else{
                nextSong.text = songList[0]
            }
        }

        val layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = layoutManager
        initRecyclerViewDividers(recyclerView)
        adapter.submitList(songInfo)
        adapter.notifyDataSetChanged()
        Log.d("current list", "${adapter.currentList}")

        //XXX Write me. Hook up buttons.
        //play and pause button
        btnPlay.setOnClickListener {
            if(player.isPlaying) {
                player.pause()
                btnPlay.setImageResource(R.drawable.ic_play_arrow_black_24dp)
            } else {
                player.start()
                btnPlay.setImageResource(R.drawable.ic_pause_black_24dp)
                currentSong.text = songList[position]
                if(position < songList.size -1 ) {
                    nextSong.text =  songList[position + 1]
                }
                else{
                    nextSong.text = songList[0]
                }
            }
        }
        //forward button
        btnForward.setOnClickListener {
            if(player.isPlaying){
                if(position < songList.size -1 ){
                    position += 1
                }
                else{
                    position = 0
                }
                player.reset()
                val data = resources.openRawResourceFd(songInfo[position].rawId)
                player.setDataSource(data.fileDescriptor, data.startOffset, data.declaredLength)
                player.prepare()
                currentSong.text = songList[position]
                if(position < songList.size -1 ) {
                    nextSong.text =  songList[position + 1]
                }
                else{
                    nextSong.text = songList[0]
                }
                count += 1
                player.start()
                player.isLooping = enableLoop
            } else{
                if(position < songList.size -1 ){
                    position += 1
                }
                else{
                    position = 0
                }
                player.reset()
                val data = resources.openRawResourceFd(songInfo[position].rawId)
                player.setDataSource(data.fileDescriptor, data.startOffset, data.declaredLength)
                player.prepare()
                currentSong.text = songList[position]
                if(position < songList.size -1 ) {
                    nextSong.text =  songList[position + 1]
                }
                else{
                    nextSong.text = songList[0]
                }
                count += 1
                player.isLooping = enableLoop
            }

        }
        //backward button
        btnBackward.setOnClickListener {
            if(player.isPlaying){
                if(position <= 0){
                    position = songList.size - 1
                }
                else{
                    position -= 1
                }
                player.reset()
                val data = resources.openRawResourceFd(songInfo[position].rawId)
                player.setDataSource(data.fileDescriptor, data.startOffset, data.declaredLength)
                player.prepare()
                currentSong.text = songList[position]
                if(position < songList.size -1 ) {
                    nextSong.text =  songList[position + 1]
                }
                else{
                    nextSong.text = songList[0]
                }
                count+= 1
                player.start()
                player.isLooping = enableLoop
            } else{
                if(position <= 0){
                    position = songList.size - 1
                }
                else{
                    position -= 1
                }
                player.reset()
                val data = resources.openRawResourceFd(songInfo[position].rawId)
                player.setDataSource(data.fileDescriptor, data.startOffset, data.declaredLength)
                player.prepare()
                currentSong.text = songList[position]
                if(position < songList.size -1 ) {
                    nextSong.text =  songList[position + 1]
                }
                else{
                    nextSong.text = songList[0]
                }
                count+= 1
                player.isLooping = enableLoop
            }
        }

        //shuffle button
        shuffleButton.setOnClickListener {
            songResources = songResources.map { it.key to it.value }.shuffled().toMap() as HashMap<String, SongInfo>
            songInfo = songResources.values.toMutableList()
            songList = songResources.keys.toMutableList()
            adapter.submitList(null)
            adapter.submitList(songInfo)
            adapter.notifyDataSetChanged()
            position = songList.indexOf(currentSong.text)
            Log.d("current song list:", "${adapter.currentList}")
            Log.d("shuffle list", "$songList")
            Log.d("position", "$position")
            currentSong.text = songList[position]
            if(position < songList.size -1 ) {
                nextSong.text =  songList[position + 1]
            }
            else{
                nextSong.text = songList[0]
            }
        }

        //XXX Write me. Setup seekbar
        seekBar.isClickable = true
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {}
            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                val currentPosition = seekBar.progress
                player.seekTo(currentPosition)
            }
        })

        //XXX Write me. Setup media player and play the first song
        player = MediaPlayer.create(this, songInfo[0].rawId)
        player.start()
        currentSong.text = songList[position]
        nextSong.text =  songList[position + 1]
        btnPlay.setImageResource(R.drawable.ic_pause_black_24dp)
        player.isLooping = enableLoop
        count += 1
        player.setOnCompletionListener {
            if(position < songList.size -1 ){
                position += 1
            }
            else{
                position = 0
            }
            player.reset()
            val data = resources.openRawResourceFd(songInfo[position].rawId)
            player.setDataSource(data.fileDescriptor, data.startOffset, data.declaredLength)
            player.prepare()
            player.start()
            data.close()
            currentSong.text = songList[position]
            if(position < songList.size -1 ) {
                nextSong.text =  songList[position + 1]
            }
            else{
                nextSong.text = songList[0]
            }
            count += 1
        }

        player.setOnSeekCompleteListener {
           /* if(currentSong.text == songList[position]){

            }else{ count += 1}*/
            if(player.isLooping){
                count+= 1
            }

        }

        // Don't change this code.  We are launching a coroutine (user-level thread) that will
        // execute concurrently with our code, but will update the displayed time
        val millisec = 100L
        launch {
            displayTime(millisec)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cancel()
    }

    // The suspend modifier marks this as a function called from a coroutine
    // Note, this whole function is somewhat reminiscent of the Timer class
    // from Fling and Peck.  We use an independent thread to manage one small
    // piece of our GUI.  This coroutine should not modify any data accessed
    // by the main thread (it can read property values)
    private suspend fun displayTime(misc: Long) {
        // While the coroutine is running and has not been canceled by its parent
        while (coroutineContext.isActive) {
            //XXX Write me, display passed and remaining time, update seek bar
            seekBar.max = player.duration
            seekBar.progress = player.currentPosition
            startTime.text = convertTime(player.currentPosition)
            val diff = player.duration - player.currentPosition
            songEndTime.text = convertTime(diff)
            // Leave this code as is.  it inserts a delay so that this thread does
            // not consume too much CPU
            delay(misc)
        }
    }

    // This method converts time in milliseconds to minutes-second formated string
    private fun convertTime(milliseconds: Int): String {
        val minutes = floor((milliseconds.toDouble() / 1000.0 / 60.0)).toInt()
        val millisecondsRemaining = milliseconds - minutes * 60 * 1000
        val seconds = millisecondsRemaining / 1000

        return String.format("%02d:%02d", minutes, seconds)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.player_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here.
        val id = item.itemId

        return if (id == R.id.action_settings) {
            settingsButton(item)
            true
        } else super.onOptionsItemSelected(item)

    }

    private fun settingsButton(@Suppress("UNUSED_PARAMETER") item: MenuItem) {
        // XXX Write me
        Log.d("count played song: ", "$count")
        val sendIntent = Intent(this, SettingsManager::class.java)
        sendIntent.apply {
            putExtra(songsPlayedKey, count)
            putExtra(loopState, enableLoop)
        }
        startActivityForResult(sendIntent, 1)
    }

    // XXX Write me, handle player dynamics and currently playing/next song
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode ==1 ){
            if(resultCode == RESULT_OK){
                val isLoop = data!!.getBooleanExtra(doLoopKey, false)
                Log.d("is it looping", "$isLoop")
                if(isLoop){
                    loopButton.setBackgroundColor(Color.parseColor("#ff0000"))
                    player.isLooping = true
                    enableLoop = true
                } else{
                    loopButton.setBackgroundColor(Color.parseColor("#ffffff"))
                    player.isLooping = false
                    enableLoop = false
                }

            }
        }
    }
}


