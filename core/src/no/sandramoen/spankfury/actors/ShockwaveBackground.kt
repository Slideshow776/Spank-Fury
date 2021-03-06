package no.sandramoen.spankfury.actors

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.scenes.scene2d.Event
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction
import no.sandramoen.spankfury.utils.BaseActor
import no.sandramoen.spankfury.utils.BaseGame
import no.sandramoen.spankfury.utils.GameUtils

class ShockwaveBackground(x: Float, y: Float, texturePath: String, s: Stage) : BaseActor(x, y, s) {
    private var vertexShaderCode: String
    private var fragmenterShaderCode: String
    var shaderProgram: ShaderProgram

    private var time = .0f
    private var shockWavePositionX = .0f
    private var shockWavePositionY = .0f
    private var disabled = true

    init {
        if (texturePath.isNotBlank()) loadTexture(texturePath)
        setSize(100f, 100f)

        ShaderProgram.pedantic = false
        vertexShaderCode = BaseGame.defaultShader.toString()
        fragmenterShaderCode = BaseGame.shockwaveShader.toString()
        shaderProgram = ShaderProgram(vertexShaderCode, fragmenterShaderCode)
        if (!shaderProgram.isCompiled)
            Gdx.app.error("ShockwaveBackground", "Shader compile error: " + shaderProgram.log)

        addListener { e: Event ->
            if (GameUtils.isTouchDownEvent(e)) {
                val x = (Gdx.input.x.toFloat() - 0) / (Gdx.graphics.width - 0)
                val y = (Gdx.input.y.toFloat() - 0) / (Gdx.graphics.height - 0)
                start(x, y) // x and y are normalized
            }
            false
        }
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        if (disabled)
            super.draw(batch, parentAlpha)
        else {
            try {
                batch.shader = shaderProgram
                shaderProgram.setUniformf("time", time)
                shaderProgram.setUniformf("center", Vector2(shockWavePositionX, shockWavePositionY))
                shaderProgram.setUniformf("shockParams", Vector3(10f, .8f, .1f))
                super.draw(batch, parentAlpha)
                batch.shader = null
            } catch (error: Error) {
                super.draw(batch, parentAlpha)
            }
        }
    }

    override fun act(dt: Float) {
        super.act(dt)
        time += dt
    }

    fun start(normalizedPosX: Float, normalizedPosY: Float) {
        if (time >= 1f) { // prevents interrupting previous animation
            this.shockWavePositionX = normalizedPosX
            this.shockWavePositionY = normalizedPosY
            val enable = RunnableAction()
            enable.runnable = Runnable { disabled = true }
            this.addAction(Actions.delay(1f, enable))
            disabled = false
            time = 0f
        }
    }
}
