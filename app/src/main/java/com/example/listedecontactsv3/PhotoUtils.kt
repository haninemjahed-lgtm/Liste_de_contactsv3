package com.example.listedecontactsv3

import android.net.Uri
import android.widget.ImageView

fun ImageView.chargerPhoto(uri: String?) {
    if (uri == null) {
        setImageResource(android.R.drawable.ic_menu_gallery)
        return
    }
    try {
        setImageURI(Uri.parse(uri))
    } catch (e: Exception) {
        setImageResource(android.R.drawable.ic_menu_gallery)
    }
}