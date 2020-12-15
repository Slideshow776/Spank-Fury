package no.sandramoen.spankfury.actors

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.Array
import no.sandramoen.spankfury.utils.BaseGame

class MediumEnemy(x: Float, y: Float, s: Stage, player: Player) : Enemy(x, y, s, player) {
    private val token = "MediumEnemy.kt"
    override var health = 2

    init {
        setSize(
                (BaseGame.WORLD_WIDTH / 11) * BaseGame.scale,
                (BaseGame.WORLD_HEIGHT / 2.75f) * BaseGame.scale
        )
        color = Color.WHITE
        originalColor = Color.WHITE
        originalWidth = width
        originalHeight = height
        points = 20
    }

    override fun setAnimation() {
        var animationImages: Array<TextureAtlas.AtlasRegion> = Array()
        for (i in 1..16) animationImages.add(BaseGame.textureAtlas!!.findRegion("mediumEnemy-idle-01"))
        for (i in 1..2) animationImages.add(BaseGame.textureAtlas!!.findRegion("mediumEnemy-idle-02"))
        animationImages.add(BaseGame.textureAtlas!!.findRegion("mediumEnemy-idle-03"))
        idleAnimation = Animation(.03f, animationImages, Animation.PlayMode.LOOP)
        animationImages.clear()

        for (i in 1..8)
            animationImages.add(BaseGame.textureAtlas!!.findRegion("mediumEnemy-walking-0$i"))
        walkingAnimation = Animation(.25f, animationImages, Animation.PlayMode.LOOP)
        animationImages.clear()

        for (i in 1..2) animationImages.add(BaseGame.textureAtlas!!.findRegion("mediumEnemy-hitting-01"))
        animationImages.add(BaseGame.textureAtlas!!.findRegion("mediumEnemy-hitting-02"))
        animationImages.add(BaseGame.textureAtlas!!.findRegion("mediumEnemy-hitting-03"))
        for (i in 1..4) animationImages.add(BaseGame.textureAtlas!!.findRegion("mediumEnemy-hitting-04"))
        hittingAnimation = Animation(.05f, animationImages, Animation.PlayMode.NORMAL)
        animationImages.clear()

        animationImages.add(BaseGame.textureAtlas!!.findRegion("mediumEnemy-stunned-01"))
        stunnedAnimation = Animation(1f, animationImages, Animation.PlayMode.NORMAL)
        animationImages.clear()

        animationImages.add(BaseGame.textureAtlas!!.findRegion("mediumEnemy-dying-01"))
        deadAnimation = Animation(1f, animationImages, Animation.PlayMode.NORMAL)
        animationImages.clear()

        setAnimation(walkingAnimation)
    }
}