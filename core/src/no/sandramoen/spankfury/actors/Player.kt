package no.sandramoen.spankfury.actors

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.Interpolation
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
    private var highKickAnimation: Animation<TextureAtlas.AtlasRegion>
    private var hitAnimations: Array<Animation<TextureAtlas.AtlasRegion>> = Array()

    // properties
    var playerSpeed = 40f
    var playerAcceleration = 70f
    var playerDeceleration = 70f
    var health = 3
    var slowMotion = false

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
        highKickAnimation = Animation(.01f, animationImages, Animation.PlayMode.NORMAL)
        hitAnimations.add(highKickAnimation)
        animationImages.clear()

        setAnimation(idleAnimation)

        // other
        setSize(originalWidth, originalHeight)
        setAnimationSize(originalWidth, originalHeight)
        centerAtPosition(BaseGame.WORLD_WIDTH / 2, BaseGame.WORLD_HEIGHT / 3)

        // physics
        setAcceleration(playerAcceleration * BaseGame.tempo)
        setMaxSpeed(playerSpeed * BaseGame.tempo) // (world units)/seconds
        setDeceleration(playerDeceleration * BaseGame.tempo)
    }

    override fun act(dt: Float) {
        super.act(dt * BaseGame.tempo)
        if (pause) return
        if (BaseGame.tempo != tempo) setNewTempo()
        applyPhysics(dt)
        alignCamera(lerp = .1f)
    }

    fun hit(distance: Float) {
        shouldFlip(distance)
        BaseGame.tempo = 1f // break slow motion
        var index: Int = MathUtils.random(0, hitAnimations.size - 1)
        changeAnimation(hitAnimations[index], originalWidth * 4)
        addAction(Actions.sequence(
                Actions.moveBy(distance * .6f, 0f, .25f),
                Actions.delay(hitAnimations[index].frameDuration * hitAnimations[index].keyFrames.size),
                Actions.run { changeAnimation(idleAnimation) }
        ))
    }

    fun struck(moveToRight: Boolean) {
        var direction = -1f
        if (moveToRight) direction = 1f

        health--
        if (health >= 1) BaseGame.tempo = slowMotionModifier // start slow motion

        addAction(Actions.moveBy(direction * 10f, 0f, .25f))
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

    private fun changeAnimation(animation: Animation<TextureAtlas.AtlasRegion>, width: Float = this.width, height: Float = this.height) {
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
