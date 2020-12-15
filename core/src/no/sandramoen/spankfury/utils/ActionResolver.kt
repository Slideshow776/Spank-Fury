package no.sandramoen.spankfury.utils

interface ActionResolver {
    val signedInGPGS: Boolean
    fun loginGPGS()
    fun submitScoreGPGS(score: Int, id: String?)
    fun unlockAchievementGPGS(achievementId: String?)
    val leaderboardGPGS: Unit
    val achievementsGPGS: Unit
}
