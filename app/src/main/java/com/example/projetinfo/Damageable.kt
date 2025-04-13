package com.example.projetinfo

interface Damageable {
    var hp: Int
    /**
     * Applique des dégâts à l'objet.
     * @param damage le nombre de points de dégâts à infliger.
     * @return true si l'objet est détruit (hp <= 0), false sinon.
     */
    fun applyDamage(damage: Int): Boolean
}
