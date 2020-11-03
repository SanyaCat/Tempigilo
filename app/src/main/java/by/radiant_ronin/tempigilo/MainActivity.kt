package by.radiant_ronin.tempigilo

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.media.MediaPlayer
import android.os.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    companion object {
        lateinit var players: ArrayList<Player>
        lateinit var context: Context
        lateinit var reservedColors: MutableSet<Int>
        val colors = listOf(
            Color.RED,
            Color.BLUE,
            Color.GREEN,
            Color.YELLOW,
            Color.CYAN,
            Color.MAGENTA
        )
        var startTime = 0L
    }

    private var countDownTimer: CountDownTimer? = null
    private var isRunning = false
    var currentPlayer = 0
    var losers = mutableSetOf<Int>()
    private lateinit var mediaPlayer: MediaPlayer

    fun playSound() {
        mediaPlayer.start()
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= 26) {
            vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(500)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        context = this
        mediaPlayer = MediaPlayer.create(this, R.raw.beep_short)

        btn_next_player.setOnClickListener {
            findNext()

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
            } else {
                startTimer(players[currentPlayer].time)
            }
        }
    }

    override fun onResume() {
        super.onResume()

        losers = mutableSetOf()
        btn_next_player.isEnabled = true
        btn_pause.isEnabled = true
        for (i in players)
            i.time = startTime

        updateTime()
        updateColor()
    }

    override fun onPause() {
        super.onPause()
        if (countDownTimer != null)
            pauseTimer()
    }

    private fun findNext() {
        do
            if (currentPlayer < players.size - 1)
                currentPlayer++
            else
                currentPlayer = 0
        while (losers.contains(currentPlayer))
    }

    private fun pauseTimer() {
        countDownTimer!!.cancel()
        isRunning = false
        btn_pause.setImageResource(R.drawable.ic_start)
    }

    private fun startTimer(timeSeconds: Long) {
        countDownTimer = object : CountDownTimer(timeSeconds, 10) {
            override fun onTick(millisUntilFinished: Long) {
                players[currentPlayer].time = millisUntilFinished
                updateTime()
            }

            @SuppressLint("SetTextI18n")
            override fun onFinish() {
                if (losers.size + 2 < players.size) {
                    playSound()
                    Toast.makeText(
                        this@MainActivity,
                        "${players[currentPlayer].name} ${getString(R.string.loses)}",
                        Toast.LENGTH_SHORT
                    ).show()
                    losers.add(currentPlayer)
                    txtv_time.text = "00:00"
                    currentPlayer--
                } else {
                    findNext()
                    playSound()
                    Toast.makeText(
                        this@MainActivity,
                        "${players[currentPlayer].name} ${getString(R.string.wins)}",
                        Toast.LENGTH_LONG
                    ).show()
                    updateColor()
                    updateTime()
                    btn_next_player.isEnabled = false
                    btn_pause.isEnabled = false
                }
            }
        }

        countDownTimer!!.start()
        isRunning = true
        btn_pause.setImageResource(R.drawable.ic_pause)
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