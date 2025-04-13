package com.example.projetinfo

interface ScoreObserver {
    /**
     * Méthode appelée quand le score global est mis à jour.
     * @param newScore le nouveau score global.
     */
    fun onScoreChanged(newScore: Int)
}
