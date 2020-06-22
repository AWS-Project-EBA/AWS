package com.example.rapunzel

class Singleton {
    var name:String=""
    var email:String=""
    var id:Int=-1

    companion object {
        private var INSTANCE: Singleton? = null

        val instance: Singleton
            get() {
                if (INSTANCE == null) {
                    INSTANCE = Singleton()
                }

                return INSTANCE!!
            }
    }
}