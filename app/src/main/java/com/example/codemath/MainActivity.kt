package com.example.codemath

import android.animation.ArgbEvaluator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.SeekBar
import android.widget.SpinnerAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.Spinner


private const val TAG = "MainActivity"
private const val INITIAL_TIP_PERCENT = 15
private const val INITIAL_PARTY_SIZE = 1
class MainActivity : AppCompatActivity() {
    private lateinit var etBaseAmount: EditText
    private lateinit var seekBarTip: SeekBar
    private lateinit var tvTipPercentLabel: TextView
    private lateinit var tvTipAmount: TextView
    private lateinit var tvTotalAmount: TextView
    private lateinit var tvTipDescription: TextView
    private lateinit var spinPartyCount: Spinner
    private lateinit var tvPartySize: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        etBaseAmount = findViewById(R.id.etBaseAmount)
        seekBarTip = findViewById(R.id.seekBarTip)
        tvTipPercentLabel = findViewById(R.id.tvPercentLabel)
        tvTipAmount = findViewById(R.id.tvTipAmount)
        tvTotalAmount = findViewById(R.id.tvTotalAmount)
        tvTipDescription = findViewById(R.id.tvTipDescription)
        spinPartyCount = findViewById(R.id.spinPartyCount)
        tvPartySize = findViewById(R.id.tvPartySize)

        spinPartyCount.setSelection(0)
        tvPartySize.text = "$INITIAL_PARTY_SIZE"
        seekBarTip.progress = INITIAL_TIP_PERCENT
        tvTipPercentLabel.text = "$INITIAL_TIP_PERCENT"
        updateTipDescription(INITIAL_TIP_PERCENT)
        spinPartyCount.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>, selectedItemView: View?, position: Int, id: Long) {
                val partySize = position + 1
                val baseAmountText = etBaseAmount.text.toString()
                val baseAmount = if (baseAmountText.isNotEmpty() && baseAmountText.isNotBlank()) {
                    baseAmountText.toDouble()
                } else {
                    0.0
                }
                val tipPercent = seekBarTip.progress

                tvPartySize.text = partySize.toString()

                computeTotalPerPerson(baseAmount, tipPercent, partySize)
            }

            override fun onNothingSelected(parentView: AdapterView<*>) {}
        }

        seekBarTip.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seeker: SeekBar?, progress: Int, fromUser: Boolean) {
                Log.i(TAG, "onProgressChanged $progress")
                tvTipPercentLabel.text = "$progress"
                val baseAmountText = etBaseAmount.text.toString()
                val baseAmount = if (baseAmountText.isNotEmpty() && baseAmountText.isNotBlank()) {
                    baseAmountText.toDouble()
                } else {
                    0.0
                }
                computeTipAndTotal(baseAmount, progress)
                updateTipDescription(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })



        etBaseAmount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable) {
                val baseAmountText = s.toString()
                val baseAmount = if (baseAmountText.isNotEmpty() && baseAmountText.isNotBlank()) {
                    baseAmountText.toDouble()
                } else {
                    0.0
                }
                val tipPercent = seekBarTip.progress

                computeTipAndTotal(baseAmount, tipPercent)
                computeTotalPerPerson(baseAmount, tipPercent, getSelectedPartySize())
            }
        })

    }

    private fun computeTipAndTotal(baseAmount: Double, tipPercent: Int) {
        val tipAmount = baseAmount * tipPercent / 100
        val totalAmount = baseAmount + tipAmount
        tvTipAmount.text = "%.2f".format(tipAmount)
        tvTotalAmount.text = "%.2f".format(totalAmount)
    }


    private fun updateTipDescription(tipPercent: Int) {
        val tipDescription = when (tipPercent) {
            in 0..9 -> "Poor"
            in 10..14 -> "Acceptable"
            in 15..19 -> "Good"
            else -> "Amazing"
        }

        tvTipDescription.text = tipDescription
        // update the color based on the tip percent
        val color = ArgbEvaluator().evaluate(
            tipPercent.toFloat() / seekBarTip.max,
            ContextCompat.getColor(this, R.color.color_worst_tip),
            ContextCompat.getColor(this, R.color.color_best_tip)

        ) as Int
        tvTipDescription.setTextColor(color)

    }

    private fun computeTotalPerPerson(baseAmount: Double, tipPercent: Int, partySize: Int) {
        if (partySize <= 0) {
            // Handle division by zero or negative party size
            tvTotalAmount.text = ""
        } else {
            val tipAmount = baseAmount * tipPercent / 100
            val totalAmount = baseAmount + tipAmount
            val perPersonTotal = totalAmount / partySize
            tvTotalAmount.text = "%.2f".format(perPersonTotal)
        }
    }

    private fun getSelectedPartySize(): Int {
        return spinPartyCount.selectedItemPosition + 1
    }




}

