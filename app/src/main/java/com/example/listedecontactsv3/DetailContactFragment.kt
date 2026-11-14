package com.example.listedecontactsv3

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import java.io.File

class DetailContactFragment : Fragment(R.layout.fragment_detail_contact) {

    companion object {
        private const val ARG_ID = "contact_id"
        private const val STATE_PHOTO = "photo"
        fun newInstance(id: Int) = DetailContactFragment().apply {
            arguments = bundleOf(ARG_ID to id)
        }
    }

    private val vm: ContactViewModel by activityViewModels()
    private var contactId = -1
    private var photoUri: String? = null
    private lateinit var ivPhoto: ImageView
    private lateinit var etNom: EditText
    private lateinit var etPrenom: EditText
    private lateinit var etTel: EditText
    private lateinit var etEmail: EditText
    private lateinit var etLat: EditText
    private lateinit var etLng: EditText

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            photoUri = uri.toString()
            ivPhoto.chargerPhoto(photoUri)
        }
        // uri == null : l'utilisateur a annulé, on ne fait rien
    }

    private val takePhoto = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bmp ->
        if (bmp != null) {
            photoUri = sauverBitmap(bmp)
            ivPhoto.chargerPhoto(photoUri)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        contactId = requireArguments().getInt(ARG_ID)

        val contact = vm.getById(contactId)
        if (contact == null) {
            parentFragmentManager.popBackStack()
            return
        }

        ivPhoto = view.findViewById(R.id.ivPhoto)
        etNom = view.findViewById(R.id.etNom)
        etPrenom = view.findViewById(R.id.etPrenom)
        etTel = view.findViewById(R.id.etTel)
        etEmail = view.findViewById(R.id.etEmail)
        etLat = view.findViewById(R.id.etLat)
        etLng = view.findViewById(R.id.etLng)

        if (savedInstanceState == null) {      // premier affichage : remplissage depuis le modèle
            photoUri = contact.photoUri
            etNom.setText(contact.nom)
            etPrenom.setText(contact.prenom)
            etTel.setText(contact.telephone)
            etEmail.setText(contact.email)
            etLat.setText(contact.latitude?.toString().orEmpty())
            etLng.setText(contact.longitude?.toString().orEmpty())
        } else {                                // rotation : les EditText se restaurent seuls
            photoUri = savedInstanceState.getString(STATE_PHOTO)
        }
        ivPhoto.chargerPhoto(photoUri)

        view.findViewById<Button>(R.id.btnGalerie).setOnClickListener {
            try { pickImage.launch("image/*") }
            catch (e: ActivityNotFoundException) { toast("Aucune galerie disponible") }
        }
        view.findViewById<Button>(R.id.btnCamera).setOnClickListener {
            try { takePhoto.launch(null) }
            catch (e: ActivityNotFoundException) { toast("Aucune caméra disponible") }
        }
        view.findViewById<Button>(R.id.btnAppeler).setOnClickListener { appeler() }
        view.findViewById<Button>(R.id.btnEmail).setOnClickListener { envoyerEmail() }
        view.findViewById<Button>(R.id.btnCarte).setOnClickListener { voirCarte() }
        view.findViewById<Button>(R.id.btnEnregistrer).setOnClickListener { enregistrer(contact) }
        view.findViewById<Button>(R.id.btnAnnuler).setOnClickListener {
            parentFragmentManager.popBackStack()   // rien n'est enregistré
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(STATE_PHOTO, photoUri)
    }

    private fun appeler() {
        val tel = etTel.text.toString().trim()
        if (tel.isEmpty()) { toast("Numéro vide"); return }
        lancer(Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", tel, null)))
    }

    private fun envoyerEmail() {
        val mail = etEmail.text.toString().trim()
        if (mail.isEmpty()) { toast("E-mail vide"); return }
        lancer(Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(mail))
        })
    }

    private fun lancer(i: Intent) {
        try { startActivity(i) }
        catch (e: ActivityNotFoundException) {
            toast("Aucune application disponible pour cette action")
        }
    }

    private fun voirCarte() {
        val c = vm.getById(contactId)
        if (c?.latitude == null || c.longitude == null) {
            toast("Coordonnées absentes : renseignez-les et enregistrez d'abord")
            return
        }
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, CarteContactFragment.newInstance(contactId))
            .addToBackStack(null)
            .commit()
    }

    private fun enregistrer(ancien: Contact) {
        val nom = etNom.text.toString().trim()
        val prenom = etPrenom.text.toString().trim()
        val tel = etTel.text.toString().trim()
        val mail = etEmail.text.toString().trim()
        val latTxt = etLat.text.toString().trim().replace(',', '.')
        val lngTxt = etLng.text.toString().trim().replace(',', '.')
        val lat = latTxt.toDoubleOrNull()
        val lng = lngTxt.toDoubleOrNull()

        var ok = true
        if (nom.isEmpty()) { etNom.error = "Nom obligatoire"; ok = false }
        if (tel.isEmpty()) { etTel.error = "Téléphone obligatoire"; ok = false }
        if (mail.isNotEmpty() && !Patterns.EMAIL_ADDRESS.matcher(mail).matches()) {
            etEmail.error = "E-mail invalide"; ok = false
        }
        if (latTxt.isNotEmpty() && (lat == null || lat !in -90.0..90.0)) {
            etLat.error = "Latitude invalide (-90 à 90)"; ok = false
        }
        if (lngTxt.isNotEmpty() && (lng == null || lng !in -180.0..180.0)) {
            etLng.error = "Longitude invalide (-180 à 180)"; ok = false
        }
        if (latTxt.isEmpty() != lngTxt.isEmpty()) {
            etLng.error = "Renseignez les deux coordonnées ou aucune"; ok = false
        }
        if (!ok) return

        vm.update(
            ancien.copy(
                nom = nom, prenom = prenom, telephone = tel, email = mail,
                photoUri = photoUri,
                latitude = if (latTxt.isEmpty()) null else lat,
                longitude = if (lngTxt.isEmpty()) null else lng
            )
        )
        toast("Contact enregistré")
        parentFragmentManager.popBackStack()
    }

    private fun sauverBitmap(bmp: Bitmap): String {
        val f = File(requireContext().filesDir, "photo_${contactId}_${System.currentTimeMillis()}.jpg")
        f.outputStream().use { bmp.compress(Bitmap.CompressFormat.JPEG, 90, it) }
        return Uri.fromFile(f).toString()
    }

    private fun toast(msg: String) =
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
}