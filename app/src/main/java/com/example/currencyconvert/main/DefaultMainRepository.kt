package com.example.currencyconvert.main

import com.example.currencyconvert.data.CurrencyApi
import com.example.currencyconvert.data.models.CurrencyResponse
import com.example.currencyconvert.util.Resource
import javax.inject.Inject

class DefaultMainRepository @Inject constructor(
    private val api: CurrencyApi
) : MainRepository {

    override suspend fun getRates(access_key: String): Resource<CurrencyResponse> {
        return try {
            val response = api.getRates(access_key, 1)
            val result = response.body()
            if(response.isSuccessful && result != null) {
                Resource.Success(result)
            } else {
                Resource.Error(response.message())
            }
        } catch(e: Exception) {
            Resource.Error(e.message ?: "An error occured")
        }
    }
}