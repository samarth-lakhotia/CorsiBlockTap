package com.example.corsiblocktappingapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.widget.TextView

class CountDownTimerActivity : Activity() {

    private lateinit var mCountDownTimer:TextView
    private lateinit var mTimer:CrossCountDownTimer

    // This is the count down activity after user click play button in the main page
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_countdown)

        mCountDownTimer = findViewById(R.id.textView_countdown) as TextView

        mTimer = CrossCountDownTimer(5000, 1000)
        mTimer.start()

        //delay for 5 seconds to start the game
        Handler().postDelayed({
//            this.setResult(RESULT_OK,Intent(this, MainActivity::class.java))
//            this.finish()
            startActivity(Intent(baseContext, GameActivity::class.java))
        }, 5000)

    }


    //The inner count down class for the count down timer (mTimer)
    inner class CrossCountDownTimer(millisInFuture: Long, countDownInterval: Long) : CountDownTimer(millisInFuture, countDownInterval) {

        override fun onFinish() {

        }

        override fun onTick(millisUntilFinished: Long) {
            mCountDownTimer.textSize = 60f

            //Show the word "Start!" at the last second
            if (millisUntilFinished / 1000 != 0L)
                mCountDownTimer.text = (millisUntilFinished / 1000).toString() + ""
            else mCountDownTimer.text = "Start!"
        }
    }
}