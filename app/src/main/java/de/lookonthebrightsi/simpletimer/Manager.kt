package de.lookonthebrightsi.simpletimer

import androidx.lifecycle.MutableLiveData
import com.google.common.base.Stopwatch
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.concurrent.scheduleAtFixedRate
import kotlin.concurrent.timerTask
import kotlin.time.Duration.Companion.milliseconds

const val TAG = "SimpleTimer"

object Manager {
    val time = MutableLiveData(0L)
    private val timer = Timer()
    private val stopwatch = Stopwatch.createUnstarted()
    private val runnable: TimerTask.() -> Unit = {
        time.postValue(stopwatch.elapsed(TimeUnit.MILLISECONDS))
    }
    private var task = timerTask(runnable)
    var running = false

    fun resume() {
        if (running) return
        stopwatch.start()
        task = timer.scheduleAtFixedRate(1, 1, runnable)
        running = true
    }
    fun pause() {
        if (!running) return
        task.cancel()
        stopwatch.stop()
        running = false
    }
    fun reset() {
        if (running) pause()
        stopwatch.reset()
        time.postValue(0)
    }
}

private val Number.formatted get() = String.format("%02d", this)
private val Number.formatted3 get() = String.format("%03d", this)

fun Long.formatTime() = milliseconds.toComponents { hours, minutes, seconds, nanoseconds ->
    "${hours.formatted}:${minutes.formatted}:${seconds.formatted}:${(nanoseconds / 1000000).formatted3}"
}