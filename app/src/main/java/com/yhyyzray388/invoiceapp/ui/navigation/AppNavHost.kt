package com.yhyyzray388.invoiceapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.yhyyzray388.invoiceapp.InvoiceApplication
import com.yhyyzray388.invoiceapp.domain.usecase.CalculateInvoiceTotalsUseCase
import com.yhyyzray388.invoiceapp.domain.usecase.CreateInvoiceUseCase
import com.yhyyzray388.invoiceapp.domain.usecase.DeleteInvoiceUseCase
import com.yhyyzray388.invoiceapp.domain.usecase.GetInvoicesUseCase
import com.yhyyzray388.invoiceapp.domain.usecase.UpdateInvoiceUseCase
import com.yhyyzray388.invoiceapp.ui.invoice.CreateInvoiceScreen
import com.yhyyzray388.invoiceapp.ui.invoice.CreateInvoiceViewModel
import com.yhyyzray388.invoiceapp.ui.invoice.EditInvoiceScreen
import com.yhyyzray388.invoiceapp.ui.invoice.EditInvoiceViewModel
import com.yhyyzray388.invoiceapp.ui.invoice.InvoiceDetailsScreen
import com.yhyyzray388.invoiceapp.ui.invoice.InvoiceDetailsViewModel
import com.yhyyzray388.invoiceapp.ui.invoices.InvoicesScreen
import com.yhyyzray388.invoiceapp.ui.invoices.InvoicesViewModel

@Composable
fun AppNavHost(application: InvoiceApplication) {
    val navController = rememberNavController()
    val container = application.appContainer

    NavHost(navController = navController, startDestination = AppDestination.Invoices.route) {
        composable(AppDestination.Invoices.route) {
            val vm: InvoicesViewModel = viewModel(
                factory = remember { InvoicesViewModel.Factory(GetInvoicesUseCase(container.invoiceRepository)) }
            )
            InvoicesScreen(
                viewModel = vm,
                onCreateInvoice = { navController.navigate(AppDestination.CreateInvoice.route) },
                onInvoiceClick = { id -> navController.navigate(AppDestination.InvoiceDetails.createRoute(id)) }
            )
        }
        composable(AppDestination.CreateInvoice.route) {
            val vm: CreateInvoiceViewModel = viewModel(
                factory = remember {
                    CreateInvoiceViewModel.Factory(
                        CreateInvoiceUseCase(container.invoiceRepository, container.invoiceItemRepository),
                        CalculateInvoiceTotalsUseCase()
                    )
                }
            )
            CreateInvoiceScreen(vm, onSaved = { navController.popBackStack() }, onBack = { navController.popBackStack() })
        }
        composable(AppDestination.InvoiceDetails.route, arguments = listOf(navArgument("invoiceId") { type = NavType.LongType })) { entry ->
            val id = entry.arguments?.getLong("invoiceId") ?: return@composable
            val vm: InvoiceDetailsViewModel = viewModel(
                factory = remember(id) {
                    InvoiceDetailsViewModel.Factory(
                        container.invoiceRepository,
                        container.invoiceItemRepository,
                        DeleteInvoiceUseCase(container.invoiceRepository),
                        id
                    )
                }
            )
            InvoiceDetailsScreen(
                viewModel = vm,
                onBack = { navController.popBackStack() },
                onEdit = { invoiceId -> navController.navigate(AppDestination.EditInvoice.createRoute(invoiceId)) }
            )
        }
        composable(AppDestination.EditInvoice.route, arguments = listOf(navArgument("invoiceId") { type = NavType.LongType })) { entry ->
            val id = entry.arguments?.getLong("invoiceId") ?: return@composable
            val vm: EditInvoiceViewModel = viewModel(
                factory = remember(id) {
                    EditInvoiceViewModel.Factory(
                        container.invoiceRepository,
                        container.invoiceItemRepository,
                        UpdateInvoiceUseCase(container.invoiceRepository, container.invoiceItemRepository),
                        CalculateInvoiceTotalsUseCase(),
                        id
                    )
                }
            )
            EditInvoiceScreen(vm, onSaved = { navController.popBackStack() }, onBack = { navController.popBackStack() })
        }
    }
}
