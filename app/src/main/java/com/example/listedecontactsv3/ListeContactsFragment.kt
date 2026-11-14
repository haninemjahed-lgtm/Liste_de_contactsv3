package com.example.listedecontactsv3

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

class ListeContactsFragment : Fragment(R.layout.fragment_liste_contacts) {

    private val vm: ContactViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val rv = view.findViewById<RecyclerView>(R.id.rvContacts)
        val tvVide = view.findViewById<TextView>(R.id.tvVide)
        val et = view.findViewById<EditText>(R.id.etRecherche)

        val adapter = ContactAdapter { contact ->
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, DetailContactFragment.newInstance(contact.id))
                .addToBackStack(null)
                .commit()
        }
        rv.layoutManager = LinearLayoutManager(requireContext())
        rv.adapter = adapter

        et.setText(vm.recherche.value)   // garde le filtre au retour / rotation
        et.doAfterTextChanged { vm.rechercher(it?.toString().orEmpty()) }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                vm.contactsFiltres.collect { liste ->
                    adapter.submitList(liste)
                    tvVide.visibility = if (liste.isEmpty()) View.VISIBLE else View.GONE
                }
            }
        }
    }
}