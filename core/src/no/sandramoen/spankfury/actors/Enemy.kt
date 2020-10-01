package no.sandramoen.spankfury.actors

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import no.sandramoen.spankfury.utils.BaseActor
import kotlin.math.abs

open class Enemy(x: Float, y: Float, s: Stage, private val player: Player) : BaseActor(x, y, s) {
    private val token = "Enemy.kt"
    private val stunFrequency = 2f
    var alive = true

    open var health = 1
    var startPosition = player.x - 55 // ensures enemy spawns offscreen relative to players position
    var spawnFromLeft = MathUtils.randomBoolean()
    var stunTimer = 2f
    var enemySpeed = MathUtils.random(8f, 12f)
    var enemyAcceleration = MathUtils.random(5f, 8f)
    var enemyDeceleration = MathUtils.random(20f, 50f)

    init {
        if (!spawnFromLeft)
            startPosition = player.x + 55

        setAcceleration(enemyAcceleration)
        setMaxSpeed(enemySpeed) // (world units)/seconds
        setDeceleration(enemyDeceleration)
    }

    override fun act(dt: Float) {
        super.act(dt)

        // movement
        val distance = abs(x - player.x)
        /*if (stunTimer < stunFrequency) {
            stunTimer += dt
        } else */
        if (width * .8f + player.width * .8f <= distance){
            if (spawnFromLeft)
                accelerateAtAngle(0f)
            else
                accelerateAtAngle(180f)
        }
        applyPhysics(dt)
    }

    fun hit(): Boolean {
        println("$token: hit()")
        health--
        /*stunTimer = 0f
        if (x <= player.x) { // if player is on the left
            // jump a bit back to the right
            addAction(Actions.moveBy(-1f, 0f, .1f))
        } else
        // jump a bit back to the left
            addAction(Actions.moveBy(1f, 0f, .1f))*/
        if (health <= 0 && actions.size == 0) {
            alive = false
            addAction(Actions.sequence(
                    Actions.fadeOut(.25f),
                    Actions.run { remove() }
            ))
            return true
        }
        return false
    }
}
