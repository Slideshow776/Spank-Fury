package no.sandramoen.spankfury.actors

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Stage
import no.sandramoen.spankfury.utils.BaseActor
import no.sandramoen.spankfury.utils.BaseGame

class Player(x: Float, y: Float, s: Stage) : BaseActor(x, y, s) {
    private val token = "Player.kt"
    var playerSpeed = 40f
    var playerAcceleration = 70f
    var playerDeceleration = 70f

    var strikeLeft = false
    var strikeTimer = 0f
    var strikeLength = .4f

    init {
        loadImage("whitePixel")
        setSize(BaseGame.WORLD_WIDTH / 12, BaseGame.WORLD_HEIGHT / 3)
        color = Color.GREEN
        centerAtPosition(BaseGame.WORLD_WIDTH / 2, BaseGame.WORLD_HEIGHT / 2)
        setBoundaryRectangle()

        setAcceleration(playerAcceleration)
        setMaxSpeed(playerSpeed) // (world units)/seconds
        setDeceleration(playerDeceleration)

        alignCamera()
    }

    override fun act(dt: Float) {
        super.act(dt)

        if (strikeTimer < strikeLength) {
            strikeTimer += dt
            if (strikeLeft)
                accelerateAtAngle(180f)
            else
                accelerateAtAngle(0f)
        }

        applyPhysics(dt)
        alignCamera()
    }

    fun strike(strikeLeft: Boolean) {
        this.strikeTimer = 0f
        this.strikeLeft = strikeLeft
    }
}
