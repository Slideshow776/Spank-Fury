package no.sandramoen.spankfury.actors

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.utils.Array
import no.sandramoen.spankfury.utils.BaseGame

class SwapEnemy(x: Float, y: Float, s: Stage, player: Player) : Enemy(x, y, s, player) {
    private val token = "SwapEnemy.kt"
    override var health = 3

    init {
        setSize(
                (BaseGame.WORLD_WIDTH / 14) * BaseGame.scale,
                (BaseGame.WORLD_HEIGHT / 3) * BaseGame.scale
        )
        color = Color.WHITE
        originalColor = Color.WHITE
        originalWidth = width
        originalHeight = height
        points = 30
    }

    override fun struck(enableSound: Boolean): Boolean { // returns true if enemy died
        if (enableSound) BaseGame.hitSound1!!.play(BaseGame.soundVolume)
        health--
        if (health <= 0)
            return handleDeath()
        swapSide()
        return false
    }

    private fun swapSide() {
        if (x <= player.x) { // if on left
            addAction(Actions.moveBy(2 * width, 0f, .1f)) // move to right side
        } else {
            addAction(Actions.moveBy(-2 * width, 0f, .1f)) // move to left side
        }
        flip()
    }


    override fun setAnimation() {
        var animationImages: Array<TextureAtlas.AtlasRegion> = Array()
        for (i in 1..16) animationImages.add(BaseGame.textureAtlas!!.findRegion("swapEnemy-idle-01"))
        for (i in 1..4) animationImages.add(BaseGame.textureAtlas!!.findRegion("swapEnemy-idle-02"))
        animationImages.add(BaseGame.textureAtlas!!.findRegion("swapEnemy-idle-03"))
        for (i in 1..4) animationImages.add(BaseGame.textureAtlas!!.findRegion("swapEnemy-idle-04"))
        idleAnimation = Animation(.03f, animationImages, Animation.PlayMode.LOOP)
        animationImages.clear()

        for (i in 1..9)
            animationImages.add(BaseGame.textureAtlas!!.findRegion("swapEnemy-walking-0$i"))
        walkingAnimation = Animation(.25f, animationImages, Animation.PlayMode.LOOP)
        animationImages.clear()

        for (i in 1..2) animationImages.add(BaseGame.textureAtlas!!.findRegion("swapEnemy-hitting-01"))
        animationImages.add(BaseGame.textureAtlas!!.findRegion("swapEnemy-hitting-02"))
        animationImages.add(BaseGame.textureAtlas!!.findRegion("swapEnemy-hitting-03"))
        animationImages.add(BaseGame.textureAtlas!!.findRegion("swapEnemy-hitting-04"))
        for (i in 1..4) animationImages.add(BaseGame.textureAtlas!!.findRegion("swapEnemy-hitting-05"))
        hittingAnimation = Animation(.05f, animationImages, Animation.PlayMode.NORMAL)
        animationImages.clear()

        animationImages.add(BaseGame.textureAtlas!!.findRegion("swapEnemy-stunned-01"))
        stunnedAnimation = Animation(1f, animationImages, Animation.PlayMode.NORMAL)
        animationImages.clear()

        animationImages.add(BaseGame.textureAtlas!!.findRegion("swapEnemy-dying-01"))
        deadAnimation = Animation(1f, animationImages, Animation.PlayMode.NORMAL)
        animationImages.clear()

        setAnimation(walkingAnimation)
    }
}