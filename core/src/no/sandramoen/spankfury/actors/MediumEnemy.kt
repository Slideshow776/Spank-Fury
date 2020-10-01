package no.sandramoen.spankfury.actors

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Stage
import no.sandramoen.spankfury.utils.BaseGame

class MediumEnemy(x: Float, y: Float, s: Stage, player: Player) : Enemy(x, y, s, player) {
    private val token = "MediumEnemy.kt"
    override var health = 2

    init {
        loadImage("whitePixel")
        setSize(BaseGame.WORLD_WIDTH / 11, BaseGame.WORLD_HEIGHT / 2.75f)
        centerAtPosition(startPosition, BaseGame.WORLD_HEIGHT / 2)
        color = Color.BLUE
    }
}