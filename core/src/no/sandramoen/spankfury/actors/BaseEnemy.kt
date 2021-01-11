package no.sandramoen.spankfury.actors

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.utils.Array
import no.sandramoen.spankfury.utils.BaseActor
import no.sandramoen.spankfury.utils.BaseGame
import kotlin.math.abs

open class BaseEnemy(
    x: Float,
    y: Float,
    s: Stage,
    open val player: Player,
    originalSpeed: Float = 20f,
    hittingDelay: Float = 1f
) : BaseActor(x, y, s) {
    private val token = "Enemy.kt"
    private val stunFrequency = 1f
    private var distance = 0f
    private var originalSpeed = originalSpeed
    private var originalAcceleration = 100f
    private var originalDeceleration = 100f
    private var tempo = BaseGame.tempo
    private var dead = false

    open var health = 1
    open var originalWidth = 0f
    open var originalHeight = 0f
    open var points = -1
    open var originalColor: Color = Color.WHITE

    var xStartPosition = player.x - 60 // ensures enemy spawns offscreen relative to players position
    var spawnFromLeft = MathUtils.randomBoolean()
    var stunTimer = stunFrequency
    var hitting = false
    var hittingDelay = hittingDelay
    var stunned = false
    var backOffDistanceModifier = 1f
    var enabled = true

    // animations
    lateinit var idleAnimation: Animation<TextureAtlas.AtlasRegion>
    lateinit var walkingAnimation: Animation<TextureAtlas.AtlasRegion>
    lateinit var stunnedAnimation: Animation<TextureAtlas.AtlasRegion>
    lateinit var hittingAnimation: Animation<TextureAtlas.AtlasRegion>
    lateinit var deadAnimation: Animation<TextureAtlas.AtlasRegion>

    init {
        // physics
        setAcceleration(originalAcceleration * BaseGame.tempo)
        setMaxSpeed(originalSpeed * BaseGame.tempo) // (world units)/seconds
        setDeceleration(originalDeceleration * BaseGame.tempo)

        // animations
        setAnimation()

        // other
        if (!spawnFromLeft) {
            xStartPosition = player.x + BaseGame.WORLD_WIDTH + 10 // offset ensures spawning offscreen
            flip()
        }

        val yPosition = MathUtils.random(player.y - 10f, player.y + 10f)
        setPosition(xStartPosition, yPosition)

        // z-index is divided into two: below and above player, mostly works
        var index = yPosition.toInt()
        if (index < 3 || yPosition < player.y) index = 4
        else index = 3
        zIndex = index
    }

    override fun act(dt: Float) {
        super.act(dt * BaseGame.tempo)
        if (pause || dead) return
        if (BaseGame.tempo != tempo) setNewTempo()
        checkStunned(dt)

        // x movement + behaviour
        distance = abs(x - player.x)
        if (enabled) {
            if (!stunned && !hitting && ((width + player.width) * (.55 * backOffDistanceModifier) <= distance)) { // if too far away, move closer
                if (x <= player.x)
                    accelerateAtAngle(0f)
                else
                    accelerateAtAngle(180f)
            } else if ((width + player.width) * (.4 * backOffDistanceModifier) > distance) { // if too close, move back
                if (spawnFromLeft)
                    accelerateAtAngle(180f)
                else
                    accelerateAtAngle(0f)
            } else if (!hitting && !stunned && ((width + player.width) * .55 >= distance)) {
                hit()
            }
        }

        // y movement
        if (y < player.y - 3) y += .05f
        else if (y >= player.y + 3) y -= .05f

        applyPhysics(dt)

        if (player.health <= 0) { // sets animation to idle after game ends
            if (!dead) actions.clear()
            changeAnimation(idleAnimation)
            dead = true
        }
    }

    fun handleBackOff(backOff: Boolean) {
        var playerHasBackToEnemy = (spawnFromLeft && player.isFacingRight) || (!spawnFromLeft && !player.isFacingRight)
        if (playerHasBackToEnemy && backOff) {
            backOffDistanceModifier = 4f
            if (!dead) actions.clear()
            hitting = false
            changeAnimation(walkingAnimation)
        } else backOffDistanceModifier = 1f
    }

    fun changeAnimation(animation: Animation<TextureAtlas.AtlasRegion>) {
        setAnimation(animation)
        setSize(originalWidth, originalHeight)
    }

    open fun setAnimation() {
        var animationImages: Array<TextureAtlas.AtlasRegion> = Array()
        for (i in 1..4)
            animationImages.add(BaseGame.textureAtlas!!.findRegion("enemy-idle-0$i"))
        idleAnimation = Animation(.25f, animationImages, Animation.PlayMode.LOOP)
        animationImages.clear()

        for (i in 1..4)
            animationImages.add(BaseGame.textureAtlas!!.findRegion("enemy-walk-0$i"))
        walkingAnimation = Animation(.25f, animationImages, Animation.PlayMode.LOOP)
        animationImages.clear()

        for (i in 1..7)
            animationImages.add(BaseGame.textureAtlas!!.findRegion("enemy-dead-0$i"))
        deadAnimation = Animation(.1f, animationImages, Animation.PlayMode.NORMAL)
        animationImages.clear()

        setAnimation(walkingAnimation)
    }

    open fun struck(enableSound: Boolean = true): Boolean { // returns true if enemy died
        if (enableSound) BaseGame.hitSound1!!.play(BaseGame.soundVolume)
        health--
        if (health <= 0)
            return handleDeath()
        handleStun()
        return false
    }

    open fun handleDeath(): Boolean {
        dead = true
        changeAnimation(deadAnimation)
        actions.clear()
        addAction(Actions.sequence(
            Actions.fadeOut(1f),
            Actions.run { remove() }
        ))
        return true
    }

    fun resetActions() {
        hitting = false
        if (!dead) actions.clear()
        changeAnimation(walkingAnimation)
    }

    private fun checkStunned(dt: Float) {
        if (stunTimer < stunFrequency) stunTimer += dt
        else if (stunTimer > stunFrequency) {
            stunTimer = stunFrequency
            changeAnimation(walkingAnimation)
        } else {
            stunned = false
        }
    }

    private fun hit() {
        if (player.health > 0) {
            hitting = true
            changeAnimation(idleAnimation)
            addAction(Actions.sequence(
                Actions.delay(hittingDelay),
                Actions.run {
                    if ((width + player.width) * .85 > distance) {
                        addAction(Actions.sequence(
                            Actions.run {
                                changeAnimation(hittingAnimation)
                            },
                            Actions.delay(.5f),
                            Actions.run {
                                if (player.health > 0) {
                                    BrokenHeart(x + width / 2, y + height, stage)
                                    player.struck(spawnFromLeft)
                                    hitting = false
                                    enabled = true
                                    changeAnimation(walkingAnimation)
                                }
                            }
                        ))
                    } else {
                        if (player.health > 0) {
                            hitting = false
                            enabled = true
                            changeAnimation(walkingAnimation)
                        }
                    }
                }
            ))
        }
    }

    private fun setNewTempo() {
        tempo = BaseGame.tempo
        setAcceleration(originalAcceleration * BaseGame.tempo)
        setMaxSpeed(originalSpeed * BaseGame.tempo) // (world units)/seconds
        setDeceleration(originalDeceleration * BaseGame.tempo)
    }

    private fun handleStun() {
        stunTimer = 0f
        stunned = true
        hitting = false
        if (!dead) actions.clear()
        changeAnimation(stunnedAnimation)
    }
}
