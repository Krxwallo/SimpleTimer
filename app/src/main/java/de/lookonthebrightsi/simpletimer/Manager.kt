package de.lookonthebrightsi.simpletimer

import android.os.SystemClock
import androidx.lifecycle.MutableLiveData
import java.util.*
import kotlin.concurrent.scheduleAtFixedRate
import kotlin.concurrent.timerTask
import kotlin.time.Duration.Companion.milliseconds

const val TAG = "SimpleTimer"

object Manager {
    val time = MutableLiveData(0L)
    private val timer = Timer()
    private var startTime = 0L
    private val runnable: TimerTask.() -> Unit = {
        time.postValue((millis() - startTime) + addition)
    }
    private var task = timerTask(runnable)
    private var addition = 0L
    private var pauseStartTime: Long? = null
    var running = false

    fun resume() {
        if (running) return
        startTime = millis()
        if (pauseStartTime != null) {
            addition += millis() - pauseStartTime!!
            println("addition = $addition")
        }
        task = timer.scheduleAtFixedRate(1, 1, runnable)
        running = true
    }
    fun pause() {
        if (running) return
        task.cancel()
        running = false
        pauseStartTime = millis()
    }
    fun reset() {
        if (running) pause()
        startTime = 0
        addition = 0
        pauseStartTime = null
        time.value = 0
    }
}

private fun millis() = SystemClock.elapsedRealtime()
private val Number.formatted get() = String.format("%02d", this)
private val Number.formatted3 get() = String.format("%03d", this)

fun Long.formatTime() = milliseconds.toComponents { hours, minutes, seconds, nanoseconds ->
    "${hours.formatted}:${minutes.formatted}:${seconds.formatted}:${(nanoseconds / 1000000).formatted3}"
}