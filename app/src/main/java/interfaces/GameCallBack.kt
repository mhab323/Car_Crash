package interfaces

import data.cargame.TileType

interface GameCallBack {
    fun onBoardUpdated(board: Array<Array<TileType>>)
    fun onCarPositionChanged(col: Int)
    fun onLivesUpdated(lives: Int)
    fun onGameOver()
    fun onCollision(livesLeft: Int)

    fun onDistanceUpdated(distance: Double)

    fun onCoinPickedUp()
}