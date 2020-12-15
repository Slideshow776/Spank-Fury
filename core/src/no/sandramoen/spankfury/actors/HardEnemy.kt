package no.sandramoen.spankfury.actors

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.Array
import no.sandramoen.spankfury.utils.BaseGame

class HardEnemy(x: Float, y: Float, s: Stage, player: Player) : Enemy(x, y, s, player) {
    private val token = "HardEnemy.kt"
    override var health = 4

    init {
        setSize(
                (BaseGame.WORLD_WIDTH / 9) * BaseGame.scale,
                (BaseGame.WORLD_HEIGHT / 2.5f) * BaseGame.scale
        )
        color = Color.WHITE
        originalColor = Color.WHITE
        originalWidth = width
        originalHeight = height
        points = 40
    }

    override fun setAnimation() {
        var animationImages: Array<TextureAtlas.AtlasRegion> = Array()
        for (i in 1..16) animationImages.add(BaseGame.textureAtlas!!.findRegion("hardEnemy-idle-01"))
        for (i in 1..2) animationImages.add(BaseGame.textureAtlas!!.findRegion("hardEnemy-idle-02"))
        for (i in 1..2) animationImages.add(BaseGame.textureAtlas!!.findRegion("hardEnemy-idle-03"))
        for (i in 1..2) animationImages.add(BaseGame.textureAtlas!!.findRegion("hardEnemy-idle-04"))
        animationImages.add(BaseGame.textureAtlas!!.findRegion("hardEnemy-idle-05"))
        idleAnimation = Animation(.03f, animationImages, Animation.PlayMode.LOOP)
        animationImages.clear()

        for (i in 1..8)
            animationImages.add(BaseGame.textureAtlas!!.findRegion("hardEnemy-walking-0$i"))
        walkingAnimation = Animation(.25f, animationImages, Animation.PlayMode.LOOP)
        animationImages.clear()

        for (i in 1..4)
            animationImages.add(BaseGame.textureAtlas!!.findRegion("hardEnemy-hitting-0$i"))
        hittingAnimation = Animation(.05f, animationImages, Animation.PlayMode.NORMAL)
        animationImages.clear()

        animationImages.add(BaseGame.textureAtlas!!.findRegion("hardEnemy-stunned-01"))
        stunnedAnimation = Animation(1f, animationImages, Animation.PlayMode.NORMAL)
        animationImages.clear()

        animationImages.add(BaseGame.textureAtlas!!.findRegion("hardEnemy-dying-01"))
        deadAnimation = Animation(1f, animationImages, Animation.PlayMode.NORMAL)
        animationImages.clear()

        setAnimation(walkingAnimation)
    }
}