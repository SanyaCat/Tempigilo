package by.radiant_ronin.tempigilo

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    companion object {
        lateinit var players: ArrayList<Player>
        lateinit var context: Context
        lateinit var reservedColors: MutableSet<Int>
        val colors = listOf(Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, Color.CYAN, Color.MAGENTA)
        var startTime = 10000L
    }

    lateinit var countDownTimer: CountDownTimer
    var isRunning = false
    var currentPlayer = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        context = this

        btn_next_player.setOnClickListener {
            if (currentPlayer < players.size - 1)
                currentPlayer++
            else
                currentPlayer = 0

            if (isRunning) {
                pauseTimer()
                startTimer(players[currentPlayer].time)
            }

            updateTime()
            updateColor()
        }

        btn_pause.setOnClickListener {
            if (isRunning) {
                pauseTimer()
                // Toast.makeText(this, "paused", Toast.LENGTH_SHORT).show()
            } else {
                //val time = txtvTime.text.toString()
                //timeMillis = 30000L //one minute
                startTimer(players[currentPlayer].time)
            }
        }
    }

    override fun onResume() {
        super.onResume()

        for (i in players)
            i.time = startTime

        updateTime()
        updateColor()
    }

    override fun onPause() {
        super.onPause()

        pauseTimer()
    }

    private fun pauseTimer() {
        countDownTimer.cancel()
        isRunning = false
        btn_pause.setImageResource(android.R.drawable.ic_media_play)
    }

    private fun startTimer(timeSeconds: Long) {
        countDownTimer = object : CountDownTimer(timeSeconds, 10) {
            override fun onTick(millisUntilFinished: Long) {
                players[currentPlayer].time = millisUntilFinished
                updateTime()
            }

            @SuppressLint("SetTextI18n")
            override fun onFinish() {
                // TODO: FINISH
                Toast.makeText(this@MainActivity, "Player ${players[currentPlayer].name} loses!", Toast.LENGTH_SHORT).show()
                txtv_time.text = "00:00"
            }
        }

        countDownTimer.start()
        isRunning = true
        btn_pause.setImageResource(android.R.drawable.ic_media_pause)
    }

    @SuppressLint("SetTextI18n")
    private fun updateTime() {
        var seconds = ((players[currentPlayer].time / 1000) % 60).toString()
        var millis = ((players[currentPlayer].time / 10) % 100).toString()
        if (seconds.length < 2)
            seconds = "0$seconds"
        if (millis.length < 2)
            millis = "0$millis"

        txtv_time.text = "$seconds:$millis"
    }

    private fun updateColor() {
        background.setBackgroundColor(players[currentPlayer].color)
    }
}