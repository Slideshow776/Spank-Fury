package no.sandramoen.spankfury.actors

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import no.sandramoen.spankfury.utils.BaseActor
import no.sandramoen.spankfury.utils.BaseGame

class ScoreLabel(s: Stage, label: String) : BaseActor(0f, 0f, s) {
    private val token = "ScoreLabel.kt"
    private val lifeTime = 1f // seconds
    private var remove = false

    init {
        color.a = 0f

        // actors
        val score = Label("$label", BaseGame.labelStyle)
        score.setFontScale(.3f)
        score.x -= 15 // centers actor
        addActor(score)

        // actions
        addAction(Actions.parallel(
            Actions.moveBy(0f, 20f, lifeTime, Interpolation.exp10Out), // move upwards
            Actions.sequence( // fade in, and out again
                Actions.fadeIn(lifeTime / 2),
                Actions.fadeOut(lifeTime / 2),
                Actions.run { remove = true }
            )
        ))
    }

    override fun act(dt: Float) {
        super.act(dt)
        if (remove) remove()
    }
}
