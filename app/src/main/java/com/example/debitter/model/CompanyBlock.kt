package com.example.debitter.model

import androidx.compose.runtime.Immutable
import java.io.Serializable

@Immutable
data class CompanyBlock(val addressLine: String, val contactLine: String, val name: String) : Serializable
