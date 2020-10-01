package no.sandramoen.spankfury.utils

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.Texture.TextureFilter
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.*
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.scenes.scene2d.Group

open class BaseActor(x: Float, y: Float, s: Stage) : Group() {
    private val token = "BaseActor.kt"
    private var animation: Animation<TextureRegion>?
    private var elapsedTime: Float = 0F
    private var animationPaused: Boolean = false

    private var velocityVec: Vector2 = Vector2(0f, 0f)
    private var accelerationVec: Vector2 = Vector2(0f, 0f)
    private var acceleration: Float = 0f
    private var maxSpeed: Float = 1000f
    private var deceleration: Float = 0f

    init {
        this.x = x
        this.y = y
        s.addActor(this)
        animation = null
    }

    override fun act(dt: Float) {
        super.act(dt)

        if (!animationPaused)
            elapsedTime += dt
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        //  apply color tint effect
        val c: Color = color
        batch.setColor(c.r, c.g, c.b, c.a)

        if (animation != null && isVisible) {
            batch.draw(
                    animation!!.getKeyFrame(elapsedTime),
                    x,
                    y,
                    originX,
                    originY,
                    width,
                    height,
                    scaleX,
                    scaleY,
                    rotation
            )
        }
        super.draw(batch, parentAlpha)
    }

    // Graphics ---------------------------------------------------------------------------------------------------
    private fun setAnimation(anim: Animation<TextureRegion>, loop: Boolean) { // TODO: might not need this
        animation = anim
        val tr: TextureRegion = animation!!.getKeyFrame(0.toFloat())
        val w: Float = tr.regionWidth.toFloat()
        val h: Float = tr.regionHeight.toFloat()
        setSize(w, h)
        setOrigin(w / 2, h / 2)

        if (loop)
            anim.playMode = Animation.PlayMode.LOOP
        else
            anim.playMode = Animation.PlayMode.NORMAL
    }

    fun setAnimationPaused(pause: Boolean) {
        animationPaused = pause
    }

    fun loadTexture(fileName: String): Animation<TextureRegion> {
        val fileNames: Array<String> = Array(1)
        fileNames.add(fileName)
        return loadAnimationFromFiles(fileNames, 1f, true)
    }

    private fun loadAnimationFromFiles(fileNames: Array<String>, frameDuration: Float, loop: Boolean,
                                       textureFilter: TextureFilter = TextureFilter.Linear): Animation<TextureRegion> {  // TODO: might not need this
        val textureArray: Array<TextureRegion> = Array()

        for (i in 0 until fileNames.size) {
            val texture = Texture(Gdx.files.internal(fileNames[i]))
            texture.setFilter(textureFilter, textureFilter)
            textureArray.add(TextureRegion(texture))
        }

        val anim: Animation<TextureRegion> = Animation(frameDuration, textureArray)

        if (loop)
            anim.playMode = Animation.PlayMode.LOOP
        else
            anim.playMode = Animation.PlayMode.NORMAL

        if (animation == null)
            setAnimation(anim, loop)

        return anim
    }

    fun loadImage(name: String) {
        val region = BaseGame.textureAtlas!!.findRegion(name)
        setAnimation(Animation(1f, region), false)
    }

    fun loadAnimation(region: Array<TextureAtlas.AtlasRegion>, frameDuration: Float, loop: Boolean) {
        setAnimation(Animation(frameDuration, region), loop)
    }

    fun isAnimationFinished(): Boolean {
        return animation!!.isAnimationFinished(elapsedTime)
    }

    // Physics ---------------------------------------------------------------------------------------------------
    fun setSpeed(speed: Float) {
        // If length is zero, then assume motion angle is zero degrees
        // println("$token: ${velocityVec.len()}")
        if (velocityVec.len() == 0f)
            velocityVec.set(speed, 0f)
        else
            velocityVec.setLength(speed)
    }

    fun getSpeed() = velocityVec.len()
    fun setMotionAngle(angle: Float) {
        velocityVec.setAngle(angle)
        println("$token: $angle, ${velocityVec.angle()}")
    }

    fun getMotionAngle() = velocityVec.angle()
    fun isMoving() = getSpeed() > 0
    fun setAcceleration(acc: Float) {
        acceleration = acc
    }

    fun accelerateAtAngle(angle: Float) = accelerationVec.add(Vector2(acceleration, 0f).setAngle(angle))
    fun accelerateForward() = accelerateAtAngle(rotation)
    fun setMaxSpeed(ms: Float) {
        maxSpeed = ms
    }

    fun setDeceleration(dec: Float) {
        deceleration = dec
    }

    fun applyPhysics(dt: Float) {
        // apply acceleration
        velocityVec.add(accelerationVec.x * dt, accelerationVec.y * dt)

        var speed = getSpeed()

        // decrease speed (decelerate) when not accelerating
        if (accelerationVec.len() == 0f)
            speed -= deceleration * dt

        // keep speed within set bounds
        speed = MathUtils.clamp(speed, 0f, maxSpeed)

        // update velocity
        setSpeed(speed)

        // apply velocity
        moveBy(velocityVec.x * dt, velocityVec.y * dt)

        // reset acceleration
        accelerationVec.set(0f, 0f)
    }

    // camera -------------------------------------------------------------------------------------------------
    fun alignCamera(target: Vector2 = Vector2(x, y), lerp: Float = 1f) {
        if (this.stage != null) {
            val camera = this.stage.camera

            // center camera on actor
            val position = camera.position
            position.x = camera.position.x + (target.x + width / 2 - camera.position.x) * lerp
            position.y = camera.position.y + (target.y + height / 2 - camera.position.y) * lerp
            camera.position.set(position)

            camera.update()
        }
    }

    // miscellaneous ------------------------------------------------------------------------------------------
    fun centerAtPosition(x: Float, y: Float) = setPosition(x - width / 2, y - height / 2)
    fun centerAtActor(other: BaseActor) = centerAtPosition(other.x + other.width / 2, other.y + other.height / 2)
    fun setOpacity(opacity: Float) {
        this.color.a = opacity
    }

    companion object {
        fun getList(stage: Stage, className: String): ArrayList<BaseActor> {
            var list: ArrayList<BaseActor> = ArrayList()

            var theClass: Class<*>? = null
            try {
                theClass = Class.forName(className)
            } catch (error: Exception) {
                error.printStackTrace()
            }

            for (actor in stage.actors) {
                if (theClass!!.isInstance(actor)) {
                    list.add(actor as BaseActor)
                }
            }
            return list
        }

        fun count(stage: Stage, className: String): Int {
            return getList(stage, className).size
        }
    }
}