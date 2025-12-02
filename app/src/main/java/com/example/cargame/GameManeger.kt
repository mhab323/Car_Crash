package com.example.cargame

import android.os.Handler
import android.os.Looper
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

class GameManeger(
    private val rows: Int,
    private val cols: Int,
    private val boardUpdated: (Array<IntArray>) -> Unit,
    private val carPositionChanged: (Int) -> Unit,
    private val livesUpdate: (Int) -> Unit,
    private val gameOver: () -> Unit,
    private val collision: (Int) -> Unit
)
{
    private val INITIAL_LIVES = 3
    private  val BASE_TICK_DELAY_MS = 1000L
    private  val MIN_TICK_DELAY_MS = 500L
    private  val SPEEDUP_EVERY_TICKS = 5
    private  val SPEEDUP_STEP_MS = 100L

    private val board: Array<IntArray> = Array(rows){ IntArray(cols) { 0 } }

    private  var carCol:Int = cols/2

    private var lives: Int = INITIAL_LIVES

    private var flag = true

    private val handler = Handler(Looper.getMainLooper())

    private var isRunning = false
    private var currentDelay = BASE_TICK_DELAY_MS
    private var ticks = 0

    private val tick = object : Runnable{
        override fun run() {
            if(!isRunning) return
            step()
            ticks++
            adjustSpeed()
            handler.postDelayed(this,currentDelay)
        }


    }

    init {
        resetGame()
    }

     fun resetGame() {
        for(r in 0 until rows){
            for(c in 0 until cols){
                board[r][c] = 0
            }
        }
         lives = INITIAL_LIVES
         carCol = cols/2

         flag = true
         generateInitialRows()

         boardUpdated(copyBoard())
         carPositionChanged(carCol)
         livesUpdate(lives)
    }

     fun generateInitialRows() {
         board[0] = generateRow()

        for(r in 1 until rows){
            for(c in 0 until cols)
            board[r][c] = 0
        }
    }


    private fun generateRow(): IntArray {
        val row = IntArray(cols) { 0 }
        val maxRocks = min(2,cols)
        val rocksToPlace = Random.nextInt(1,maxRocks+1)
        var placed = 0
        
        while(placed < rocksToPlace){
            val col = Random.nextInt(cols)
            if(row[col] == 0){
                row[col] = 1
                placed++
            }
        }
        return row
    }

    private fun step() {
        checkCollision()

        for(r in rows -1 downTo 1){
            board[r]= board[r-1].clone()
        }

        val newRow = if(flag){
            generateRow()
        }
        else
        {
            IntArray(cols)  { 0 }
        }
        flag = !flag

        board[0] = newRow

        boardUpdated(copyBoard())
    }

    private fun copyBoard(): Array<IntArray> = Array(rows) {
        r -> board[r].clone()
    }

    private fun adjustSpeed() {
        if (ticks % SPEEDUP_EVERY_TICKS == 0 && currentDelay > MIN_TICK_DELAY_MS) {
            currentDelay = max(MIN_TICK_DELAY_MS, currentDelay - SPEEDUP_STEP_MS)
        }
    }

    fun startGame(){
        if(isRunning)return
        isRunning = true
        handler.postDelayed(tick,currentDelay)
    }

    fun pauseGame(){
        if(!isRunning)return
        isRunning = false
        handler.removeCallbacks(tick)
    }

    fun endGame(){
        isRunning = false
        handler.removeCallbacks(tick)
        gameOver()
    }

    private fun checkCollision() {
        val lastRow = rows - 1
        if(board[lastRow][carCol] == 1) {
            lives--
            collision(lives)
            livesUpdate(lives)

            if(lives <= 0) {
                endGame()
            }
        }
    }

    fun moveCarLeft() {
        if (carCol > 0) {
            carCol--
            carPositionChanged(carCol)
        }
    }

    fun moveCarRight() {
        if (carCol < cols - 1) {
            carCol++
            carPositionChanged(carCol)
        }
    }

}