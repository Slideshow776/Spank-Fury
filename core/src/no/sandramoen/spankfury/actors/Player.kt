package no.sandramoen.spankfury.actors

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import no.sandramoen.spankfury.utils.BaseActor
import no.sandramoen.spankfury.utils.BaseGame

class Player(x: Float, y: Float, s: Stage) : BaseActor(x, y, s) {
    private val token = "Player.kt"
    var playerSpeed = 40f
    var playerAcceleration = 70f
    var playerDeceleration = 70f

    init {
        loadImage("whitePixel")
        setSize(BaseGame.WORLD_WIDTH / 12, BaseGame.WORLD_HEIGHT / 3)
        color = Color.GREEN
        println("$token: $width, ${BaseGame.WORLD_WIDTH / 2}, $x")
        centerAtPosition(BaseGame.WORLD_WIDTH / 2, BaseGame.WORLD_HEIGHT / 2)
        println("$token: $width, ${BaseGame.WORLD_WIDTH / 2}, $x")

        setAcceleration(playerAcceleration)
        setMaxSpeed(playerSpeed) // (world units)/seconds
        setDeceleration(playerDeceleration)

        alignCamera(lerp = .1f)
    }

    override fun act(dt: Float) {
        super.act(dt)
        applyPhysics(dt)
        alignCamera()
    }

    fun hit(distance: Float) {
        addAction(Actions.moveBy(distance * .6f, 0f, .25f))
    }
}
