package no.sandramoen.spankfury.utils

interface GooglePlayServices {
    fun signIn()
    fun signOut()
    fun isSignedIn(): Boolean
    fun getLeaderboard()
    fun submitScore(score: Int)
}
