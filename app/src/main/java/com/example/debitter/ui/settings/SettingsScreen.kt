package com.example.debitter.ui.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.debitter.model.AgentEntry
import com.example.debitter.model.CustomerEntry
import com.example.debitter.model.Letterhead
import com.example.debitter.model.ShippingAgent
import com.example.debitter.ui.components.AppSnackbarHost
import com.example.debitter.ui.components.AppTopBar
import com.example.debitter.ui.components.rememberAppSnackbarState
import com.example.debitter.ui.settings.components.AgentSheet
import com.example.debitter.ui.settings.components.CustomerSheet
import com.example.debitter.ui.settings.components.directorySection
import com.example.debitter.ui.theme.AppColors
import com.example.debitter.ui.theme.AppSpacing
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onDeleteAgent: (AgentEntry) -> Unit,
    onDeleteCustomer: (CustomerEntry) -> Unit,
    onSaveCustomer: (String?, Letterhead) -> Unit,
    onSaveAgent: (String?, ShippingAgent) -> Unit,
    state: SettingsState,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    val snackbarState = rememberAppSnackbarState()

    var isAgentSheetOpen by remember { mutableStateOf(false) }
    var isCustomerSheetOpen by remember { mutableStateOf(false) }

    var editingAgent by remember { mutableStateOf<AgentEntry?>(null) }

    var editingCustomer by remember { mutableStateOf<CustomerEntry?>(null) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = AppColors.surfaceBase,
        snackbarHost = { AppSnackbarHost(state = snackbarState) },
        topBar = { AppTopBar(onBack = onBack, title = "Settings") },
    ) { padding ->
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(modifier = Modifier.size(AppSpacing.progressIndicator), color = AppColors.primary, strokeWidth = AppSpacing.progressStroke)
            }
        } else {
            SettingsList(
                modifier = Modifier.fillMaxSize().padding(padding),
                onAddAgent = {
                    editingAgent = null
                    isAgentSheetOpen = true
                },
                onAddCustomer = {
                    editingCustomer = null
                    isCustomerSheetOpen = true
                },
                onOpenAgent = {
                    editingAgent = it
                    isAgentSheetOpen = true
                },
                onOpenCustomer = {
                    editingCustomer = it
                    isCustomerSheetOpen = true
                },
                state = state,
            )
        }
    }

    if (isCustomerSheetOpen) {
        val customer = editingCustomer

        CustomerSheet(
            entry = customer,
            onDelete = {
                isCustomerSheetOpen = false
                if (customer != null) onDeleteCustomer(customer)
                scope.launch { snackbarState.showBrief("That customer was deleted. Documents you already saved keep the details they were made with.") }
            },
            onDismiss = { isCustomerSheetOpen = false },
            onSave = { letterhead ->
                isCustomerSheetOpen = false
                onSaveCustomer(customer?.id, letterhead)
                scope.launch {
                    snackbarState.showSuccess(
                        if (customer == null) {
                            "Customer added. Choose it from the customer list on your next debit note or refund letter."
                        } else {
                            "Customer updated. Documents you already saved keep the details they were made with."
                        },
                    )
                }
            },
        )
    }

    if (isAgentSheetOpen) {
        val agent = editingAgent

        AgentSheet(
            entry = agent,
            onDelete = {
                isAgentSheetOpen = false
                if (agent != null) onDeleteAgent(agent)
                scope.launch { snackbarState.showBrief("That shipping agent was deleted. Letters you already saved keep the details they were made with.") }
            },
            onDismiss = { isAgentSheetOpen = false },
            onSave = { shippingAgent ->
                isAgentSheetOpen = false
                onSaveAgent(agent?.id, shippingAgent)
                scope.launch {
                    snackbarState.showSuccess(
                        if (agent == null) {
                            "Shipping agent added. Choose it from the shipping agent list on your next refund letter."
                        } else {
                            "Shipping agent updated. Letters you already saved keep the details they were made with."
                        },
                    )
                }
            },
        )
    }
}

@Composable
private fun SettingsList(onAddAgent: () -> Unit, onAddCustomer: () -> Unit, onOpenAgent: (AgentEntry) -> Unit, onOpenCustomer: (CustomerEntry) -> Unit, state: SettingsState, modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier, contentPadding = PaddingValues(top = AppSpacing.lg)) {
        directorySection(
            addLabel = "Add Customer",
            emptyText = "No customers yet. Tap Add Customer to create the first one.",
            heading = "Customers",
            intro = "Customers listed here appear when you choose a customer on a debit note or a refund letter. Documents you already saved keep the details they were made with.",
            entries = state.customers,
            onAdd = onAddCustomer,
            detail = { it.letterhead.addressLine },
            id = { it.id },
            name = { it.letterhead.name },
            onOpen = onOpenCustomer,
        )
        directorySection(
            addLabel = "Add Shipping Agent",
            emptyText = "No shipping agents yet. Tap Add Shipping Agent to create the first one.",
            heading = "Shipping Agents",
            intro = "Shipping agents listed here appear when you choose a shipping agent on a refund letter. Letters you already saved keep the details they were made with.",
            entries = state.agents,
            onAdd = onAddAgent,
            detail = { it.agent.address.lines().joinToString(", ") },
            id = { it.id },
            name = { it.agent.name },
            onOpen = onOpenAgent,
        )
    }
}
