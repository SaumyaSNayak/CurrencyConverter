package com.example.currencyconvert.data.models

import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import org.json.JSONObject

data class CurrencyResponse(
    @SerializedName("rates")
    val rates: JsonObject
)