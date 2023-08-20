package com.example.a7minuteworkout


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.a7minuteworkout.databinding.ActivityBmiBinding
import java.math.BigDecimal
import java.math.RoundingMode

class BMIActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBmiBinding

    val METRIC_UNITS_VIEW = "METRIC UNIT VIEW"
    val US_UNITS_VIEW = "US UNIT VIEW"

    var currentVisibleView: String = METRIC_UNITS_VIEW

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBmiBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        setSupportActionBar(binding.toolBarBMIActivity)
        val actionBar = supportActionBar
        if (actionBar != null){
            actionBar.title = "Calculate BMI"
            actionBar.setDisplayHomeAsUpEnabled(true)
        }
        binding.toolBarBMIActivity.setNavigationOnClickListener {
            onBackPressed()
        }
        binding.btnCalculateUnits.setOnClickListener {

            if(currentVisibleView == METRIC_UNITS_VIEW){
                if(validateMetricUnits()){
                    try {
                        val heightValue: Float = binding.etMetricUnitHeight.text.toString().toFloat() / 100
                        val weightValue: Float = binding.etMetricUnitWeight.text.toString().toFloat()

                        val BMI = weightValue / (heightValue*heightValue)
                        displayBMIResult(BMI)
                    }catch (e: Exception){
                        Toast.makeText(
                            this@BMIActivity,
                            "Please, enter correct values!",
                            Toast.LENGTH_SHORT).show()

                        e.printStackTrace()
                    }
                }else{
                    Toast.makeText(
                        this@BMIActivity,
                        "Please, enter correct values!",
                        Toast.LENGTH_SHORT).show()
                }
            }else{
                if(validateUSUnits()){
                    try {
                        val usUnitsHeightFeetValue: String = binding.etUSUnitHeightFeet.text.toString()
                        val usUnitsHeightInchValue: String = binding.etUSUnitHeightInch.text.toString()
                        val usUnitsWeightValue: Float = binding.etUSUnitWeight.text.toString().toFloat()

                        val heightValue = usUnitsHeightInchValue.toFloat() + usUnitsHeightFeetValue.toFloat() * 12

                        val bmi = 703 * (usUnitsWeightValue / (heightValue*heightValue))
                        displayBMIResult(bmi)
                    }catch (e: Exception){
                        Toast.makeText(
                            this@BMIActivity,
                            "Please, enter correct values!",
                            Toast.LENGTH_SHORT).show()

                        e.printStackTrace()
                    }
                }else{
                    Toast.makeText(
                        this@BMIActivity,
                        "Please, enter correct values!",
                        Toast.LENGTH_SHORT).show()
                }
            }
        }

        makeVisibleMetricUnitsView()

        binding.rgUnits.setOnCheckedChangeListener { group, checkedId ->
            if(checkedId == R.id.rbMetricUnits){
                makeVisibleMetricUnitsView()
            }else{
                makeVisibleUSUnitsView()
            }
        }
    }

    private fun validateUSUnits(): Boolean{
        var isValid = true

        when{
            binding.etUSUnitWeight.text.toString().isEmpty() -> isValid = false
            binding.etUSUnitHeightInch.text.toString().isEmpty() -> isValid = false
            binding.etUSUnitHeightFeet.text.toString().isEmpty() -> isValid = false
        }

        return isValid
    }
    private fun validateMetricUnits(): Boolean{
        var isValid = true

        when{
            binding.etMetricUnitHeight.text.toString().isEmpty() -> isValid = false
            binding.etMetricUnitWeight.text.toString().isEmpty() -> isValid = false
        }

        return isValid
    }
    private fun displayBMIResult(bmi: Float){
        val bmiLabel: String
        val bmiDescription:String

        if (bmi.compareTo(15f) <= 0) {
            bmiLabel = "Very severely underweight"
            bmiDescription = "Oops! You really need to take better care of yourself! Eat more!"
        } else if (bmi.compareTo(15f) > 0 && bmi.compareTo(16f) <= 0
        ) {
            bmiLabel = "Severely underweight"
            bmiDescription = "Oops!You really need to take better care of yourself! Eat more!"
        } else if (bmi.compareTo(16f) > 0 && bmi.compareTo(18.5f) <= 0
        ) {
            bmiLabel = "Underweight"
            bmiDescription = "Oops! You really need to take better care of yourself! Eat more!"
        } else if (bmi.compareTo(18.5f) > 0 && bmi.compareTo(25f) <= 0
        ) {
            bmiLabel = "Normal"
            bmiDescription = "Congratulations! You are in a good shape!"
        } else if (java.lang.Float.compare(bmi, 25f) > 0 && java.lang.Float.compare(
                bmi,
                30f
            ) <= 0
        ) {
            bmiLabel = "Overweight"
            bmiDescription = "Oops! You really need to take care of your yourself! Workout maybe!"
        } else if (bmi.compareTo(30f) > 0 && bmi.compareTo(35f) <= 0
        ) {
            bmiLabel = "Obese Class | (Moderately obese)"
            bmiDescription = "Oops! You really need to take care of your yourself! Workout maybe!"
        } else if (bmi.compareTo(35f) > 0 && bmi.compareTo(40f) <= 0
        ) {
            bmiLabel = "Obese Class || (Severely obese)"
            bmiDescription = "OMG! You are in a very dangerous condition! Act now!"
        } else {
            bmiLabel = "Obese Class ||| (Very Severely obese)"
            bmiDescription = "OMG! You are in a very dangerous condition! Act now!"
        }

        binding.llBMIResultLayout.visibility = View.VISIBLE

        val bmiValue = BigDecimal(bmi.toDouble()).setScale(2, RoundingMode.HALF_EVEN).toString()

        binding.tvBMIValue.text = bmiValue
        binding.tvBMIType.text = bmiLabel
        binding.tvBMIDescription.text = bmiDescription
    }
    private fun makeVisibleMetricUnitsView(){
        currentVisibleView = METRIC_UNITS_VIEW
        binding.tilMetricUnitWeight.visibility = View.VISIBLE
        binding.tilMetricUnitHeight.visibility = View.VISIBLE

        binding.etMetricUnitHeight.text!!.clear()
        binding.etMetricUnitWeight.text!!.clear()

        binding.tilUSUnitWeight.visibility = View.GONE
        binding.llUSUnitsHeight.visibility = View.GONE

        binding.llBMIResultLayout.visibility = View.INVISIBLE
    }
    private fun makeVisibleUSUnitsView(){
        currentVisibleView = US_UNITS_VIEW
        binding.tilMetricUnitWeight.visibility = View.GONE
        binding.tilMetricUnitHeight.visibility = View.GONE

        binding.etUSUnitHeightFeet.text!!.clear()
        binding.etUSUnitHeightInch.text!!.clear()
        binding.etUSUnitWeight.text!!.clear()

        binding.tilUSUnitWeight.visibility = View.VISIBLE
        binding.llUSUnitsHeight.visibility = View.VISIBLE

        binding.llBMIResultLayout.visibility = View.INVISIBLE
    }
}