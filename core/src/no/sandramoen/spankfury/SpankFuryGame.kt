package no.sandramoen.spankfury

import no.sandramoen.spankfury.screens.gameplay.LevelScreen
import no.sandramoen.spankfury.screens.shell.MenuScreen
import no.sandramoen.spankfury.screens.shell.SplashScreen
import no.sandramoen.spankfury.utils.BaseGame

class SpankFuryGame : BaseGame() {
    override fun create() {
        super.create()
        // setActiveScreen(SplashScreen()) // TODO: @release: change to this
        setActiveScreen(LevelScreen())
        // setActiveScreen(MenuScreen())
    }
}
