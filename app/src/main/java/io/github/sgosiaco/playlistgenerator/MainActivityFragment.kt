package io.github.sgosiaco.playlistgenerator

import android.content.ContentUris
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_main.*
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

class MainActivityFragment : Fragment(), OnItemClickListener {

    private val songs = mutableListOf<Song>()


    override fun onItemClicked(song: Song) {
        Toast.makeText(activity, "Song: ${song.title} ${song.path}", Toast.LENGTH_LONG).show()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val audioList = mutableListOf<Audio>()

        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DATE_MODIFIED,
            MediaStore.Audio.Media.DATA
        )

        val format = DateTimeFormatter.ofPattern("MM dd yyyy")
        val date = LocalDate.parse("01 01 2019", format).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()/1000
        val selection = "${MediaStore.Audio.Media.DATE_MODIFIED} >= ?"
        val selectionArgs = arrayOf(""+date)

        val sortOrder = "${MediaStore.Audio.Media.DATE_MODIFIED} DESC"

        val query = activity?.contentResolver?.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )

        query?.use {cursor ->
            val idCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val titleCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val artistCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val dateCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_MODIFIED)
            val dataCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)

            while(cursor.moveToNext()) {
                val id = cursor.getLong(idCol)
                val title = cursor.getString(titleCol)
                val artist = cursor.getString(artistCol)
                val date = DateTimeFormatter.ofPattern("MM/dd/yyyy").withLocale(Locale.getDefault()).withZone(ZoneId.systemDefault()).format(Instant.ofEpochSecond(cursor.getLong(dateCol)))
                val contentUri: Uri = ContentUris.withAppendedId(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    id
                )
                val data = cursor.getString(dataCol)
                audioList.add(Audio(contentUri, title, artist, date, data))
            }
        }

        for(audio in audioList) {
            songs.add(Song(audio.title, audio.artist, audio.date, audio.data))
        }

        rv_song_list.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = SongAdapter(songs, this@MainActivityFragment)
            addItemDecoration(DividerItemDecoration(this.context, LinearLayoutManager.VERTICAL))
        }
    }
    companion object {
        fun newInstance(): MainActivityFragment = MainActivityFragment()
    }
}

data class Audio(val uri: Uri,
                 val title: String,
                 val artist: String,
                 val date: String,
                 val data: String)


