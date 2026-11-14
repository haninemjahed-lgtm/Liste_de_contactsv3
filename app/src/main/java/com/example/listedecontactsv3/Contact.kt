package com.example.listedecontactsv3

data class Contact(
    val id: Int,
    val nom: String,
    val prenom: String,
    val telephone: String,
    val email: String,
    val photoUri: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null
)

fun donneesDemo() = listOf(
    Contact(1, "Ben Ali", "Ahmed", "22123456", "ahmed@mail.com", null, 36.4000, 10.6167),
    Contact(2, "Trabelsi", "Sarra", "98765432", "sarra@mail.com", null, 36.8065, 10.1815),
    Contact(3, "Gharbi", "Nour", "55123456", "nour@mail.com", null, 34.7406, 10.7603),
    Contact(4, "Mansour", "Sami", "50111222", "sami@mail.com"), // sans position
    Contact(5, "Jaziri", "Amel", "29876543", "amel@mail.com", null, 35.8256, 10.6084)
)