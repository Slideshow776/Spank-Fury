package no.sandramoen.spankfury.tables

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener
import no.sandramoen.spankfury.utils.BaseGame
import no.sandramoen.spankfury.utils.GameUtils
import no.sandramoen.spankfury.utils.ScreenTransition

class LevelPauseTable : Table() {
    var menuTextButton: TextButton
    var continueTextButton: TextButton
    var exitTextButton: TextButton

    private var screenTransition: ScreenTransition

    init {
        color.a = 0f

        screenTransition = ScreenTransition()
        screenTransition.fadeOut(0f)

        // buttons
        menuTextButton = initializeTextButton("Menu", Color.YELLOW)
        continueTextButton = initializeTextButton("Continue", Color.GREEN)
        continueTextButton.width = Gdx.graphics.width * .25f
        continueTextButton.height = Gdx.graphics.height * .125f

        exitTextButton = initializeTextButton("Quit", Color.RED)
        add(continueTextButton).width(Gdx.graphics.width * .25f).height(Gdx.graphics.height * .125f).padBottom(Gdx.graphics.height * .05f).padTop(Gdx.graphics.height * .25f).row()
        add(menuTextButton).width(Gdx.graphics.width * .25f).height(Gdx.graphics.height * .125f).padBottom(Gdx.graphics.height * .05f).row()
        add(exitTextButton).width(Gdx.graphics.width * .25f).height(Gdx.graphics.height * .125f).row()
        continueTextButton.debug = true
        debug = true
    }

    fun fadeInAndEnable(overlayDuration: Float) {
        addAction(Actions.fadeIn(overlayDuration))

        // enable menu buttons
        GameUtils.enableActorsWithDelay(menuTextButton)
        GameUtils.enableActorsWithDelay(continueTextButton)
        GameUtils.enableActorsWithDelay(exitTextButton)
    }

    fun fadeOutAndDisable(overlayDuration: Float) {
        addAction(Actions.fadeOut(overlayDuration))

        // disable menu buttons
        menuTextButton.touchable = Touchable.disabled
        continueTextButton.touchable = Touchable.disabled
        exitTextButton.touchable = Touchable.disabled
    }

    private fun initializeTextButton(text: String, color: Color): TextButton {
        val button = TextButton(text, BaseGame.textButtonStyle)
        button.color = color
        button.touchable = Touchable.disabled
        return button
    }
}