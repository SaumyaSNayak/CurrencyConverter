package com.example.currencyconvert

import android.R
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.currencyconvert.main.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import com.example.currencyconvert.databinding.ActivityMainBinding
import androidx.core.view.isVisible
import com.google.gson.JsonObject
import kotlinx.coroutines.flow.collect
import java.math.RoundingMode
import java.text.DecimalFormat


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    private lateinit var rates: JsonObject

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.etTo.text = "1".toEditable()
        binding.etFrom.text = "1".toEditable()
        binding.progressBar.isVisible = true
        rates = JsonObject()

        viewModel.convert("fdbd7a5216ac1a61363e33afef062144")

        lifecycleScope.launchWhenStarted {
            viewModel.conversion.collect { event ->
                when(event) {
                    is MainViewModel.CurrencyEvent.Success -> {
                        binding.progressBar.isVisible = false
                        binding.etTo.setTextColor(Color.BLACK)
                        binding.etFrom.setTextColor(Color.BLACK)

                        rates = event.rates
                        val adapter = ArrayAdapter(this@MainActivity, R.layout.simple_spinner_item, event.currencies.toList())
                        binding.fromCurrency.adapter = adapter
                        binding.toCurrency.adapter = adapter

                        binding.etFrom.addTextChangedListener(object : TextWatcher {

                            override fun afterTextChanged(s: Editable) {}

                            override fun beforeTextChanged(s: CharSequence, start: Int,
                                                           count: Int, after: Int) {
                            }

                            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                                if (binding.etFrom.hasFocus() && !s.isEmpty() && !(s.endsWith('.') && s.startsWith('0')))
                                    binding.etTo.text =
                                        convertCurrency(binding.fromCurrency.selectedItem.toString(),
                                            binding.toCurrency.selectedItem.toString(),
                                            s.toString()).toEditable()
                            }
                        })
                        binding.etTo.addTextChangedListener(object : TextWatcher {

                            override fun afterTextChanged(s: Editable) {}

                            override fun beforeTextChanged(s: CharSequence, start: Int,
                                                           count: Int, after: Int) {
                            }

                            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                                if (binding.etTo.hasFocus() && !s.isEmpty() && !(s.endsWith('.') && s.startsWith('0')))
                                    binding.etFrom.text =
                                        convertCurrency(binding.toCurrency.selectedItem.toString(),
                                            binding.fromCurrency.selectedItem.toString(),
                                            s.toString()).toEditable()
                            }
                        })

                        binding.fromCurrency.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                                binding.etTo.text =
                                    convertCurrency(binding.fromCurrency.selectedItem.toString(),
                                        binding.toCurrency.selectedItem.toString(),
                                        binding.etFrom.text.toString()).toEditable()
                            }

                            override fun onNothingSelected(parent: AdapterView<*>?) {
                            }
                        }
                        binding.toCurrency.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                                binding.etFrom.text =
                                    convertCurrency(binding.toCurrency.selectedItem.toString(),
                                        binding.fromCurrency.selectedItem.toString(),
                                        binding.etTo.text.toString()).toEditable()
                            }

                            override fun onNothingSelected(parent: AdapterView<*>?) {
                            }
                        }

                        binding.btnDetils.setOnClickListener {
                            val intent = Intent(this@MainActivity, HistoricalActivity::class.java)
                            intent.putExtra("fromCurrency", binding.fromCurrency.selectedItem.toString())
                            intent.putExtra("toCurrency", binding.toCurrency.selectedItem.toString())
                            startActivity(intent)
                        }
                    }
                    is MainViewModel.CurrencyEvent.Failure -> {
                        binding.progressBar.isVisible = false
                        binding.etTo.setTextColor(Color.RED)
                        binding.etFrom.setTextColor(Color.RED)
                        Log.d("TAG---", event.errorText)
                        Toast.makeText(this@MainActivity, event.errorText, Toast.LENGTH_SHORT).show()
                    }
                    is MainViewModel.CurrencyEvent.Loading -> {
                        binding.progressBar.isVisible = true
                    }
                    else -> Unit
                }
            }
        }
    }

    fun String.toEditable(): Editable =  Editable.Factory.getInstance().newEditable(this)

    fun convertCurrency(from: String, to: String, amount: String): String {
        val fromValue: Double = rates.get(from).asDouble
        val toValue: Double = rates.get(to).asDouble
        return String.format("%.2f", ((toValue/fromValue) * amount.toDouble()))
    }

}