package com.MuhammadZidane.Aplicash

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.MuhammadZidane.Aplicash.ui.theme.AplicashTheme

class TransactionActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AplicashTheme {
                TransactionScreen(onBack = { finish() })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionScreen(onBack: () -> Unit) {
    var searchQuery by remember { mutableStateOf("") }
    val categories = listOf("Semua")

    // Aesthetic colors
    val bgCanvas = Color(0xFFF8FAFC)
    val textDark = Color(0xFF0F172A)
    val brandBlue = Color(0xFF2563EB)

    Scaffold(
        containerColor = bgCanvas,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(bgCanvas)
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    onClick = onBack,
                    shape = CircleShape,
                    color = Color.White,
                    shadowElevation = 2.dp,
                    modifier = Modifier.size(44.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Rounded.ArrowBack, contentDescription = "Kembali", tint = textDark)
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    placeholder = { Text("Cari produk...", color = Color.Gray) },
                    leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = "Search", tint = Color.Gray) },
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Color.White,
                        focusedContainerColor = Color.White,
                        unfocusedBorderColor = Color.Transparent,
                        focusedBorderColor = Color.Transparent,
                        cursorColor = brandBlue
                    ),
                    singleLine = true
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Category List Horizontal
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                items(categories) { category ->
                    val isSelected = category == "Semua"
                    OutlinedButton(
                        onClick = { /* TODO */ },
                        shape = RoundedCornerShape(20.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            width = 1.dp,
                            color = if (isSelected) brandBlue else Color(0xFFE2E8F0)
                        ),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (isSelected) Color(0xFFEFF6FF) else Color.White,
                            contentColor = if (isSelected) brandBlue else textDark
                        ),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(category, fontWeight = FontWeight.SemiBold)
                    }
                }
                item {
                    OutlinedButton(
                        onClick = { /* TODO */ },
                        shape = RoundedCornerShape(20.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCBD5E1)),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color.White,
                            contentColor = Color(0xFF64748B)
                        ),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Icon(Icons.Rounded.Add, contentDescription = "Tambah", modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Tambah Kategori", fontWeight = FontWeight.Medium)
                    }
                }
            }

            // Empty space for product lists
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {

            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TransactionScreenPreview() {
    AplicashTheme {
        TransactionScreen(onBack = {})
    }
}

