package de.bashburg.springbook.edgeservice.user

data class User(val username: String, val firstName: String, val lastName: String, val roles: List<String>)
