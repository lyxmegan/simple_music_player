package musicplayer.cs371m.musicplayer

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

// Pass in a function called clickListener that takes a view and a songName
// as parameters.  Call clickListener when the row is clicked.
class RVDiffAdapter(private val clickListener: (View: View, songName: String) -> Unit)
    : ListAdapter<SongInfo,
        RVDiffAdapter.ViewHolder>(Diff())
{

    companion object {
        val TAG = "RVDiffAdapter"
    }

    // ViewHolder pattern minimizes calls to findViewById
    inner class ViewHolder(view: View)
        : RecyclerView.ViewHolder(view) {
        //XXX Write me.
        private var songTitleView = view.findViewById<TextView>(R.id.songTitle)
        private var songTimeView = view.findViewById<TextView>(R.id.songTime)

        init {
            //XXX Write me.
            view.setOnClickListener{
                val selected = "${getItem(adapterPosition).name}"
                Toast.makeText(it.context, selected, Toast.LENGTH_SHORT).show()
                clickListener.invoke(it,getItem(adapterPosition).name )
            }

        }
        fun bind(item: SongInfo) {
            //XXX Write me.
            songTitleView.text = item.name
            songTimeView.text = item.time
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        //XXX Write me
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.song_row, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //XXX Write me.
        holder.bind(getItem(position))
    }

    class Diff : DiffUtil.ItemCallback<SongInfo>() {
        // Item identity
        override fun areItemsTheSame(oldItem: SongInfo, newItem: SongInfo): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
        // Item contents are the same, but the object might have changed
        override fun areContentsTheSame(oldItem: SongInfo, newItem: SongInfo): Boolean {
            return oldItem.name == newItem.name
                    && oldItem.rawId == newItem.rawId
                    && oldItem.time == oldItem.time
        }
    }
}

