package com.example.listedecontactsv3

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class ContactViewModel : ViewModel() {

    private val _contacts = MutableStateFlow(donneesDemo())
    private val _recherche = MutableStateFlow("")
    val recherche: StateFlow<String> = _recherche.asStateFlow()

    val contactsFiltres: StateFlow<List<Contact>> =
        combine(_contacts, _recherche) { liste, q ->
            val t = q.trim().lowercase()
            if (t.isEmpty()) liste
            else liste.filter {
                it.nom.lowercase().contains(t) ||
                        it.prenom.lowercase().contains(t) ||
                        it.telephone.contains(t)
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), donneesDemo())

    fun rechercher(q: String) { _recherche.value = q }

    fun getById(id: Int): Contact? = _contacts.value.firstOrNull { it.id == id }

    fun update(c: Contact) {
        _contacts.value = _contacts.value.map { if (it.id == c.id) c else it }
    }
}