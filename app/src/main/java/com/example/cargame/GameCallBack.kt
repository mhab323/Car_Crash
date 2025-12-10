package com.example.cargame

interface GameCallBack {
    fun onBoardUpdated(board: Array<Array<TileType>>)
    fun onCarPositionChanged(col: Int)
    fun onLivesUpdated(lives: Int)
    fun onGameOver()
    fun onCollision(livesLeft: Int)
}