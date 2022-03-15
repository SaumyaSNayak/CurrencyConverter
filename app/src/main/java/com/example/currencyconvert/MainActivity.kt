package com.example.currencyconvert

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.currencyconvert.main.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import com.example.currencyconvert.databinding.ActivityMainBinding
import androidx.core.view.isVisible
import kotlinx.coroutines.flow.collect


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /*binding.btnConvert.setOnClickListener {
            viewModel.convert(
                binding.etFrom.text.toString(),
                binding.spFromCurrency.selectedItem.toString(),
                binding.spToCurrency.selectedItem.toString(),
            )
        }*/

        /*binding.etFrom.setOnFocusChangeListener{ _, focused ->

        }*/

        binding.etFrom.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {
                val message = " - from - " + binding.fromCurrency.selectedItem.toString() + " - to - " + binding.toCurrency.selectedItem.toString()
                if (binding.fromCurrency.selectedItem.toString() != binding.toCurrency.selectedItem.toString()){
                    Toast.makeText(this@MainActivity, "if$message", Toast.LENGTH_SHORT).show()
                    viewModel.convert(
                        binding.etFrom.text.toString(),
                        binding.fromCurrency.selectedItem.toString(),
                        binding.toCurrency.selectedItem.toString(),
                    )
                } else {
                    Toast.makeText(this@MainActivity, "else$message", Toast.LENGTH_SHORT).show()
                }
            }
        })

        lifecycleScope.launchWhenStarted {
            viewModel.conversion.collect { event ->
                when(event) {
                    is MainViewModel.CurrencyEvent.Success -> {
                        binding.progressBar.isVisible = false
                        binding.etTo.setTextColor(Color.BLACK)
                        binding.etTo.text = event.resultText.toEditable()
                    }
                    is MainViewModel.CurrencyEvent.Failure -> {
                        binding.progressBar.isVisible = false
                        binding.etTo.setTextColor(Color.RED)
                        binding.etTo.text = event.errorText.toEditable()
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

}