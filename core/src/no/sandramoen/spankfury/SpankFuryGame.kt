package no.sandramoen.spankfury;

import no.sandramoen.spankfury.screens.gameplay.LevelScreen
import no.sandramoen.spankfury.screens.shell.SplashScreen
import no.sandramoen.spankfury.utils.BaseGame

class SpankFuryGame : BaseGame() {
    override fun create() {
        super.create()
        // setActiveScreen(SplashScreen()) // TODO: @release: uncomment this
        setActiveScreen(LevelScreen()) // TODO: @release: comment this
    }
}
