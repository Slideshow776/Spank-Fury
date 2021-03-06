package no.sandramoen.spankfury.tables

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener
import com.badlogic.gdx.utils.Align
import no.sandramoen.spankfury.screens.gameplay.LevelScreen
import no.sandramoen.spankfury.screens.shell.MenuScreen
import no.sandramoen.spankfury.utils.BaseGame
import no.sandramoen.spankfury.utils.GameUtils

class LevelGameOverTable : Table() {

    private var gameOverScoreLabel: Label

    private var highScores: ArrayList<Pair<String, Int>>

    private var gameOverMenuButton: TextButton
    private var gameOverPlayButton: TextButton

    private var highScoreTable: Table

    private var blackOverlay: Image

    private var motivationalText: Label

    var newHighScore = false

    init {
        val gameOverScoreTable = Table()
        gameOverScoreLabel = Label("", BaseGame.labelStyle)
        gameOverScoreLabel.setFontScale(2f)
        val gameOverScoreLabelLabel = Label("SCORE", BaseGame.labelStyle)
        gameOverScoreLabelLabel.color = Color.GOLD
        gameOverScoreTable.add(gameOverScoreLabel).padBottom(Gdx.graphics.height * .01f).row()
        gameOverScoreTable.add(gameOverScoreLabelLabel)

        highScoreTable = Table()
        highScores = arrayListOf()
        highScores.add(Pair("Mystery Kinkster", 250_000))
        highScores.add(Pair("Top", 100_000))
        highScores.add(Pair("Slut", 80_000))
        highScores.add(Pair("Princess", 60_000))
        highScores.add(Pair("Leatherman", 50_000))
        highScores.add(Pair("Rope Bunny", 40_000))
        highScores.add(Pair("Fetishist", 35_000))
        highScores.add(Pair("Sadist", 30_000))
        highScores.add(Pair("Vanilla", 25_000))
        highScores.add(Pair("You", 0))

        for (i in 0 until highScores.size) {
            // labels
            val tempName = Label(highScores[i].first, BaseGame.labelStyle)
            tempName.setAlignment(Align.left)
            tempName.name = "name$i"
            val tempScore = Label("${highScores[i].second}", BaseGame.labelStyle)
            tempScore.setAlignment(Align.right)
            tempScore.name = "score$i"
            val index = Label("${i + 1}", BaseGame.labelStyle)
            index.name = "index$i"
            index.setAlignment(Align.right)

            // mystery kinkster
            if (i == 0) {
                tempName.color = Color.PURPLE
                tempScore.color = Color.PURPLE
                index.color = Color.PURPLE
            }

            // player
            if (highScores[i].first == "You") {
                tempName.color = BaseGame.red
                tempScore.color = BaseGame.red
                index.color = BaseGame.red
            }

            // table
            val tempTable = Table()
            tempTable.add(index).width(Gdx.graphics.width * .04f).padRight(Gdx.graphics.width * .01f)
            tempTable.add(tempName).width(Gdx.graphics.width * .33f)
            tempTable.add(tempScore).width(Gdx.graphics.width * .33f)
            highScoreTable.add(tempTable).padBottom(Gdx.graphics.height * .01f).row()
        }
        // updateHighScoreTable() // TODO: Do I need to call this here?
        // highScoreTable.debug = true

        gameOverMenuButton = TextButton("< Menu", BaseGame.textButtonStyle)
        gameOverMenuButton.color = BaseGame.yellow
        gameOverMenuButton.touchable = Touchable.disabled
        gameOverMenuButton.addListener(object : ActorGestureListener() {
            override fun tap(event: InputEvent?, x: Float, y: Float, count: Int, button: Int) {
                addAction(
                    Actions.sequence(
                        Actions.run { BaseGame.clickSound!!.play(BaseGame.soundVolume) },
                        Actions.fadeOut(1f),
                        Actions.run {
                            BaseGame.tempo = 1f // reset
                            GameUtils.stopAllMusic()
                            BaseGame.setActiveScreen(MenuScreen())
                        }
                    ))
            }
        })
        gameOverPlayButton = TextButton("Play", BaseGame.textButtonStyle)
        gameOverPlayButton.color = BaseGame.green
        gameOverPlayButton.touchable = Touchable.disabled
        gameOverPlayButton.addListener(object : ActorGestureListener() {
            override fun tap(event: InputEvent?, x: Float, y: Float, count: Int, button: Int) {
                addAction(
                    Actions.sequence(
                        Actions.run { BaseGame.clickSound!!.play(BaseGame.soundVolume) },
                        Actions.fadeOut(1f),
                        Actions.run {
                            GameUtils.stopAllMusic()
                            BaseGame.tempo = 1f // reset
                            BaseGame.setActiveScreen(LevelScreen())
                        }
                    ))
            }
        })

        motivationalText = Label("", BaseGame.labelStyle)
        motivationalText.setFontScale(.75f)
        setMotivationalText()

        setFillParent(true)
        color.a = 0f
        add(gameOverScoreTable).colspan(2).padBottom(Gdx.graphics.height * .05f).row()
        add(motivationalText).colspan(2).padBottom(Gdx.graphics.height * .05f).row()
        add(highScoreTable).colspan(2).padBottom(Gdx.graphics.height * .05f).row()
        add(gameOverMenuButton).width(Gdx.graphics.width * .25f).height(Gdx.graphics.height * .125f).left()
        add(gameOverPlayButton).width(Gdx.graphics.width * .25f).height(Gdx.graphics.height * .125f).right()
        // debug = true

        // black transition overlay
        blackOverlay = Image(BaseGame.textureAtlas!!.findRegion("whitePixel"))
        blackOverlay.color = Color.BLACK
        blackOverlay.touchable = Touchable.childrenOnly
        blackOverlay.setSize(Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
        blackOverlay.addAction(Actions.fadeOut(1f))
    }

    fun enableButtons() {
        gameOverMenuButton.touchable = Touchable.enabled
        gameOverPlayButton.touchable = Touchable.enabled
    }

    fun fadeIn(overlayDuration: Float) {
        addAction(Actions.fadeIn(overlayDuration))
    }

    fun setScoreLabel(score: Int) {
        gameOverScoreLabel.setText("$score")
    }

    fun updateHighScoreTable() {
        // reinitialize list with proper names
        for (i in 0 until highScores.size) {
            highScoreTable.findActor<Label>("name$i").setText(highScores[i].first)
            highScoreTable.findActor<Label>("score$i").setText(highScores[i].second)
        }

        // check global score to mystery kinkster
        var globalHighScore = BaseGame.mysteryKinksterScore
        /*if (!BaseGame.disableGPS && Gdx.app.type == Application.ApplicationType.Android) {
            globalHighScore = BaseGame.gps!!.getHighScore()
            BaseGame.mysteryKinksterScore = globalHighScore
        }*/
        if (globalHighScore > highScoreTable.findActor<Label>("score0").text.toString().toInt())
            highScoreTable.findActor<Label>("score0").setText("$globalHighScore")

        // update player score
        for (i in 0 until highScores.size) {
            // val entryName = highScoreTable.findActor<Label>("name$i").text.toString()
            val entryScore = highScoreTable.findActor<Label>("score$i").text.toString().toInt()
            if (BaseGame.highScore >= entryScore) {
                // move the list downwards
                for (j in 9 downTo i + 1) {
                    highScoreTable.findActor<Label>("name$j").setText(
                        highScoreTable.findActor<Label>("name${j - 1}").text.toString()
                    )
                    highScoreTable.findActor<Label>("score$j").setText(
                        highScoreTable.findActor<Label>("score${j - 1}").text.toString()
                    )
                    highScoreTable.findActor<Label>("name$j").color = Color.WHITE
                    highScoreTable.findActor<Label>("score$j").color = Color.WHITE
                    highScoreTable.findActor<Label>("index$j").color = Color.WHITE
                }
                // insert the player
                highScoreTable.findActor<Label>("name$i").setText("You")
                highScoreTable.findActor<Label>("score$i").setText("${BaseGame.highScore}")
                highScoreTable.findActor<Label>("name$i").color = Color.RED
                highScoreTable.findActor<Label>("score$i").color = Color.RED
                highScoreTable.findActor<Label>("index$i").color = Color.RED

                // check if player is the mystery kinkster
                if (highScoreTable.findActor<Label>("name0").text.toString() == "You") {
                    highScoreTable.findActor<Label>("name0").setText("Mystery Kinkster (you)")
                    highScoreTable.findActor<Label>("name0").color = Color.PURPLE
                    highScoreTable.findActor<Label>("score0").color = Color.PURPLE
                    highScoreTable.findActor<Label>("index0").color = Color.PURPLE

                    highScoreTable.findActor<Label>("name1").setText("DomlyDom69")
                    highScoreTable.findActor<Label>("score1").setText("2500")
                }
                return
            }
        }
    }

    fun setMotivationalText() {
        if (BaseGame.highScore >= highScoreTable.findActor<Label>("score0").text.toString().toInt()) {
            motivationalText.setText("You are the Mystery Kinkster!")
            motivationalText.color = Color.LIGHT_GRAY
        } else if (newHighScore) {
            motivationalText.setText("New High Score!")
            motivationalText.color = Color.LIGHT_GRAY
        } else {
            motivationalText.setText("Can you outspank the Mystery Kinkster?")
            motivationalText.color = Color.LIGHT_GRAY
        }
    }
}
