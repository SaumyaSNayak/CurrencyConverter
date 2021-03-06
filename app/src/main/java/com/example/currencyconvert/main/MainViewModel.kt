package com.example.currencyconvert.main

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.currencyconvert.util.DispatcherProvider
import com.example.currencyconvert.util.Resource
import com.google.gson.JsonObject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.math.round

class MainViewModel @ViewModelInject constructor(
    private val repository: MainRepository,
    private val dispatchers: DispatcherProvider
): ViewModel() {

    sealed class CurrencyEvent {
        class Success(val currencies: Set<String>, val rates: JsonObject): CurrencyEvent()
        class Failure(val errorText: String): CurrencyEvent()
        object Loading : CurrencyEvent()
        object Empty : CurrencyEvent()
    }

    private val _conversion = MutableStateFlow<CurrencyEvent>(CurrencyEvent.Empty)
    val conversion: StateFlow<CurrencyEvent> = _conversion

    fun convert(access_key: String) {
        viewModelScope.launch(dispatchers.io) {
            _conversion.value = CurrencyEvent.Loading
            when(val ratesResponse = repository.getRates(access_key)) {
                is Resource.Error -> _conversion.value = CurrencyEvent.Failure(ratesResponse.message!!)
                is Resource.Success -> {
                    val currencies: Set<String> = ratesResponse.data!!.rates.keySet()
                    val rates: JsonObject = ratesResponse.data!!.rates
                    if(currencies.isEmpty()) {
                        _conversion.value = CurrencyEvent.Failure("Unexpected error")
                    } else {
                        _conversion.value = CurrencyEvent.Success(currencies, rates)
                    }
                }
            }
        }
    }

}