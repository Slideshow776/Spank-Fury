package no.sandramoen.spankfury.actors

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Stage
import no.sandramoen.spankfury.utils.BaseGame

class EasyEnemy(x: Float, y: Float, s: Stage, player: Player) : Enemy(x, y, s, player) {
    private val token = "EasyEnemy.kt"
    override var health = 1

    init {
        setSize(BaseGame.WORLD_WIDTH / 12, BaseGame.WORLD_HEIGHT / 3)
        color = Color.RED
        originalColor = Color.RED
        originalWidth = width
        originalHeight = height
        points = 10
    }
}