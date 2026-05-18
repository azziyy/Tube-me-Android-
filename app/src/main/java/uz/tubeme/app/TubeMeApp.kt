package uz.tubeme.app

import android.app.Application

class TubeMeApp : Application() {
    companion object {
        lateinit var instance: TubeMeApp
            private set
    }
    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}
