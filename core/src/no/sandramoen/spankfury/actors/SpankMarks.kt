package no.sandramoen.spankfury.actors

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.utils.Align
import no.sandramoen.spankfury.utils.BaseActor
import no.sandramoen.spankfury.utils.BaseGame

class SpankMarks(x: Float, y: Float, s: Stage) : BaseActor(x, y, s) {
    private val token = "SpankMarks.kt"

    init {
        // graphics
        loadImage("whitePixel")
        color = Color.FIREBRICK
        color.a = .75f

        // properties
        setSize(
            (BaseGame.WORLD_WIDTH / 100) * BaseGame.scale,
            (BaseGame.WORLD_HEIGHT / 100) * (Gdx.graphics.width.toFloat() / Gdx.graphics.height) * BaseGame.scale
        )
        setPosition(x - width / 2, y)
        // rotateBy(MathUtils.random(0f, 45f))
        setOrigin(Align.center)

        // actions
        var totalDuration = 1f
        addAction(Actions.sequence(
            Actions.parallel(
                /*Actions.sequence( // pulsing effect
                    Actions.alpha(.75f, .2f),
                    Actions.alpha(.5f, .2f),
                    Actions.alpha(.75f, .2f),
                    Actions.alpha(.5f, .2f)
                ),*/
                Actions.alpha(1f, totalDuration / 2),
                Actions.fadeOut(totalDuration / 2)
            ),
            Actions.run { remove() }
        ))
    }
}
