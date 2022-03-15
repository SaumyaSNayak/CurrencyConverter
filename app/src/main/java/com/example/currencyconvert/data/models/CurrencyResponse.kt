package com.example.currencyconvert.data.models

import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import org.json.JSONObject

data class CurrencyResponse(
    val base: String,
    val date: String,
    val rates: JsonObject
)