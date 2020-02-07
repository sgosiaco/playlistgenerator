package io.github.sgosiaco.playlistgenerator

import java.util.*

fun Calendar.formatString() = "${this.get(Calendar.MONTH).toString().padStart(2, '0')}/${this.get(Calendar.DAY_OF_MONTH).toString().padStart(2, '0')}/${this.get(Calendar.YEAR)}"