package com.example.corsiblocktappingapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.widget.TextView

class CountDownTimerActivity : Activity() {

    private lateinit var mCountDownTimer:TextView
    private lateinit var timer:MyCounter
    private val REQUEST_CODE_1 = 101
    private val REQUEST_CODE_2 = 102

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_countdown)

        mCountDownTimer = findViewById(R.id.textView_countdown) as TextView

        timer = MyCounter(5000, 1000)
        timer.start()
        Handler().postDelayed({
            this.setResult(RESULT_OK,Intent(this, MainActivity::class.java))
            this.finish()
        }, 5000)

    }



    inner class MyCounter(millisInFuture: Long, countDownInterval: Long) : CountDownTimer(millisInFuture, countDownInterval) {

        override fun onFinish() {

        }

        override fun onTick(millisUntilFinished: Long) {
            mCountDownTimer.textSize = 60f
            if (millisUntilFinished / 1000 != 0L)
                mCountDownTimer.text = (millisUntilFinished / 1000).toString() + ""
            else mCountDownTimer.text = "Start!"
        }
    }
}