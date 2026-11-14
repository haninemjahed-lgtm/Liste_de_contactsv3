package com.example.listedecontactsv3

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class ContactAdapter(private val onClick: (Contact) -> Unit) :
    ListAdapter<Contact, ContactAdapter.VH>(Diff) {

    class VH(v: View) : RecyclerView.ViewHolder(v) {
        val photo: ImageView = v.findViewById(R.id.ivPhoto)
        val nom: TextView = v.findViewById(R.id.tvNom)
        val tel: TextView = v.findViewById(R.id.tvTel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(LayoutInflater.from(parent.context).inflate(R.layout.item_contact, parent, false))

    override fun onBindViewHolder(h: VH, position: Int) {
        val c = getItem(position)              // élément de la liste FILTRÉE
        h.nom.text = "${c.nom} ${c.prenom}"
        h.tel.text = c.telephone
        h.photo.chargerPhoto(c.photoUri)
        h.itemView.setOnClickListener { onClick(c) }   // on renvoie le contact, pas l'indice
    }

    object Diff : DiffUtil.ItemCallback<Contact>() {
        override fun areItemsTheSame(a: Contact, b: Contact) = a.id == b.id
        override fun areContentsTheSame(a: Contact, b: Contact) = a == b
    }
}