package no.sandramoen.spankfury.actors

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction
import com.badlogic.gdx.utils.Align
import no.sandramoen.spankfury.utils.BaseActor
import no.sandramoen.spankfury.utils.BaseGame
import com.badlogic.gdx.utils.Array

class BrokenHeart(x: Float, y: Float, s: Stage) : BaseActor(x, y, s) {
    private val token = "BrokenHeart.kt"

    init {
        // animation
        var animationImages: Array<TextureAtlas.AtlasRegion> = Array()
        for (i in 1..10) animationImages.add(BaseGame.textureAtlas!!.findRegion("brokenHeart$i"))
        setAnimation(Animation(.03f, animationImages, Animation.PlayMode.NORMAL))

        // properties
        setSize(
            (BaseGame.WORLD_WIDTH / 20) * BaseGame.scale,
            (BaseGame.WORLD_HEIGHT / 20) * (Gdx.graphics.width.toFloat() / Gdx.graphics.height) * BaseGame.scale
        )
        println("$token: ${(Gdx.graphics.width.toFloat() / Gdx.graphics.height)}, ${Gdx.graphics.width}, ${Gdx.graphics.height}")
        setPosition(x - width / 2, y)
        setOrigin(Align.center)

        // actions
        addAction(
            Actions.forever(
                Actions.sequence( // wiggle rotation
                    Actions.rotateBy(2f, .125f),
                    Actions.rotateBy(-4f, .25f),
                    Actions.rotateBy(2f, .125f)
                )
            )
        )
        addAction(fadeUpAndDisappear())
    }

    private fun fadeUpAndDisappear(): SequenceAction {
        var totalDuration = 2f
        return Actions.sequence(
            Actions.parallel(
                Actions.moveBy(0f, 10f, totalDuration),
                Actions.fadeOut(totalDuration)
            ),
            Actions.run { remove() }
        )
    }
}
