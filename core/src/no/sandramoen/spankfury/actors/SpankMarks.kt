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
        loadImage("mark")
        color = Color.WHITE
        color.a = .4f

        // properties
        setSize(
            (BaseGame.WORLD_WIDTH / 50) * BaseGame.scale,
            (BaseGame.WORLD_HEIGHT / 50) * (Gdx.graphics.width.toFloat() / Gdx.graphics.height) * BaseGame.scale
        )
        setPosition(x - width / 2, y - height / 2)
        setOrigin(Align.center)

        // actions
        var totalDuration = 1f
        addAction(Actions.sequence(
            Actions.parallel(
                /*Actions.rotateBy(20f, totalDuration / 2),*/
                Actions.fadeOut(totalDuration / 2)
            ),
            Actions.run { remove() }
        ))

        // effects
        val effect = SmackEffect()
        effect.setPosition(width / 4, height / 3) // by trial and error...
        effect.setScale(Gdx.graphics.height * .00005f)
        this.addActor(effect)
        effect.start()
    }
}
