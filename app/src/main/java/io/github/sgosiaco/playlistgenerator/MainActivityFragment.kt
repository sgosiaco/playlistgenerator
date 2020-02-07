package io.github.sgosiaco.playlistgenerator

import android.content.ContentUris
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_main.*
import java.io.File
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

class MainActivityFragment : Fragment(), OnItemClickListener {

    private val songs = mutableListOf<Song>()
    private val dateFormat = DateTimeFormatter.ofPattern("MM/dd/yyyy")
    private val cal = Calendar.getInstance()
    private var targetDate : Long = 0
    private val audioList = mutableListOf<Audio>()

    private val projection = arrayOf(
        MediaStore.Audio.Media._ID,
        MediaStore.Audio.Media.TITLE,
        MediaStore.Audio.Media.ARTIST,
        MediaStore.Audio.Media.DATE_MODIFIED,
        MediaStore.Audio.Media.DATA
    )

    private val selection = "${MediaStore.Audio.Media.DATE_MODIFIED} >= ?"
    private val sortOrder = "${MediaStore.Audio.Media.DATE_MODIFIED} DESC"

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

        val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
        var datePref = sharedPref.getString("date", cal.formatString()) ?: cal.formatString()
        datePref = "${(datePref.split("/")[0].toInt()+1).toString().padStart(2, '0')}/${datePref.split("/")[1]}/${datePref.split("/")[2]}"
        targetDate = LocalDate.parse(datePref, dateFormat).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()/1000

        swipeList.apply {
            setOnRefreshListener {
                updateList()
                swipeList.isRefreshing = false
            }
            ContextCompat.getColor(context, android.R.color.holo_blue_light)
            setColorSchemeColors(
                ContextCompat.getColor(context, android.R.color.holo_blue_light),
                ContextCompat.getColor(context, android.R.color.holo_green_light),
                ContextCompat.getColor(context, android.R.color.holo_orange_light),
                ContextCompat.getColor(context, android.R.color.holo_red_light)
            )
        }

        rv_song_list.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity)
            adapter = SongAdapter(songs, this@MainActivityFragment)
            addItemDecoration(DividerItemDecoration(this.context, LinearLayoutManager.VERTICAL))
        }

        updateList()
    }

    private fun updateList() {
        audioList.clear()
        songs.clear()
        val selectionArgs = arrayOf(""+targetDate)

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
                val date = dateFormat.withLocale(Locale.getDefault()).withZone(ZoneId.systemDefault()).format(Instant.ofEpochSecond(cursor.getLong(dateCol)))
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
        rv_song_list.adapter!!.notifyDataSetChanged()
    }

    fun export() {
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
        val dirPref = sharedPref.getString("directory", "/PlaylistGenerator") ?: "/PlaylistGenerator"
        val filename = sharedPref.getString("filename", "list.m3u8") ?: "list.m3u8"
        val UAPP = sharedPref.getBoolean("uapp", false)
        var dir = File("/storage/emulated/0$dirPref")
        var file = File(dir, filename)

        var playlist = ""
        for(song in songs) {
            playlist += song.path+"\n"
        }

        dir.mkdir()
        file.writeText(playlist)

        if(UAPP) {
            with(File("/storage/emulated/0/UAPP/PlayListsV3", "${filename.split(".")[0]}.xml")) { if(exists()) { delete() } }
            dir = File("/storage/emulated/0/UAPP/PlayLists")
            file = File(dir, filename)
            file.writeText(playlist)
        }
    }

    fun setDate(time: Long) {
        targetDate = time/1000
        updateList()
    }
}

data class Audio(val uri: Uri,
                 val title: String,
                 val artist: String,
                 val date: String,
                 val data: String)


