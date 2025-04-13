package com.example.projetinfo

object ScoreManager {
    private val observers = mutableListOf<ScoreObserver>()
    var totalScore: Int = 0
        private set

    fun addScore(points: Int) {
        totalScore += points
        notifyObservers()
    }

    fun reset() {
        totalScore = 0
        notifyObservers()
    }

    fun addObserver(observer: ScoreObserver) {
        if (!observers.contains(observer)) {
            observers.add(observer)
        }
    }

    fun removeObserver(observer: ScoreObserver) {
        observers.remove(observer)
    }

    private fun notifyObservers() {
        for (observer in observers) {
            observer.onScoreChanged(totalScore)
        }
    }
}
