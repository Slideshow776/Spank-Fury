package no.sandramoen.spankfury.utils

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Event
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.*

class GameUtils {
    private val token = "GameUtils.kt"

    companion object {
        fun isTouchDownEvent(e: Event): Boolean { // Custom type checker
            return e is InputEvent && e.type == InputEvent.Type.touchDown
        }

        fun saveGameState() {
            try {
                BaseGame.prefs!!.putInteger("highScore", BaseGame.highScore)
            } catch (error: Error) {
                BaseGame.prefs!!.putInteger("highScore", Int.MAX_VALUE)
            }
            BaseGame.prefs!!.putInteger("mysteryKinksterScore", BaseGame.mysteryKinksterScore)
            BaseGame.prefs!!.putBoolean("vibrations", BaseGame.vibrations)
            BaseGame.prefs!!.putBoolean("googlePlayServices", BaseGame.disableGPS)
            BaseGame.prefs!!.putBoolean("loadPersonalParameters", true)
            BaseGame.prefs!!.putFloat("musicVolume", BaseGame.musicVolume)
            BaseGame.prefs!!.putFloat("soundVolume", BaseGame.soundVolume)
            BaseGame.prefs!!.flush()
        }

        fun loadGameState() {
            BaseGame.prefs = Gdx.app.getPreferences("spankFuryGameState")
            BaseGame.highScore = BaseGame.prefs!!.getInteger("highScore")
            BaseGame.mysteryKinksterScore = BaseGame.prefs!!.getInteger("mysteryKinksterScore")
            BaseGame.vibrations = BaseGame.prefs!!.getBoolean("vibrations")
            BaseGame.disableGPS = BaseGame.prefs!!.getBoolean("googlePlayServices")
            BaseGame.loadPersonalParameters = BaseGame.prefs!!.getBoolean("loadPersonalParameters")
            BaseGame.musicVolume = BaseGame.prefs!!.getFloat("musicVolume")
            BaseGame.soundVolume = BaseGame.prefs!!.getFloat("soundVolume")
        }

        fun pulseLabel(label: Label, lowestAlpha: Float = .5f, duration: Float = .5f) {
            label.addAction(Actions.forever(Actions.sequence(
                    Actions.alpha(1f, duration),
                    Actions.alpha(lowestAlpha, duration)
            )))
        }

        fun playAndLoopRandomMusic() {
            var music: Music? = null
            when (MathUtils.random(1, 3)) {
                1 -> music = BaseGame.levelMusic1
                2 -> music = BaseGame.levelMusic2
                3 -> music = BaseGame.levelMusic3
            }
            setMusicLoopingAndPlay(music)
        }

        fun stopAllMusic() {
            BaseGame.levelMusic1!!.stop()
            BaseGame.levelMusic2!!.stop()
            BaseGame.levelMusic3!!.stop()
        }

        fun setMusicVolume() {
            BaseGame.levelMusic1!!.volume = BaseGame.musicVolume
            BaseGame.levelMusic2!!.volume = BaseGame.musicVolume
            BaseGame.levelMusic3!!.volume = BaseGame.musicVolume
        }

        private fun setMusicLoopingAndPlay(music: Music?) {
            music!!.play()
            music!!.volume = BaseGame.musicVolume
            music!!.isLooping = true
        }

        fun enableActorsWithDelay(actor: Actor, delay: Float = .5f) {
            actor.addAction(Actions.sequence(
                    Actions.delay(delay),
                    Actions.run { actor.touchable = Touchable.enabled }
            ))
        }
    }
}
