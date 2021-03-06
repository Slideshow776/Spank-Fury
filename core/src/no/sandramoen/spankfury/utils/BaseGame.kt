package no.sandramoen.spankfury.utils

import com.badlogic.gdx.*
import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.assets.AssetErrorListener
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture.TextureFilter
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.NinePatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable

abstract class BaseGame(var googlePlayServices: GooglePlayServices?) : Game(), AssetErrorListener {
    private val token = "BaseGame.kt"

    init {
        game = this
    }

    companion object {
        private var game: BaseGame? = null

        lateinit var assetManager: AssetManager
        lateinit var fontGenerator: FreeTypeFontGenerator
        const val WORLD_WIDTH = 100f
        const val WORLD_HEIGHT = 100f
        const val scale = 1.5f

        // game assets
        var gps: GooglePlayServices? = null
        var labelStyle: LabelStyle? = null
        var textButtonStyle: TextButtonStyle? = null
        var textureAtlas: TextureAtlas? = null
        var skin: Skin? = null
        var defaultShader: String? = null
        var shockwaveShader: String? = null
        var levelMusic1: Music? = null
        var levelMusic2: Music? = null
        var levelMusic3: Music? = null
        var hit1Sound: Sound? = null
        var heartInitSound: Sound? = null
        var heartLooseSound: Sound? = null
        var whipCrackSound: Sound? = null
        var floggerSound: Sound? = null
        var caneSound: Sound? = null
        var swooshSound: Sound? = null
        var paddleSound: Sound? = null
        var titlePowerUpSound: Sound? = null
        var clickSound: Sound? = null
        var gameOverSound: Sound? = null
        var newHighScoreSound: Sound? = null
        var vibrations: Boolean = false
        var green = Color(0.113f, 0.968f, 0.282f, 1f)
        var yellow = Color(0.968f, 0.815f, 0.113f, 1f)
        var red = Color(0.968f, 0.113f, 0.113f, 1f)

        // game state
        var prefs: Preferences? = null
        var loadPersonalParameters = false
        var highScore: Int = 0
        var mysteryKinksterScore: Int = 250_000
        var soundVolume = .75f
        var musicVolume = .125f
        var tempo = 1f
        var backOffFrequency = 2f
        var disableGPS = false

        fun setActiveScreen(s: BaseScreen) {
            game?.setScreen(s)
        }
    }

