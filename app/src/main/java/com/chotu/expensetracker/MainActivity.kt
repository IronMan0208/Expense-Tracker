package com.chotu.expensetracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.chotu.expensetracker.data.database.DatabaseProvider
import com.chotu.expensetracker.repository.ExpenseRepository
import com.chotu.expensetracker.ui.theme.ExpenseTrackerTheme
import com.chotu.expensetracker.viewmodel.ExpenseViewModel
import com.chotu.expensetracker.viewmodel.ExpenseViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ExpenseTrackerTheme {
                Expense_Tracker()
            }
        }
    }
}

val expenseColors = listOf(
    Color(0xFFD6F5D6), // soft green
    Color(0xFFFFE8C8), // soft orange
    Color(0xFFFFF9C4), // soft yellow
    Color(0xFFD6EAFF), // soft blue
    Color(0xFFEDD6FF), // soft purple
    Color(0xFFFFD6F0), // soft pink
    Color(0xFFFFD6D6), // soft red
)

@Composable
fun Expense_Tracker() {

    val context = LocalContext.current
    val database = remember {
        DatabaseProvider.getDatabase(context)
    }
    val repository = remember {
        ExpenseRepository(database.expenseDao())
    }
    val factory = remember {
        ExpenseViewModelFactory(repository)
    }
    val viewModel: ExpenseViewModel = viewModel(
        factory = factory
    )

    var title by remember {
        mutableStateOf("")
    }
    var amount by remember {
        mutableStateOf("")
    }

    val totalExpense = viewModel.expenses.sumOf {
        it.amount
    }

    LaunchedEffect(Unit) {
        viewModel.loadExpenses()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1B5E20), // dark green
                        Color(0xFF66BB6A)  // light green
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            //Header
            Text(
                "💰 Expense Tracker",
                fontSize = 30.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )
            Text(
                "${viewModel.expenses.size} expenses added",
                fontSize = 13.sp,
                color = Color.White.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(20.dp))

            //Total Expense Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF2E7D32)
                ),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Total Expenses",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                    Text(
                        "₹ ${"%.2f".format(totalExpense)}",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            //Input Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    OutlinedTextField(
                        value = title,
                        onValueChange = {
                            title = it
                        },
                        placeholder = {
                            Text("Enter Description")
                        },
                        label = {
                            Text("Description")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = amount,
                        onValueChange = {
                            amount = it
                        },
                        placeholder = {
                            Text("Enter amount")
                        },
                        label = {
                            Text("Amount (₹)")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )

                    Spacer(
                        modifier = Modifier.height(14.dp)
                    )

                    Button(
                        onClick = {
                            val amountValue = amount.toDoubleOrNull()
                            if (title.isNotEmpty() && amountValue != null) {
                                viewModel.insertExpense(
                                    title = title,
                                    amount = amountValue
                                )
                                title = ""
                                amount = ""
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2E7D32)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add",
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Add Expense",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Expenses List
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(viewModel.expenses.size) { index ->
                    val cardColor = expenseColors[index % expenseColors.size]
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = cardColor
                        ),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            //Iden Circle
                            Box(
                                modifier = Modifier
                                    .size(30.dp)
                                    .background(
                                        color = Color.White.copy(alpha = 0.6f),
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "${index + 1}",
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1B5E20)
                                )
                            }
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    viewModel.expenses[index].title,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = Color(0xFF222222)
                                )
                                Text(
                                    text = "₹ ${"%.2f".format(viewModel.expenses[index].amount)}",
                                    fontSize = 14.sp,
                                    color = Color(0xFF444444)
                                )
                            }
                            // Delete button
                            IconButton(
                                onClick = { viewModel.deleteExpense(
                                    viewModel.expenses[index]
                                ) },
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(
                                        color = Color.White.copy(alpha = 0.5f),
                                        shape = CircleShape
                                    )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    tint = Color(0xFFE53935),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

//@Composable
//fun Expense_Tracker(modifier: Modifier = Modifier) {
//
//    var title by remember {
//        mutableStateOf("")
//    }
//
//    var amount by remember {
//        mutableStateOf("")
//    }
//
//    val expenses = remember {
//        mutableStateListOf<Expense>()
//    }
//
//    Column(
//        modifier = Modifier.fillMaxSize(),
//        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.Top
//    ) {
//        Text(
//            "Expense Tracker",
//            fontSize = 32.sp,
//            fontWeight = FontWeight.Bold
//        )
//
//        Spacer(modifier = Modifier.height(15.dp))
//
//        Text(
//            "Description",
//            fontSize = 20.sp,
//            fontWeight = FontWeight.SemiBold,
//            textAlign = TextAlign.Left
//        )
//
//        Spacer(modifier = Modifier.height(10.dp))
//
//        OutlinedTextField(
//            value = title,
//            onValueChange = {
//                title = it
//            },
//            placeholder = {
//                Text("Enter Description")
//            }
//        )
//
//        Spacer(modifier = Modifier.height(15.dp))
//
//        Text(
//            "Amount",
//            fontSize = 20.sp,
//            fontWeight = FontWeight.SemiBold,
//            textAlign = TextAlign.Left
//        )
//
//        Spacer(modifier = Modifier.height(10.dp))
//
//        OutlinedTextField(
//            value = amount,
//            onValueChange = {
//                amount = it
//            },
//            placeholder = {
//                Text("Enter Amount")
//            }
//        )
//
//        Spacer(modifier = Modifier.height(15.dp))
//
//        Button(onClick = {
//            val amountValue = amount.toDoubleOrNull()
//            if (title.isNotEmpty() && amountValue != null) {
//                expenses.add(
//                    Expense(
//                        title = title,
//                        amount = amountValue
//                    )
//                )
//                title = ""
//                amount = ""
//            }
//
//        }) {
//            Text("Add Expense")
//        }
//
//        Spacer(modifier = Modifier.height(10.dp))
//
//        Text("Total Expenses")
//
//        val totalExpense = expenses.sumOf {
//            it.amount
//        }
//
//        Card(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(16.dp)
//        ) {
//
//            Column(
//                modifier = Modifier.padding(16.dp)
//            ) {
//
//                Text(
//                    "Total Expense",
//                    fontWeight = FontWeight.SemiBold
//                )
//
//                Text(
//                    "₹ $totalExpense",
//                    fontSize = 28.sp,
//                    fontWeight = FontWeight.Bold
//                )
//            }
//        }
//
//        LazyColumn() {
//            items(expenses.size) { index ->
//                Card(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(8.dp)
//                ) {
//                    Row(
//                        modifier = Modifier.fillMaxWidth(),
//                        horizontalArrangement = Arrangement.SpaceBetween,
//                        verticalAlignment = Alignment.CenterVertically
//                    ) {
//                        Column(
//
//                        ) {
//                            Text(
//                                expenses[index].title,
//                                fontWeight = FontWeight.Bold,
//                                fontSize = 18.sp
//                            )
//
//                            Text(
//                                "₹ ${expenses[index].amount}",
//                            )
//
//                        }
//                        IconButton(onClick = {
//                            expenses.removeAt(index)
//                        }) {
//                            Icon(
//                                imageVector = Icons.Default.Delete,
//                                contentDescription = "Delete"
//                            )
//                        }
//                    }
//                }
//            }
//        }
//    }
//}

