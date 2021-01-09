package no.sandramoen.spankfury.tables

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

class LevelGameOverTable : Table() {

    private var gameOverScoreLabel: Label

    private var highscores: ArrayList<Pair<String, Int>>

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
        highscores = arrayListOf()
        highscores.add(Pair("Mystery Kinkster", 250_000))
        highscores.add(Pair("Top", 100_000))
        highscores.add(Pair("Slut", 80_000))
        highscores.add(Pair("Princess", 60_000))
        highscores.add(Pair("Leatherman", 50_000))
        highscores.add(Pair("Rope Bunny", 40_000))
        highscores.add(Pair("Fetishist", 35_000))
        highscores.add(Pair("Sadist", 30_000))
        highscores.add(Pair("Vanilla", 25_000))
        highscores.add(Pair("You", 0))

        for (i in 0 until highscores.size) {
            val tempName = Label(highscores[i].first, BaseGame.labelStyle)
            tempName.setAlignment(Align.left)
            tempName.name = "name$i"
            val tempScore = Label("${highscores[i].second}", BaseGame.labelStyle)
            tempScore.setAlignment(Align.right)
            tempScore.name = "score$i"
            val index = Label("${i + 1}", BaseGame.labelStyle)
            index.name = "index$i"
            index.setAlignment(Align.right)

            if (highscores[i].first == "You") {
                tempName.color = BaseGame.red
                tempScore.color = BaseGame.red
                index.color = BaseGame.red
            }

            val tempTable = Table()
            tempTable.add(index).width(Gdx.graphics.width * .04f).padRight(Gdx.graphics.width * .01f)
            tempTable.add(tempName).width(Gdx.graphics.width * .33f)
            tempTable.add(tempScore).width(Gdx.graphics.width * .33f)
            highScoreTable.add(tempTable).padBottom(Gdx.graphics.height * .01f).row()
        }
        updateHighScoreTable()
        // highScoreTable.debug = true

        gameOverMenuButton = TextButton("< Menu", BaseGame.textButtonStyle)
        gameOverMenuButton.color = BaseGame.yellow
        gameOverMenuButton.touchable = Touchable.disabled
        gameOverMenuButton.addListener(object : ActorGestureListener() {
            override fun tap(event: InputEvent?, x: Float, y: Float, count: Int, button: Int) {
                addAction(
                    Actions.sequence(
                        Actions.fadeIn(1f),
                        Actions.run {
                            BaseGame.tempo = 1f // reset
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
                        Actions.fadeIn(1f),
                        Actions.run {
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
        add(gameOverMenuButton).left()
        add(gameOverPlayButton).right()
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
        for (i in 0 until highscores.size) {
            highScoreTable.findActor<Label>("name$i").setText(highscores[i].first)
            highScoreTable.findActor<Label>("score$i").setText(highscores[i].second)
        }

        // update player score
        for (i in 0 until highscores.size) {
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
                return
            }
        }
    }

    fun setMotivationalText() {
        if (BaseGame.highScore >= highScoreTable.findActor<Label>("score0").text.toString().toInt()) {
            motivationalText.setText("You are the Mystery Kinkster!")
            motivationalText.color = Color.PURPLE
        }
        else if (newHighScore) {
            motivationalText.setText("New High Score!")
            motivationalText.color = Color.PURPLE
        }
        else {
            motivationalText.setText("Can you outspank the Mystery Kinkster?")
            motivationalText.color = Color.LIGHT_GRAY
        }
    }
}
