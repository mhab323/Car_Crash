package ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import utilities.BackgroundMusicPlayer
import com.example.cargame.R
import com.example.cargame.databinding.ActivityMenuBinding

class MenuActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMenuBinding


    companion object {
        const val KEY_PLAYER_NAME = "playerName"
        const val KEY_MODE = "mode"

        const val KEY_FAST_MODE = "fastMode"

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setUpMenuButtons()



    }

    private fun setUpMenuButtons() {
        binding.btnSensor.setOnClickListener {
            startGame("SENSORS")
        }
        binding.btnArrows.setOnClickListener {
            startGame("ARROWS")
        }
        binding.btnLeader.setOnClickListener {
            val intent = Intent(this, LeaderBoardActivity::class.java)
            startActivity(intent)
        }
    }

    private fun startGame(mode : String) {
        val playerName = binding.nameInput.text.toString()

        if (playerName.isEmpty()) {
            binding.nameInput.error = "Please enter your name"
            return
        }

        val intent = Intent(this, GameActivity::class.java)

        val isFastMode = binding.switchFastMode.isChecked
        val bundle = Bundle()
        if (isFastMode) {
            bundle.putBoolean(KEY_FAST_MODE, true)
        }

        bundle.putString(KEY_PLAYER_NAME, playerName)
        bundle.putString(KEY_MODE, mode)

        intent.putExtras(bundle)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        BackgroundMusicPlayer.play(this, R.raw.menu_music)    }

}