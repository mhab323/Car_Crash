package ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import utilities.HighScoreManager
import com.example.cargame.R
import data.cargame.Record
import utilities.SoundEffectPlayer
import com.example.cargame.databinding.ActivityGameOverBinding

class GameOverActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGameOverBinding
    private lateinit var highScoreManager: HighScoreManager
    private var playerName: String = "Guest"
    private var gameMode: String = "ARROWS"
    private var isFast: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityGameOverBinding.inflate(layoutInflater)
        setContentView(binding.root)

        highScoreManager = HighScoreManager(this)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initGameOver()
        setupClickListeners()
    }

    private fun initGameOver() {
        SoundEffectPlayer.play(R.raw.game_over)

        val bundle = intent.extras
        if (bundle != null) {
            playerName = bundle.getString(MenuActivity.KEY_PLAYER_NAME, "Guest")
            gameMode = bundle.getString(MenuActivity.KEY_MODE, "ARROWS")
            isFast = bundle.getBoolean(MenuActivity.KEY_FAST_MODE, false)

            val score = bundle.getDouble(GameActivity.KEY_SCORE, 0.0)
            val lat = bundle.getDouble(GameActivity.KEY_LAT, 0.0)
            val lon = bundle.getDouble(GameActivity.KEY_LON, 0.0)

            updateUI(playerName, score)
            saveNewRecord(playerName, score, lat, lon, gameMode)
        }
    }

    private fun updateUI(name: String, score: Double) {
        binding.scoreLBLName.text = "Player $name"
        binding.scoreLBLScore.text = "Distance %.1f".format(score)
    }

    private fun saveNewRecord(name: String, score: Double, lat: Double, lon: Double, gameMode: String) {
        highScoreManager.addScore(Record(name, score, lat, lon, gameMode,isFast))
    }

    private fun setupClickListeners() {
        binding.scoreBTNAgain.setOnClickListener {
            restartGame()
        }

        binding.scoreBTNTop.setOnClickListener {
            openLeaderboard()
        }
    }

    private fun restartGame() {
        SoundEffectPlayer.stopAll()

        val intent = Intent(this, GameActivity::class.java)
        val bundle = Bundle()

        bundle.putString(MenuActivity.KEY_PLAYER_NAME, playerName)
        bundle.putString(MenuActivity.KEY_MODE, gameMode)
        bundle.putBoolean(MenuActivity.KEY_FAST_MODE, isFast)

        intent.putExtras(bundle)
        startActivity(intent)
        finish()
    }

    private fun openLeaderboard() {
        val intent = Intent(this, LeaderBoardActivity::class.java)
        startActivity(intent)
        finish()
    }
}