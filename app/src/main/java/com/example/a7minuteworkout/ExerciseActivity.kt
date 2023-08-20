package com.example.a7minuteworkout

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.a7minuteworkout.databinding.ActivityExerciseBinding
import com.example.a7minuteworkout.databinding.DialogCustomBackConfirmationBinding
import java.util.*
import java.util.zip.Inflater
import kotlin.collections.ArrayList

class ExerciseActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var binding: ActivityExerciseBinding

    private var restTimer : CountDownTimer? = null
    private var restProgress = 0
    private var restProgressDuration : Long = 10
    //private var restProgressDuration : Long = 1 //test

    private var exerciseTimer : CountDownTimer? = null
    private var exerciseProgress = 0
    private var exerciseProgressDuration : Long = 30
    //private var exerciseProgressDuration : Long = 1 //test

    private var exerciseList: ArrayList<ExerciseModel>? = null
    private var currentExercisePosition = -1

    private var tts: TextToSpeech? = null
    private var player: MediaPlayer? = null

    private var exerciseAdapter: ExerciseStatusAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExerciseBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        tts = TextToSpeech(this, this)


        setSupportActionBar(binding.toolBarExerciseActivity)
        val actionBar = supportActionBar
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
        }
        binding.toolBarExerciseActivity.setNavigationOnClickListener {
            customDialogForBackButton()
        }

        exerciseList = Constants.defaultExerciseList()
        setupRestView()
        setupExerciseStatusRecyclerView()
    }

    override fun onDestroy() {
        if(restTimer != null){
            restTimer!!.cancel()
            restProgress = 0
        }
        if(exerciseTimer != null){
            exerciseTimer!!.cancel()
            exerciseProgress = 0
        }
        if(tts != null){
            tts!!.stop()
            tts!!.shutdown()
        }
        if(player != null){
            player!!.stop()
        }
        super.onDestroy()
    }

    private fun setRestProgressBar(){
        binding.restProgressBar.progress = restProgress
        restTimer = object : CountDownTimer(restProgressDuration * 1000, 1000){
            override fun onTick(millisUntilFinished: Long) {
                restProgress++
                binding.restProgressBar.progress = restProgressDuration.toInt()-restProgress
                binding.tvTimer.text = (restProgressDuration-restProgress).toString()
            }

            override fun onFinish() {
                currentExercisePosition++

                exerciseList!![currentExercisePosition].setIsSelected(true)
                exerciseAdapter!!.notifyDataSetChanged()

                setupExerciseView()
            }
        }.start()
    }
    private fun setupRestView(){
        binding.llExerciseView.visibility = View.GONE
        binding.llRestView.visibility = View.VISIBLE

        if(restTimer != null){
            restTimer!!.cancel()
            restProgress = 0
        }
        setRestProgressBar()
        try {
            if(currentExercisePosition >= 0){
                player = MediaPlayer.create(applicationContext, R.raw.press_start)
                player!!.isLooping = false
                player!!.start()
            }
        }catch (e: Exception){
            e.printStackTrace()
        }
        binding.tvNextExerciseName.text = exerciseList!![currentExercisePosition+1].getName()
    }

    private fun setExerciseProgressBar(){
        binding.exerciseProgressBar.progress = exerciseProgress
        exerciseTimer = object : CountDownTimer(exerciseProgressDuration * 1000, 1000){
            override fun onTick(millisUntilFinished: Long) {
                exerciseProgress++
                binding.exerciseProgressBar.progress = exerciseProgressDuration.toInt()-exerciseProgress
                binding.tvExerciseTimer.text = (exerciseProgressDuration-exerciseProgress).toString()
            }

            override fun onFinish() {
                if(currentExercisePosition < exerciseList!!.size - 1){
                //if(currentExercisePosition < 1){
                    exerciseList!![currentExercisePosition].setIsSelected(false)
                    exerciseList!![currentExercisePosition].setIsCompleted(true)
                    exerciseAdapter!!.notifyDataSetChanged()
                    setupRestView()
                }else{
                    finish()
                    val intent = Intent(this@ExerciseActivity, FinishActivity::class.java)
                    startActivity(intent)
                }
            }
        }.start()
    }
    private fun setupExerciseView(){
        binding.llRestView.visibility = View.GONE
        binding.llExerciseView.visibility = View.VISIBLE

        if(exerciseTimer != null){
            exerciseTimer!!.cancel()
            exerciseProgress = 0
        }


        speakOut(exerciseList!![currentExercisePosition].getName())

        setExerciseProgressBar()

        binding.ivImage.setImageResource(exerciseList!![currentExercisePosition].getImage())
        binding.tvExerciseName.text = exerciseList!![currentExercisePosition].getName()
    }

    override fun onInit(status: Int) {
        if(status == TextToSpeech.SUCCESS){
            val result = tts!!.setLanguage(Locale.US)
            if(result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED){
                Log.e("TTS", "The Language specified is not supported!")
            }
        }else{
            Log.e("TTS", "Initialization Failed!")
        }
    }

    private fun speakOut(text: String){
        tts!!.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
    }

    private fun setupExerciseStatusRecyclerView(){
        binding.rvExerciseStatus.layoutManager = LinearLayoutManager(this@ExerciseActivity,
            LinearLayoutManager.HORIZONTAL, false)

        exerciseAdapter = ExerciseStatusAdapter(exerciseList!!, this@ExerciseActivity)
        binding.rvExerciseStatus.adapter = exerciseAdapter
    }

    private fun customDialogForBackButton(){
        val customDialog = Dialog(this)
        customDialog.setContentView(R.layout.dialog_custom_back_confirmation)

        customDialog.findViewById<Button>(R.id.btnYes).setOnClickListener{
            finish()
            customDialog.dismiss()
        }
        customDialog.findViewById<Button>(R.id.btnNo).setOnClickListener{
            customDialog.dismiss()
        }

        val transparentBackgroundDrawable = ColorDrawable(Color.TRANSPARENT)
        customDialog.window!!.setBackgroundDrawable(transparentBackgroundDrawable)
        customDialog.show()
    }
}