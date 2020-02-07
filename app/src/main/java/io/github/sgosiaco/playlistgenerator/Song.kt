package io.github.sgosiaco.playlistgenerator

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

data class Song(val title: String, val artist: String, val duration: String, val date: String, val path: String, val art: Uri)

class SongAdapter(private val list: List<Song>, private val itemClickListener: OnItemClickListener) : RecyclerView.Adapter<SongAdapter.SongViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return SongViewHolder(
            inflater,
            parent
        )
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) = holder.bind(list[position], itemClickListener)


    override fun getItemCount(): Int = list.size


    class SongViewHolder(inflater: LayoutInflater, parent: ViewGroup) : RecyclerView.ViewHolder(inflater.inflate(
        R.layout.song_item, parent, false)) {
        private var titleView: TextView? = null
        private var artistView: TextView? = null
        private var dateView: TextView? = null
        private var albumView: ImageView? = null

        init {
            titleView = itemView.findViewById(R.id.songTitle)
            artistView = itemView.findViewById(R.id.songArtist)
            dateView = itemView.findViewById(R.id.songDate)
            albumView = itemView.findViewById(R.id.albumArt)
        }

        fun bind(song: Song, clickListener: OnItemClickListener) {
            titleView?.text = song.title
            artistView?.text = String.format("%s (%s)", song.artist, song.duration)
            dateView?.text = song.date
            albumView?.let {
                Glide
                    .with(itemView)
                    .asBitmap()
                    .load(song.art)
                    .into(it)
            }
            itemView.setOnClickListener {
                clickListener.onItemClicked(song)
            }
        }
    }
}

interface OnItemClickListener{
    fun onItemClicked(song: Song)
}