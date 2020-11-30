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

open class Enemy(x: Float, y: Float, s: Stage, open val player: Player) : BaseActor(x, y, s) {
    private val token = "Enemy.kt"
    private val stunFrequency = 1f
    private var distance = 0f
    private var originalSpeed = MathUtils.random(20f, 25f)
    private var originalAcceleration = 100f // MathUtils.random(23f, 29f)
    private var originalDeceleration = 100f // MathUtils.random(48f, 52f)
    private var tempo = BaseGame.tempo
    private var dead = false

    open var health = 1
    open var originalWidth = 0f
    open var originalHeight = 0f
    open var points = -1
    open var originalColor: Color = Color.WHITE

    var startPosition = player.x - 60 // ensures enemy spawns offscreen relative to players position
    var spawnFromLeft = MathUtils.randomBoolean()
    var stunTimer = stunFrequency
    var hitting = false
    var hittingDelay = .75f
    var stunned = false
    var backOffDistanceModifier = 1f

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
            startPosition = player.x + BaseGame.WORLD_WIDTH + 10 // offset ensures spawning offscreen
            flip()
        }

        setPosition(startPosition, MathUtils.random(player.y - 10f, player.y + 10f))
    }

    override fun act(dt: Float) {
        super.act(dt * BaseGame.tempo)
        if (pause || dead) return
        if (BaseGame.tempo != tempo) setNewTempo()
        checkStunned(dt)

        // x movement + behaviour
        distance = abs(x - player.x)
        if (!stunned && !hitting && ((width + player.width) * (.85 * backOffDistanceModifier) <= distance)) { // if too far away, move closer
            if (x <= player.x)
                accelerateAtAngle(0f)
            else
                accelerateAtAngle(180f)
        } else if ((width + player.width) * (.7 * backOffDistanceModifier) > distance) { // if too close, move back
            if (spawnFromLeft)
                accelerateAtAngle(180f)
            else
                accelerateAtAngle(0f)
        } else if (!hitting && !stunned && ((width + player.width) * .85 >= distance)) {
            hit()
        }

        // y movement
        if (y < player.y - 1) y += .05f
        else if (y >= player.y + 1) y -= .05f

        applyPhysics(dt)

        if (player.health <= 0) { // sets animation to idle after game ends
            actions.clear()
            changeAnimation(idleAnimation)
            dead = true
        }
    }

    fun handleBackOff(backOff: Boolean) {
        var playerHasBackToEnemy = (spawnFromLeft && player.isFacingRight) || (!spawnFromLeft && !player.isFacingRight)
        if (playerHasBackToEnemy && backOff) {
            backOffDistanceModifier = 2f
            actions.clear()
            hitting = false
            addAction(Actions.sequence(
                    Actions.delay(BaseGame.backOffFrequency),
                    Actions.run { changeAnimation(walkingAnimation) }
            ))
        }
        else backOffDistanceModifier = 1f
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
        // TODO: mark enemy as non-interactable/dead?
        addAction(Actions.sequence(
                Actions.fadeOut(1f),
                Actions.run { remove() }
        ))
        return true
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

    private fun handleStun() {
        stunTimer = 0f
        stunned = true
        hitting = false
        actions.clear()
        changeAnimation(stunnedAnimation)
    }

    private fun hit() {
        if (player.health > 0) {
            hitting = true
            changeAnimation(idleAnimation)
            addAction(Actions.sequence(
                    Actions.delay(hittingDelay),
                    Actions.run {
                        if ((width + player.width) * .85 > distance) {
                            if (player.health > 0) // if player is still alive
                                player.struck(spawnFromLeft)
                            changeAnimation(hittingAnimation)
                        }
                    },
                    Actions.delay(hittingAnimation.keyFrames.size * .1f), // WEAK: dependant on num frames in animation...
                    Actions.run {
                        if (player.health > 0) {
                            hitting = false
                            changeAnimation(walkingAnimation)
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

    private fun changeAnimation(animation: Animation<TextureAtlas.AtlasRegion>) {
        setAnimation(animation)
        setSize(originalWidth, originalHeight)
    }
}
