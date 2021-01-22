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

class Candle(x: Float, y: Float, s: Stage) : BaseActor(x, y, s) {
    private val token = "Candle.kt"

    init {
        // animation
        var animationImages: Array<TextureAtlas.AtlasRegion> = Array()
        for (i in 1..5) animationImages.add(BaseGame.textureAtlas!!.findRegion("candle$i"))
        setAnimation(Animation(.5f, animationImages, Animation.PlayMode.LOOP_PINGPONG))

        // properties
        setSize(
            (BaseGame.WORLD_WIDTH / 90) * BaseGame.scale,
            (BaseGame.WORLD_HEIGHT / 75) * (Gdx.graphics.width.toFloat() / Gdx.graphics.height) * BaseGame.scale
        )
    }
}
