package io.github.sgosiaco.playlistgenerator

import android.net.Uri
import java.util.*

fun Calendar.formatString() = "${this.get(Calendar.MONTH).toString().padStart(2, '0')}/${this.get(Calendar.DAY_OF_MONTH).toString().padStart(2, '0')}/${this.get(Calendar.YEAR)}"

fun generateArtUri(id: String) : Uri {
    val artUri = Uri.parse("content://media/external/audio/albumart")
    return Uri.withAppendedPath(artUri, id)
}