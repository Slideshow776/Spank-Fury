package no.sandramoen.spankfury.actors

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.utils.Array
import no.sandramoen.spankfury.utils.BaseActor
import no.sandramoen.spankfury.utils.BaseGame

class Player(x: Float, y: Float, s: Stage) : BaseActor(x, y, s) {
    private val token = "Player.kt"
    private var slowMotionModifier = .25f
    private var tempo = BaseGame.tempo
    private var originalWidth = (BaseGame.WORLD_WIDTH / 12f) * BaseGame.scale
    private var originalHeight = (BaseGame.WORLD_HEIGHT / 2.8f) * BaseGame.scale

    // animations
    private var idleAnimation: Animation<TextureAtlas.AtlasRegion>
    private var whipAnimation: Animation<TextureAtlas.AtlasRegion>
    private var hitAnimations: Array<Animation<TextureAtlas.AtlasRegion>> = Array()

    // properties
    var playerSpeed = 40f
    var playerAcceleration = 70f
    var playerDeceleration = 70f
    var health = 3

    init {
        // animations
        var animationImages: Array<TextureAtlas.AtlasRegion> = Array()
        animationImages.add(BaseGame.textureAtlas!!.findRegion("player-idle-01"))
        animationImages.add(BaseGame.textureAtlas!!.findRegion("player-idle-01"))
        animationImages.add(BaseGame.textureAtlas!!.findRegion("player-idle-02"))
        animationImages.add(BaseGame.textureAtlas!!.findRegion("player-idle-03"))
        idleAnimation = Animation(.25f, animationImages, Animation.PlayMode.LOOP)
        animationImages.clear()

        for (i in 1..9)
            animationImages.add(BaseGame.textureAtlas!!.findRegion("player-hitting-0$i"))
        whipAnimation = Animation(.01f, animationImages, Animation.PlayMode.NORMAL)
        hitAnimations.add(whipAnimation)
        animationImages.clear()

        setAnimation(idleAnimation)

        // other
        setSize(originalWidth, originalHeight)
        setAnimationSize(originalWidth, originalHeight)
        centerAtPosition(BaseGame.WORLD_WIDTH / 2, BaseGame.WORLD_HEIGHT / 3)
        zIndex = 40

        // physics
        setAcceleration(playerAcceleration * BaseGame.tempo)
        setMaxSpeed(playerSpeed * BaseGame.tempo) // (world units)/seconds
        setDeceleration(playerDeceleration * BaseGame.tempo)

        // blinking animation
        val blinkingDuration = .2f
        addAction(
            Actions.sequence(
                Actions.alpha(.5f, blinkingDuration),
                Actions.alpha(1f, blinkingDuration),
                Actions.alpha(.5f, blinkingDuration),
                Actions.alpha(1f, blinkingDuration),
                Actions.alpha(.5f, blinkingDuration),
                Actions.alpha(1f, blinkingDuration),
                Actions.alpha(.5f, blinkingDuration),
                Actions.alpha(1f, blinkingDuration)
            )
        )
    }

    override fun act(dt: Float) {
        super.act(dt * BaseGame.tempo)
        if (pause) return
        if (BaseGame.tempo != tempo) setNewTempo()
        applyPhysics(dt)
        alignCamera(lerp = .1f)

        // keep y position within acceptable bounds
        if (y < 0) addAction(Actions.moveTo(x, 4f, .25f))
        else if (y > 10) addAction(Actions.moveTo(x, 6f, .25f))
    }

    fun hit(distance: Float, miss: Boolean = false, enableSound: Boolean = true) {
        shouldFlip(distance)

        if (miss && enableSound) {
            BaseGame.swooshSound!!.play(BaseGame.soundVolume)
        } else if (enableSound) {
            when (MathUtils.random(1, 3)) {
                1 -> BaseGame.floggerSound!!.play(BaseGame.soundVolume)
                2 -> BaseGame.caneSound!!.play(BaseGame.soundVolume)
                3 -> BaseGame.paddleSound!!.play(BaseGame.soundVolume)
            }
        }

        BaseGame.tempo = 1f // break slow motion
        var randomHitAnimation: Int = MathUtils.random(0, hitAnimations.size - 1)
        changeAnimation(hitAnimations[randomHitAnimation], originalWidth * 4)
        addAction(Actions.sequence(
            Actions.moveBy(distance, MathUtils.random(-2f, 2f), .25f),
            Actions.delay(hitAnimations[randomHitAnimation].frameDuration * hitAnimations[randomHitAnimation].keyFrames.size),
            Actions.run { changeAnimation(idleAnimation) }
        ))
    }

    fun struck(moveToRight: Boolean) {
        health--
        if (health >= 1) BaseGame.tempo = slowMotionModifier // start slow motion

        if (moveToRight) addAction(Actions.moveBy(10f, 0f, .05f))
        else addAction(Actions.moveBy(-10f, 0f, .05f))

        // face striking enemy
        addAction(Actions.sequence(
            Actions.delay(.0625f), // HACK: give enemies a chance to react first
            Actions.run {
                if ((moveToRight && isFacingRight || !moveToRight && !isFacingRight) && health >= 1)
                    flip()
            }
        ))
    }

    private fun setNewTempo() {
        tempo = BaseGame.tempo
        setAcceleration(playerAcceleration * BaseGame.tempo)
        setMaxSpeed(playerSpeed * BaseGame.tempo) // (world units)/seconds
        setDeceleration(playerDeceleration * BaseGame.tempo)
    }

    private fun changeAnimation(
        animation: Animation<TextureAtlas.AtlasRegion>,
        width: Float = this.width,
        height: Float = this.height
    ) {
        setAnimation(animation)
        setSize(originalWidth, originalHeight)
        setAnimationSize(width, height)
    }

    private fun shouldFlip(direction: Float) {
        if (direction < 0 && isFacingRight)
            flip()
        else if (direction >= 0 && !isFacingRight)
            flip()
    }
}
