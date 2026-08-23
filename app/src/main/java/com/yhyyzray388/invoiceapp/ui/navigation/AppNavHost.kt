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
import com.yhyyzray388.invoiceapp.ui.invoice.CreateInvoiceScreen
import com.yhyyzray388.invoiceapp.ui.invoice.CreateInvoiceViewModel
import com.yhyyzray388.invoiceapp.ui.invoice.InvoiceViewModel
import com.yhyyzray388.invoiceapp.ui.invoice.InvoicesScreen

@Composable
fun AppNavHost(application: InvoiceApplication) {
    val navController = rememberNavController()
    val container = application.appContainer
    val invoiceViewModel: InvoiceViewModel = viewModel(
        factory = remember { InvoiceViewModel.Factory(container.invoiceRepository) }
    )

    NavHost(navController = navController, startDestination = AppDestination.Invoices.route) {
        composable(AppDestination.Invoices.route) {
            InvoicesScreen(
                viewModel = invoiceViewModel,
                onCreateInvoice = { navController.navigate(AppDestination.CreateInvoice.route) },
                onInvoiceClick = { id -> navController.navigate(AppDestination.InvoiceDetails.createRoute(id)) }
            )
        }
        composable(AppDestination.CreateInvoice.route) {
            val createViewModel: CreateInvoiceViewModel = viewModel(
                factory = remember {
                    CreateInvoiceViewModel.Factory(
                        CreateInvoiceUseCase(container.invoiceRepository, container.invoiceItemRepository),
                        CalculateInvoiceTotalsUseCase()
                    )
                }
            )
            CreateInvoiceScreen(
                viewModel = createViewModel,
                onSaved = { navController.popBackStack() },
                onBack = { navController.popBackStack() }
            )
        }
        composable(
            route = AppDestination.InvoiceDetails.route,
            arguments = listOf(navArgument("invoiceId") { type = NavType.LongType })
        ) {
            // Details screen is implemented in the next step.
            androidx.compose.material3.Text("تفاصيل الفاتورة")
        }
    }
}
