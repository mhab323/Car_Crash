package logic

import android.os.Handler
import android.os.Looper
import interfaces.GameCallBack
import data.cargame.TileType
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

class GameManager(
    private val rows: Int,
    private val cols: Int,
    private val callBack: GameCallBack,
)
{
    companion object {
        private const val INITIAL_LIVES = 3
        private const val BASE_TICK_DELAY_MS = 1000L
        private const val MIN_TICK_DELAY_MS = 500L
        private const val SPEEDUP_EVERY_TICKS = 5
        private const val SPEEDUP_STEP_MS = 100L

        private const val DISTANCE_INCREMENT = 0.1
    }
    private val board: Array<Array<TileType>> = Array(rows) {
        Array(cols) { TileType.EMPTY }
    }
    private  var carCol:Int = cols/2
    private var lives: Int = INITIAL_LIVES
    private var flag = true
    private val handler = Handler(Looper.getMainLooper())
    private var isRunning = false
    private var currentDelay = BASE_TICK_DELAY_MS
    private var ticks = 0

    private var distance: Double = 0.0


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
                board[r][c] = TileType.EMPTY
            }
        }
         lives = INITIAL_LIVES
         carCol = cols/2
         flag = true
         currentDelay = BASE_TICK_DELAY_MS
         ticks = 0
         generateInitialRows()

         callBack.onBoardUpdated(copyBoard())
         callBack.onCarPositionChanged(carCol)
         callBack.onLivesUpdated(lives)
    }



     fun generateInitialRows() {
        board[0] = generateRow()

        for(r in 1 until rows){
            for(c in 0 until cols)
                board[r][c] = TileType.EMPTY
        }
    }


    private fun generateRow(): Array<TileType> {
        val row = Array(cols) { TileType.EMPTY }
        val maxItems = min(3, cols)
        val itemsToPlace = Random.Default.nextInt(1,maxItems+1)
        var placed = 0
        var coinInThisRow = false

        while(placed < itemsToPlace){
            val col = Random.Default.nextInt(cols)
            if(row[col] == TileType.EMPTY){
                if(!coinInThisRow && Random.Default.nextInt(10) < 2){
                    row[col] = TileType.COIN
                    coinInThisRow = true
            }
                else{
                    row[col] = TileType.ROCK
                    placed++
                }
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
            Array(cols) { TileType.EMPTY }
        }
        flag = !flag

        board[0] = newRow

        if(ticks % 5 == 0 && ticks != 0){
            distance += DISTANCE_INCREMENT
            callBack.onDistanceUpdated(distance)
        }

        callBack.onBoardUpdated(copyBoard())    }

    private fun copyBoard(): Array<Array<TileType>> = Array(rows) { r ->
        board[r].clone()
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
        callBack.onGameOver()
    }


    fun getDistance(): Double{
        return distance
    }


    private fun checkCollision() {
        val lastRow = rows - 1
        val hitType = board[lastRow][carCol]

        when(hitType){
            TileType.ROCK -> {
                lives--
                callBack.onCollision(lives)
                callBack.onLivesUpdated(lives)
                if (lives <= 0) endGame()
                board[lastRow][carCol] = TileType.EMPTY
            }

            TileType.COIN -> {
                distance += 0.3
                callBack.onDistanceUpdated(distance)
                callBack.onCoinPickedUp()
                //TODO COIN sound
                board[lastRow][carCol] = TileType.EMPTY
            }
            else -> {}
        }
    }

    fun moveCarLeft() {
        if (carCol > 0) {
            carCol--
            callBack.onCarPositionChanged(carCol)
        }
    }

    fun moveCarRight() {
        if (carCol < cols - 1) {
            carCol++
            callBack.onCarPositionChanged(carCol)
        }
    }

    fun setInitialDelay(delay: Long) {
        this.currentDelay = delay
    }

}