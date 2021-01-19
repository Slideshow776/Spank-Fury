package no.sandramoen.spankfury.actors

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.Array
import no.sandramoen.spankfury.utils.BaseGame

class EasyEnemy(x: Float, y: Float, s: Stage, player: Player, originalSpeed: Float, hittingDelay: Float) : BaseEnemy(x, y, s, player, originalSpeed, hittingDelay) {
    private val token = "EasyEnemy.kt"
    override var health = 1

    init {
        setSize(
                (BaseGame.WORLD_WIDTH / 12) * BaseGame.scale,
                (BaseGame.WORLD_HEIGHT / 3) * BaseGame.scale
        )
        color = Color.WHITE
        originalColor = Color.WHITE
        originalWidth = width
        originalHeight = height
        points = 10
    }

    override fun setAnimation() {
        var animationImages: Array<TextureAtlas.AtlasRegion> = Array()
        for (i in 1..4) animationImages.add(BaseGame.textureAtlas!!.findRegion("easyEnemy-idle-01"))
        animationImages.add(BaseGame.textureAtlas!!.findRegion("easyEnemy-idle-02"))
        idleAnimation = Animation(.2f, animationImages, Animation.PlayMode.LOOP)
        animationImages.clear()

        for (i in 1..8) animationImages.add(BaseGame.textureAtlas!!.findRegion("easyEnemy-walking-0$i"))
        walkingAnimation = Animation(.15f, animationImages, Animation.PlayMode.LOOP)
        animationImages.clear()

        for (i in 1..2) animationImages.add(BaseGame.textureAtlas!!.findRegion("easyEnemy-hitting-01"))
        animationImages.add(BaseGame.textureAtlas!!.findRegion("easyEnemy-hitting-02"))
        animationImages.add(BaseGame.textureAtlas!!.findRegion("easyEnemy-hitting-03"))
        for (i in 1..4) animationImages.add(BaseGame.textureAtlas!!.findRegion("easyEnemy-hitting-04"))
        hittingAnimation = Animation(.05f, animationImages, Animation.PlayMode.NORMAL)
        animationImages.clear()

        animationImages.add(BaseGame.textureAtlas!!.findRegion("easyEnemy-stunned-01"))
        stunnedAnimation = Animation(1f, animationImages, Animation.PlayMode.NORMAL)
        animationImages.clear()

        animationImages.add(BaseGame.textureAtlas!!.findRegion("easyEnemy-dying-01"))
        deadAnimation = Animation(1f, animationImages, Animation.PlayMode.NORMAL)
        animationImages.clear()

        setAnimation(walkingAnimation)
    }
}