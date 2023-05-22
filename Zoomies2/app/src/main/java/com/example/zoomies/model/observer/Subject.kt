package com.example.zoomies.model.observer

open class Subject {
    private var observers = mutableListOf<Observer>()

    fun callObservers() {
        for(obs in observers) obs.onLanguageChanged()
    }

    fun attach(obs : Observer) {
        observers.add(obs)
    }

    fun detach(obs : Observer) {
        observers.remove(obs)
    }
}