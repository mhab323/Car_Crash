package com.example.cargame

import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.cargame.databinding.ActivityMainBinding



@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity(),GameCallBack {


    private lateinit var binding: ActivityMainBinding

    private lateinit var gm: GameManeger
    private lateinit var rockViews: Array<Array<View>>
    private lateinit var carViews: Array<View>
    private lateinit var heartViews: Array<View>

    private lateinit var vibrator: Vibrator


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator

        setupViews()
        setupGameManager()
        setupButtons()



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupGameManager() {
        gm = GameManeger(
            rows = 6,
            cols = 3,
            callBack = this
        )
    }

    private fun setupButtons() {
        binding.leftButton.setOnClickListener {
            gm.moveCarLeft()
        }

        binding.rightButton.setOnClickListener {
            gm.moveCarRight()
        }
    }


    private fun setupViews() {
        rockViews = arrayOf(
            arrayOf(binding.stone00, binding.stone01, binding.stone02),
            arrayOf(binding.stone10, binding.stone11, binding.stone12),
            arrayOf(binding.stone20, binding.stone21, binding.stone22),
            arrayOf(binding.stone30, binding.stone31, binding.stone32),
            arrayOf(binding.stone40, binding.stone41, binding.stone42),
            arrayOf(binding.stone50, binding.stone51, binding.stone52)
        )

        carViews = arrayOf(
            binding.carLeft,
            binding.carCenter,
            binding.carRight
        )

        heartViews = arrayOf(
            binding.imgHeart1Main,
            binding.imgHeart2Main,
            binding.imgHeart3Main
        )
    }

    override fun onStop() {
        super.onStop()
        gm.pauseGame()
    }

    override fun onPause() {
        super.onPause()
        gm.pauseGame()
    }

    override fun onResume() {
        super.onResume()
        gm.startGame()
    }

    private fun updateRocks(board: Array<Array<TileType>>) {
        for (r in board.indices) {
            for (c in board[r].indices) {
                rockViews[r][c].visibility =
                    if (board[r][c] == TileType.ROCK) View.VISIBLE else View.INVISIBLE
            }
        }
    }

    private fun updateCar(column: Int) {
        for (i in carViews.indices) {
            carViews[i].visibility =
                if (i == column) View.VISIBLE else View.INVISIBLE
        }
    }

    private fun updateHearts(lives: Int) {
        for (i in heartViews.indices) {
            heartViews[i].visibility =
                if (i < lives) View.VISIBLE else View.INVISIBLE
        }
    }

    private fun showGameOverDialog() {
        AlertDialog.Builder(this)
            .setTitle("Game Over")
            .setMessage("You lost all your lives")
            .setCancelable(false)
            .setPositiveButton("Play again") { _, _ ->
                gm.resetGame()
                gm.startGame()
            }
            .setNegativeButton("Exit") { _, _ ->
                finish()
            }
            .show()
    }
    private fun vibratePhone() {
        vibrator.vibrate(
            VibrationEffect.createOneShot(500,
                VibrationEffect.DEFAULT_AMPLITUDE))
    }

    private fun showCollisionToast(livesLeft: Int) {
        Toast.makeText(
            this,
            "Crash! Lives left: $livesLeft",
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onBoardUpdated(board: Array<Array<TileType>>) {
        updateRocks(board)
    }

    override fun onCarPositionChanged(col: Int) {
        updateCar(col)
    }

    override fun onCollision(livesLeft: Int) {
        showCollisionToast(livesLeft)
        vibratePhone()
    }


    override fun onGameOver() {
        showGameOverDialog()
    }

    override fun onLivesUpdated(lives: Int) {
        updateHearts(lives)
    }
}



