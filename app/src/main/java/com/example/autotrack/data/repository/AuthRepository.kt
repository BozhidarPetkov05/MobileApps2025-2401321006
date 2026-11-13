package com.example.autotrack.data.repository

import com.google.firebase.auth.FirebaseAuth

class AuthRepository(private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()) {
    val currentUser get() = firebaseAuth.currentUser

    fun logout() {
        firebaseAuth.signOut()
    }
}
