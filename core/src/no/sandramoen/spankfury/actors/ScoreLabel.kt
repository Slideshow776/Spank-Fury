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
    var flagRemove = false

    init {
        // other
        color.a = 0f

        // actors
        val score = Label("$label", BaseGame.labelStyle)
        score.setFontScale(.3f)
        score.setOrigin(Align.center)

        val table = Table()
        table.add(score)
        table.setFillParent(true)
        addActor(table)

        // actions
        addAction(Actions.parallel(
                Actions.moveBy(0f, 10f, .6f, Interpolation.exp10Out), // move upwards
                Actions.sequence( // fade in, and out again
                        Actions.fadeIn(.3f),
                        Actions.fadeOut(.3f)
                ),
                Actions.sequence( // delayed remove()
                        Actions.delay(2f),
                        Actions.run { flagRemove = true }
                )
        ))
    }

    override fun act(dt: Float) {
        super.act(dt)
        if (flagRemove)
            remove()
    }
}
