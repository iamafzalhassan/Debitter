package com.example.debitter.model

import androidx.compose.runtime.Immutable
import java.io.Serializable

@Immutable
data class ShippingAgent(val address: String, val name: String) : Serializable
