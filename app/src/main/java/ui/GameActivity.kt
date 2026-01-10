package ui

import android.R
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import utilities.BackgroundMusicPlayer
import interfaces.GameCallBack
import logic.GameManager
import utilities.LocationHelper
import utilities.SoundEffectPlayer
import data.cargame.TileType
import utilities.TiltDetector
import utilities.VibrationManager
import com.example.cargame.databinding.ActivityGameBinding

class GameActivity : AppCompatActivity(), GameCallBack {

    private lateinit var binding: ActivityGameBinding

    private lateinit var gm: GameManager
    private lateinit var vibrationManager: VibrationManager
    private lateinit var tiltDetector: TiltDetector
    private lateinit var locationHelper: LocationHelper
    private var myToast: Toast? = null

    private var isFastMode: Boolean = false
    private var playerName: String = "Guest"
    private var mode: String = "ARROWS"
    private var currentCarIndex: Int = 2
    private var lat: Double = 32.0853
    private var lon: Double = 34.7818

    private lateinit var rockViews: Array<Array<ImageView>>
    private lateinit var carViews: Array<View>
    private lateinit var heartViews: Array<View>

    companion object {
        const val KEY_SCORE = "finalScore"
        const val KEY_LAT = "lat"
        const val KEY_LON = "lon"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SoundEffectPlayer.stopAll()

        initWindowConfig()
        initBindings()
        initManager()
        loadData()
        setupViews()
        setupGameManager()
        setupInputMode(mode)
    }

    private fun initManager() {
        vibrationManager = VibrationManager(this)
        locationHelper = LocationHelper(this) { latitude, longitude ->
            lat = latitude
            lon = longitude
        }
        locationHelper.checkAndRequestLocation()
    }

    private fun initBindings() {
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    @SuppressLint("SourceLockedOrientationActivity")
    private fun initWindowConfig() {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.content)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun loadData() {
        val bundle = intent.extras
        if (bundle != null) {
            playerName = bundle.getString(MenuActivity.KEY_PLAYER_NAME, "Guest")
            mode = bundle.getString(MenuActivity.KEY_MODE, "ARROWS")
            isFastMode = bundle.getBoolean(MenuActivity.KEY_FAST_MODE, false)
        }
    }

    private fun setupGameManager() {
        gm = GameManager(7, 5, this)
        gm.setInitialDelay(if (isFastMode) 500L else 1000L)
    }

    private fun setupInputMode(mode: String) {
        if (mode == "SENSORS") {
            binding.leftButton.visibility = View.GONE
            binding.rightButton.visibility = View.GONE

            tiltDetector = TiltDetector(this) { targetLane ->
                if (currentCarIndex > targetLane) {
                    gm.moveCarLeft()
                } else if (currentCarIndex < targetLane) {
                    gm.moveCarRight()
                }
            }
            tiltDetector.start()
        } else {
            binding.leftButton.visibility = View.VISIBLE
            binding.rightButton.visibility = View.VISIBLE
            binding.leftButton.setOnClickListener { gm.moveCarLeft() }
            binding.rightButton.setOnClickListener { gm.moveCarRight() }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        locationHelper.onRequestPermissionsResult(requestCode, grantResults)
    }

    override fun onResume() {
        super.onResume()
        gm.startGame()
        BackgroundMusicPlayer.play(this, com.example.cargame.R.raw.game_music)
        SoundEffectPlayer.resumeAll()
    }

    override fun onPause() {
        super.onPause()
        gm.pauseGame()
        SoundEffectPlayer.pauseAll()
    }

    override fun onStop() {
        super.onStop()
        myToast?.cancel()
        if (::tiltDetector.isInitialized) {
            tiltDetector.stop()
        }
        gm.pauseGame()
    }

    override fun onDestroy() {
        super.onDestroy()
        BackgroundMusicPlayer.stop()
    }

    override fun onCarPositionChanged(col: Int) {
        currentCarIndex = col
        updateCar(col)
    }

    override fun onCollision(livesLeft: Int) {
        showToast("Crash! Lives: $livesLeft")
        vibrationManager.vibrate()
        SoundEffectPlayer.play(com.example.cargame.R.raw.crash, 1.0f)
    }

    override fun onGameOver() {
        myToast?.cancel()
        BackgroundMusicPlayer.stop()
        goToGameOver(gm.getDistance())
    }
    private fun goToGameOver(finalScore: Double) {
        val intent = Intent(this, GameOverActivity::class.java)
        val bundle = Bundle()

        bundle.putString(MenuActivity.KEY_PLAYER_NAME, playerName)
        bundle.putDouble(KEY_SCORE, finalScore)
        bundle.putDouble(KEY_LAT, lat)
        bundle.putDouble(KEY_LON, lon)
        bundle.putString(MenuActivity.KEY_MODE, mode)
        bundle.putBoolean(MenuActivity.KEY_FAST_MODE, isFastMode)

        intent.putExtras(bundle)
        startActivity(intent)
        finish()
    }

    override fun onBoardUpdated(board: Array<Array<TileType>>) = updateBoard(board)
    override fun onLivesUpdated(lives: Int) = updateHearts(lives)

    @SuppressLint("DefaultLocale")
    override fun onDistanceUpdated(distance: Double) { binding.distance.text = String.format("%.1f", distance) }
    override fun onCoinPickedUp() {
        SoundEffectPlayer.play(com.example.cargame.R.raw.collectcoin)
    }

    private fun setupViews() {
        rockViews = arrayOf(
            arrayOf(binding.stone00, binding.stone01, binding.stone02, binding.stone03, binding.stone04),
            arrayOf(binding.stone10, binding.stone11, binding.stone12, binding.stone13, binding.stone14),
            arrayOf(binding.stone20, binding.stone21, binding.stone22, binding.stone23, binding.stone24),
            arrayOf(binding.stone30, binding.stone31, binding.stone32, binding.stone33, binding.stone34),
            arrayOf(binding.stone40, binding.stone41, binding.stone42, binding.stone43, binding.stone44),
            arrayOf(binding.stone50, binding.stone51, binding.stone52, binding.stone53, binding.stone54),
            arrayOf(binding.stone60, binding.stone61, binding.stone62, binding.stone63, binding.stone64)
        )
        carViews = arrayOf(binding.car0, binding.car1, binding.car2, binding.car3, binding.car4)
        heartViews = arrayOf(binding.imgHeart1Main, binding.imgHeart2Main, binding.imgHeart3Main)
    }

    private fun updateBoard(board: Array<Array<TileType>>) {
        for (r in board.indices) {
            for (c in board[r].indices) {
                val cellView = rockViews[r][c]
                when (board[r][c]) {
                    TileType.ROCK -> {
                        cellView.setImageResource(com.example.cargame.R.drawable.stone)
                        cellView.visibility = View.VISIBLE
                    }
                    TileType.COIN -> {
                        cellView.setImageResource(com.example.cargame.R.drawable.dollar)
                        cellView.visibility = View.VISIBLE
                    }
                    TileType.EMPTY -> cellView.visibility = View.INVISIBLE
                }
            }
        }
    }

    private fun updateCar(column: Int) {
        for (i in carViews.indices) {
            carViews[i].visibility = if (i == column) View.VISIBLE else View.INVISIBLE
        }
    }

    private fun updateHearts(lives: Int) {
        for (i in heartViews.indices) {
            heartViews[i].visibility = if (i < lives) View.VISIBLE else View.INVISIBLE
        }
    }

    private fun showToast(message: String) {
        myToast?.cancel()

        myToast = Toast.makeText(this, message, Toast.LENGTH_SHORT)
        myToast?.show()
    }
}
