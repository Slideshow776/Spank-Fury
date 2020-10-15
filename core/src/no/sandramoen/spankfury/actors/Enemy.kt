package no.sandramoen.spankfury.actors

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.utils.Array
import no.sandramoen.spankfury.utils.BaseActor
import no.sandramoen.spankfury.utils.BaseGame
import kotlin.math.abs

open class Enemy(x: Float, y: Float, s: Stage, open val player: Player) : BaseActor(x, y, s) {
    private val token = "Enemy.kt"
    private val stunFrequency = 2f
    private var distance = 0f

    open var health = 1
    open var originalWidth = 0f
    open var originalHeight = 0f
    open var points = -1
    open var originalColor: Color = Color.WHITE

    var startPosition = player.x - 60 // ensures enemy spawns offscreen relative to players position
    var spawnFromLeft = MathUtils.randomBoolean()
    var stunTimer = 2f
    var enemySpeed = MathUtils.random(20f, 25f)
    var enemyAcceleration = 100f // MathUtils.random(23f, 29f)
    var enemyDeceleration = 100f // MathUtils.random(48f, 52f)
    var hitting = false
    var hittingDelay = .75f
    var stunned = false


    // animations
    private var idleAnimation: Animation<TextureAtlas.AtlasRegion>
    private var walkingAnimation: Animation<TextureAtlas.AtlasRegion>
    private var attackingAnimation: Animation<TextureAtlas.AtlasRegion>
    private var deadAnimation: Animation<TextureAtlas.AtlasRegion>

    init {
        // physics
        setAcceleration(enemyAcceleration)
        setMaxSpeed(enemySpeed) // (world units)/seconds
        setDeceleration(enemyDeceleration)

        // animations
        var animationImages: Array<TextureAtlas.AtlasRegion> = Array()
        for (i in 1..4)
            animationImages.add(BaseGame.textureAtlas!!.findRegion("enemy-idle-0$i"))
        idleAnimation = Animation(.25f, animationImages, Animation.PlayMode.LOOP)
        animationImages.clear()

        for (i in 1..4)
            animationImages.add(BaseGame.textureAtlas!!.findRegion("enemy-walk-0$i"))
        walkingAnimation = Animation(.25f, animationImages, Animation.PlayMode.LOOP)
        animationImages.clear()

        for (i in 1..4)
            animationImages.add(BaseGame.textureAtlas!!.findRegion("enemy-attack-0$i"))
        attackingAnimation = Animation(.1f, animationImages, Animation.PlayMode.NORMAL)
        animationImages.clear()

        for (i in 1..7)
            animationImages.add(BaseGame.textureAtlas!!.findRegion("enemy-dead-0$i"))
        deadAnimation = Animation(.1f, animationImages, Animation.PlayMode.NORMAL)
        animationImages.clear()

        setAnimation(walkingAnimation)

        // other
        if (!spawnFromLeft) {
            startPosition = player.x + 60 // half-world offset ensures spawning offscreen
            flip()
        }

        setPosition(startPosition, MathUtils.random(player.y - 20f, player.y + 20f))
    }

    override fun act(dt: Float) {
        super.act(dt)
        if (pause) return

        if (stunTimer < stunFrequency) {
            stunTimer += dt
        } else {
            stunned = false
        }

        // x movement + behaviour
        distance = abs(x - player.x)
        if (!stunned && !hitting && ((width + player.width) * .85 <= distance)) { // if too far away, move closer
            if (x <= player.x)
                accelerateAtAngle(0f)
            else
                accelerateAtAngle(180f)
        } else if ((width + player.width) * .7 > distance) { // if too close, move back
            if (spawnFromLeft)
                accelerateAtAngle(180f)
            else
                accelerateAtAngle(0f)
        } else if (!hitting && !stunned) {
            hit()
        }

        // y movement
        if (y < player.y - 1)
            y += .05f
        else if (y >= player.y + 1)
            y -= .05f

        applyPhysics(dt)
    }

    open fun struck(enableSound: Boolean = true): Boolean { // returns true if enemy died
        if (enableSound) BaseGame.hitSound1!!.play(BaseGame.soundVolume)
        health--
        if (health <= 0)
            return handleDeath()
        handleStun()
        return false
    }

    open fun handleStun() {
        stunTimer = 0f
        stunned = true
        actions.clear()
        hitting = false
        addAction(Actions.sizeTo(originalWidth, originalHeight, .5f))
    }

    open fun handleDeath(): Boolean {
        changeAnimation(deadAnimation)
        actions.clear()
        addAction(Actions.sequence(
                Actions.fadeOut(deadAnimation.frameDuration * deadAnimation.keyFrames.size),
                Actions.run { remove() }
        ))
        return true
    }

    private fun hit() {
        hitting = true
        addAction(Actions.sequence(
                Actions.sizeBy(4f, -4f, 1f, Interpolation.circleIn),
                Actions.delay(hittingDelay),
                Actions.run {
                    if ((width + player.width) * .85 > distance) {
                        player.struck()
                        changeAnimation(attackingAnimation)
                    }
                },
                Actions.sizeTo(originalWidth, originalHeight, .5f, Interpolation.bounceOut),
                Actions.run {
                    hitting = false
                    changeAnimation(walkingAnimation)
                }
        ))
    }

    private fun changeAnimation(animation: Animation<TextureAtlas.AtlasRegion>) {
        setAnimation(animation)
        setSize(originalWidth, originalHeight)
    }
}
