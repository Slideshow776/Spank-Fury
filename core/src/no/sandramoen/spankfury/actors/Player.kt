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

    var playerSpeed = 40f
    var playerAcceleration = 70f
    var playerDeceleration = 70f

    var health = 3

    // animations
    private var idleAnimation: Animation<TextureAtlas.AtlasRegion>
    private var highKickAnimation: Animation<TextureAtlas.AtlasRegion>
    private var uppercutAnimation: Animation<TextureAtlas.AtlasRegion>

    private var hitAnimations: Array<Animation<TextureAtlas.AtlasRegion>> = Array()

    init {
        // animations

        var animationImages: Array<TextureAtlas.AtlasRegion> = Array()
        for (i in 1..4)
            animationImages.add(BaseGame.textureAtlas!!.findRegion("player-idle-0$i"))
        idleAnimation = Animation(.25f, animationImages, Animation.PlayMode.LOOP)
        animationImages.clear()

        for (i in 1..6)
            animationImages.add(BaseGame.textureAtlas!!.findRegion("player-highKick-0$i"))
        highKickAnimation = Animation(.08f, animationImages, Animation.PlayMode.NORMAL)
        hitAnimations.add(highKickAnimation)
        animationImages.clear()

        for (i in 1..6)
            animationImages.add(BaseGame.textureAtlas!!.findRegion("player-uppercut-0$i"))
        uppercutAnimation = Animation(.08f, animationImages, Animation.PlayMode.NORMAL)
        hitAnimations.add(uppercutAnimation)
        animationImages.clear()

        setAnimation(idleAnimation)

        // other
        setSize(BaseGame.WORLD_WIDTH / 12, BaseGame.WORLD_HEIGHT / 3)
        centerAtPosition(BaseGame.WORLD_WIDTH / 2, BaseGame.WORLD_HEIGHT / 2)

        // physics
        setAcceleration(playerAcceleration)
        setMaxSpeed(playerSpeed) // (world units)/seconds
        setDeceleration(playerDeceleration)
    }

    override fun act(dt: Float) {
        super.act(dt)
        if (pause) return
        applyPhysics(dt)
        alignCamera(lerp = .1f)
    }

    fun hit(distance: Float) {
        shouldFlip(distance)
        var index: Int = MathUtils.random(0, hitAnimations.size - 1)
        changeAnimation(hitAnimations[index])
        addAction(Actions.sequence(
                Actions.moveBy(distance * .6f, 0f, .25f),
                Actions.delay(hitAnimations[index].frameDuration * hitAnimations[index].keyFrames.size),
                Actions.run {
                    changeAnimation(idleAnimation)
                }
        ))
    }

    fun struck() {
        health--
        if (actions.size == 0)
            addAction(Actions.sequence(
                    Actions.sizeBy(-4f, 4f, .1f, Interpolation.circleIn),
                    Actions.sizeTo(width, height, 1f, Interpolation.bounceOut)
            ))
    }

    private fun changeAnimation(animation: Animation<TextureAtlas.AtlasRegion>) {
        setAnimation(animation)
        setSize(BaseGame.WORLD_WIDTH / 12, BaseGame.WORLD_HEIGHT / 3)
    }

    private fun shouldFlip(direction: Float) {
        if (direction < 0 && isFacingRight)
            flip()
        else if (direction >= 0 && !isFacingRight)
            flip()
    }
}
