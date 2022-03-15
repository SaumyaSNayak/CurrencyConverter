package com.example.currencyconvert.main

import com.example.currencyconvert.data.models.CurrencyResponse
import com.example.currencyconvert.util.Resource

interface MainRepository {

    suspend fun getRates(base: String): Resource<CurrencyResponse>
}