package com.example.debitter.model

import androidx.compose.runtime.Immutable
import java.io.Serializable

@Immutable
data class Letterhead(val addressLine: String, val contactLine: String, val name: String, val tagline: String) : Serializable
