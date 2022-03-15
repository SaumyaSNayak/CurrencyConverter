package com.example.currencyconvert.main

import com.example.currencyconvert.data.models.CurrencyResponse
import com.example.currencyconvert.util.Resource

interface MainRepository {

    suspend fun getRates(access_key: String): Resource<CurrencyResponse>
}