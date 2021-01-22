package no.sandramoen.spankfury.actors

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction
import com.badlogic.gdx.utils.Align
import no.sandramoen.spankfury.utils.BaseActor
import no.sandramoen.spankfury.utils.BaseGame
import com.badlogic.gdx.utils.Array

/*
* This could also be made with Particles...
* */
class Note(x: Float, y: Float, s: Stage, opacity: Float) : BaseActor(x, y, s) {
    private val token = "Note.kt"

    init {
        zIndex = 50
        color.a = opacity

        // video
        loadImage("note${MathUtils.random(1, 3)}")

        // properties
        setSize(
            (BaseGame.WORLD_WIDTH / 40) * BaseGame.scale,
            (BaseGame.WORLD_HEIGHT / 52) * (Gdx.graphics.width.toFloat() / Gdx.graphics.height) * BaseGame.scale
        )

        addAction(Actions.parallel(
            Actions.moveBy(MathUtils.random(-2f, -10f), MathUtils.random(-2f, -10f), 2f),
            Actions.fadeOut(2f)
        ))
    }
}
