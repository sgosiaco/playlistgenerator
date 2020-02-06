package io.github.sgosiaco.playlistgenerator

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class Song(val title: String, val artist: String, val date: String, val path: String)

class SongViewHolder(inflater: LayoutInflater, parent: ViewGroup) : RecyclerView.ViewHolder(inflater.inflate(
    R.layout.song_item, parent, false)) {
    private var titleView: TextView? = null
    private var artistView: TextView? = null
    private var dateView: TextView? = null

    init {
        titleView = itemView.findViewById(R.id.songTitle)
        artistView = itemView.findViewById(R.id.songArtist)
        dateView = itemView.findViewById(R.id.songYear)
    }

    fun bind(song: Song, clickListener: OnItemClickListener) {
        titleView?.text = song.title
        artistView?.text = song.artist
        dateView?.text = song.date
        itemView.setOnClickListener {
            clickListener.onItemClicked(song)
        }
    }
}

class SongAdapter(private val list: List<Song>, private val itemClickListener: OnItemClickListener) : RecyclerView.Adapter<SongViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return SongViewHolder(
            inflater,
            parent
        )
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val song: Song = list[position]
        holder.bind(song, itemClickListener)
    }


    override fun getItemCount(): Int = list.size
}

interface OnItemClickListener{
    fun onItemClicked(song: Song)
}