package com.example.debitter.model

enum class AgentField(val caption: String, val key: String) {
    NAME("Agent Name", "name"),
    ADDRESS("Address Lines", "address"),
    ;

    val isMultiline: Boolean get() = this == ADDRESS
}

fun ShippingAgent.value(field: AgentField): String = when (field) {
    AgentField.ADDRESS -> address
    AgentField.NAME -> name
}

fun ShippingAgent.with(field: AgentField, value: String): ShippingAgent = when (field) {
    AgentField.ADDRESS -> copy(address = value)
    AgentField.NAME -> copy(name = value)
}
