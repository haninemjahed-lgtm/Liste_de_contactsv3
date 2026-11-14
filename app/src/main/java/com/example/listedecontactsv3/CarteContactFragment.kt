package com.example.listedecontactsv3
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class CarteContactFragment : Fragment(R.layout.fragment_carte_contact) {

    companion object {
        private const val ARG_ID = "contact_id"
        fun newInstance(id: Int) = CarteContactFragment().apply {
            arguments = bundleOf(ARG_ID to id)
        }
    }

    private val vm: ContactViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<Button>(R.id.btnRetour).setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        val c = vm.getById(requireArguments().getInt(ARG_ID))
        val lat = c?.latitude
        val lng = c?.longitude

        if (c == null || lat == null || lng == null ||
            lat !in -90.0..90.0 || lng !in -180.0..180.0) {
            view.findViewById<TextView>(R.id.tvMessage).visibility = View.VISIBLE
            view.findViewById<View>(R.id.map).visibility = View.GONE
            return
        }

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync { map ->
            val pos = LatLng(lat, lng)
            map.addMarker(MarkerOptions().position(pos).title("${c.prenom} ${c.nom}"))
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 14f))
        }
    }
}