    override fun create() {
        Gdx.input.inputProcessor = InputMultiplexer() // discrete input

        // global variables
        gps = this.googlePlayServices
        GameUtils.loadGameState()
        if (!loadPersonalParameters) {
            soundVolume = .75f
            musicVolume = .25f
            vibrations = true
        }

        // asset manager
        assetManager = AssetManager()
        assetManager.setErrorListener(this)
        assetManager.load("images/included/packed/spankFury.pack.atlas", TextureAtlas::class.java)
        assetManager.load("audio/music/AlexBeroza_-_Drive.mp3", Music::class.java)
        assetManager.load("audio/music/bensound-extremeaction.mp3", Music::class.java)
        assetManager.load("audio/music/bensound-moose.mp3", Music::class.java)
        assetManager.load("audio/sound/hit.wav", Sound::class.java)
        assetManager.load("audio/sound/heartInit.wav", Sound::class.java)
        assetManager.load("audio/sound/heartLoose.wav", Sound::class.java)
        assetManager.load("audio/sound/gameOver.wav", Sound::class.java)
        assetManager.load("audio/sound/newHighScore.wav", Sound::class.java)
        assetManager.load("audio/sound/flogger0.wav", Sound::class.java)
        assetManager.load("audio/sound/cane0.wav", Sound::class.java)
        assetManager.load("audio/sound/paddle0.wav", Sound::class.java)
        assetManager.load("audio/sound/swoosh0.wav", Sound::class.java)
        assetManager.load("audio/sound/93100__cgeffex__whip-crack-01.wav", Sound::class.java)
        assetManager.load("audio/sound/Powerup5.bfxrsound.wav", Sound::class.java)
        assetManager.load("audio/sound/click1.wav", Sound::class.java)

        // assetManager.load("skins/default/uiskin.json", Skin::class.java)

        val resolver = InternalFileHandleResolver()
        assetManager.setLoader(FreeTypeFontGenerator::class.java, FreeTypeFontGeneratorLoader(resolver))
        assetManager.setLoader(BitmapFont::class.java, ".ttf", FreetypeFontLoader(resolver))
        assetManager.setLoader(Text::class.java, TextLoader(InternalFileHandleResolver()))

        assetManager.load(AssetDescriptor("shaders/default.vs", Text::class.java, TextLoader.TextParameter()))
        assetManager.load(AssetDescriptor("shaders/shockwave.fs", Text::class.java, TextLoader.TextParameter()))
        assetManager.finishLoading()

        textureAtlas =
            assetManager.get("images/included/packed/spankFury.pack.atlas") // all images are found in this global static variable

        // audio
        levelMusic1 = assetManager.get("audio/music/AlexBeroza_-_Drive.mp3", Music::class.java)
        levelMusic2 = assetManager.get("audio/music/bensound-extremeaction.mp3", Music::class.java)
        levelMusic3 = assetManager.get("audio/music/bensound-moose.mp3", Music::class.java)
        hit1Sound = assetManager.get("audio/sound/hit.wav", Sound::class.java)
        heartInitSound = assetManager.get("audio/sound/heartInit.wav", Sound::class.java)
        heartLooseSound = assetManager.get("audio/sound/heartLoose.wav", Sound::class.java)
        gameOverSound = assetManager.get("audio/sound/gameOver.wav", Sound::class.java)
        newHighScoreSound = assetManager.get("audio/sound/newHighScore.wav", Sound::class.java)
        floggerSound = assetManager.get("audio/sound/flogger0.wav", Sound::class.java)
        caneSound = assetManager.get("audio/sound/cane0.wav", Sound::class.java)
        paddleSound = assetManager.get("audio/sound/paddle0.wav", Sound::class.java)
        swooshSound = assetManager.get("audio/sound/swoosh0.wav", Sound::class.java)
        whipCrackSound = assetManager.get("audio/sound/93100__cgeffex__whip-crack-01.wav", Sound::class.java)
        titlePowerUpSound = assetManager.get("audio/sound/Powerup5.bfxrsound.wav", Sound::class.java)
        clickSound = assetManager.get("audio/sound/click1.wav", Sound::class.java)

        // text files
        defaultShader = assetManager.get("shaders/default.vs", Text::class.java).getString()
        shockwaveShader = assetManager.get("shaders/shockwave.fs", Text::class.java).getString()

        // skins
        skin = Skin(Gdx.files.internal("skins/default/uiskin.json"))

        // fonts
        FreeTypeFontGenerator.setMaxTextureSize(2048) // solves font bug that won't show some characters like "." and "," in android
        val fontGenerator = FreeTypeFontGenerator(Gdx.files.internal("fonts/arcade.ttf"))
        val fontParameters = FreeTypeFontParameter()
        fontParameters.size = (.038f * Gdx.graphics.height).toInt() // Font size is based on width of screen...
        fontParameters.color = Color.WHITE
        fontParameters.borderWidth = 2f
        fontParameters.borderColor = Color.BLACK
        fontParameters.borderStraight = true
        fontParameters.minFilter = TextureFilter.Linear
        fontParameters.magFilter = TextureFilter.Linear
        val customFont = fontGenerator.generateFont(fontParameters)

        val buttonFontParameters = FreeTypeFontParameter()
        buttonFontParameters.size =
            (.04f * Gdx.graphics.height).toInt() // If the resolutions height is 1440 then the font size becomes 86
        buttonFontParameters.color = Color.WHITE
        buttonFontParameters.borderWidth = 2f
        buttonFontParameters.borderColor = Color.BLACK
        buttonFontParameters.borderStraight = true
        buttonFontParameters.minFilter = TextureFilter.Linear
        buttonFontParameters.magFilter = TextureFilter.Linear
        val buttonCustomFont = fontGenerator.generateFont(buttonFontParameters)

        labelStyle = LabelStyle()
        labelStyle!!.font = customFont

        textButtonStyle = TextButtonStyle()
        val buttonTexUp = textureAtlas!!.findRegion("button")
        val buttonTexDown = textureAtlas!!.findRegion("button-pressed")
        val buttonPatchUp = NinePatch(buttonTexUp, 24, 24, 24, 24)
        val buttonPatchDown = NinePatch(buttonTexDown, 24, 24, 24, 24)
        textButtonStyle!!.up = NinePatchDrawable(buttonPatchUp)
        textButtonStyle!!.down = NinePatchDrawable(buttonPatchDown)
        textButtonStyle!!.font = buttonCustomFont
        textButtonStyle!!.fontColor = Color.WHITE
    }

    override fun dispose() {
        super.dispose()

        assetManager.dispose()
        fontGenerator.dispose()
        /*try { // TODO: uncomment this when development is done
            assetManager.dispose()
            fontGenerator.dispose()
        } catch (error: UninitializedPropertyAccessException) {
            Gdx.app.error("BaseGame", "Error $error")
        }*/
    }

    override fun error(asset: AssetDescriptor<*>, throwable: Throwable) {
        Gdx.app.error("BaseGame.kt", "Could not load asset: " + asset.fileName, throwable)
    }
}
