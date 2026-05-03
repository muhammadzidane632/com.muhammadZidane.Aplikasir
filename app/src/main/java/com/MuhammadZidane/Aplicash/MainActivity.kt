@file:Suppress("PackageName", "unused")
package com.MuhammadZidane.Aplicash

import android.os.Bundle
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.MuhammadZidane.Aplicash.ui.theme.AplicashTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AplicashTheme {
                HomeScreen()
            }
        }
    }
}

// Models to structure data instead of using raw hardcoded variables throughout
data class UserProfile(val name: String, val shopName: String)
data class RevenueState(val amount: Long, val trendPercentage: Int, val isActive: Boolean, val activeHours: String)
data class SummaryData(val totalOrders: Int, val bestSellingProduct: String)
data class TransactionItem(val id: String, val customerName: String, val amount: Long, val time: String, val status: TransactionStatus)

// Modern color palette for status
enum class TransactionStatus(val label: String, val color: Color, val textColor: Color) {
    COMPLETED("Selesai", Color(0xFFDCFCE7), Color(0xFF166534)),
    PENDING("Proses", Color(0xFFFEF3C7), Color(0xFF9A3412)),
    CANCELLED("Batal", Color(0xFFFEE2E2), Color(0xFF991B1B))
}

// Modern Neo-Fintech / Indonesian Startup Aesthetic Colors
private val BrandBlue = Color(0xFF2563EB)
private val BrandBlueLight = Color(0xFFDBEAFE)
private val BrandCyan = Color(0xFF0891B2)
private val TextHeadline = Color(0xFF0F172A)
private val TextBody = Color(0xFF64748B)
private val BgCanvas = Color(0xFFF8FAFC)
private val CardWhite = Color(0xFFFFFFFF)
private val AccentPink = Color(0xFFEC4899)
private val AccentOrange = Color(0xFFF59E0B)

@Composable
fun HomeScreen(
    userProfile: UserProfile = UserProfile("Nama Pengguna", "Nama Toko"),
    revenueState: RevenueState = RevenueState(0L, 0, false, "00:00 - 00:00"),
    summaryData: SummaryData = SummaryData(0, "-"),
    transactions: List<TransactionItem> = emptyList()
) {
    var searchQuery by remember { mutableStateOf("") }

    val configuration = LocalConfiguration.current
    val isWideScreen = configuration.screenWidthDp > 600
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            bottomBar = { if (!isWideScreen) AppBottomNavigationNeo() },
            containerColor = BgCanvas
        ) { innerPadding ->
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                // Beautiful dynamic background header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(if (isWideScreen) 240.dp else 290.dp)
                        .clip(RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFF1E3A8A), BrandBlue, BrandCyan)
                            )
                        )
                )

                if (isWideScreen) {
                    // Tablet & Landscape Mode
                    Row(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
                        horizontalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        // Left Nav (Sidebar equivalent)
                        Column(
                            modifier = Modifier.width(100.dp).fillMaxHeight().padding(top = 24.dp, bottom = 24.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            SidebarNeoItem(Icons.Rounded.Home, "Beranda", true)
                            SidebarNeoItem(Icons.Rounded.Menu, "Riwayat", false)
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(CircleShape)
                                    .background(Brush.linearGradient(listOf(BrandBlue, BrandCyan))),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Rounded.Search, contentDescription = "Scan", tint = Color.White, modifier = Modifier.size(28.dp))
                            }
                            SidebarNeoItem(Icons.Rounded.Info, "Statistik", false)
                            SidebarNeoItem(Icons.Rounded.Person, "Profil", false)
                        }

                        // Middle Content
                        LazyColumn(
                            modifier = Modifier.weight(1.2f).fillMaxHeight(),
                            contentPadding = PaddingValues(bottom = 24.dp, top = 24.dp)
                        ) {
                            item { TopHeaderNeo(userProfile) }
                            item { Spacer(modifier = Modifier.height(20.dp)) }
                            item { SearchSectionNeo(searchQuery, onQueryChange = { searchQuery = it }) }
                            item { RevenueBannerNeo(revenueState) }
                            item { Spacer(modifier = Modifier.height(28.dp)) }
                            item { ActionGridNeo(onTransaksiClick = { context.startActivity(Intent(context, TransactionActivity::class.java)) }) }
                        }

                        // Right Content (Transactions & Summary)
                        LazyColumn(
                            modifier = Modifier.weight(1f).fillMaxHeight(),
                            contentPadding = PaddingValues(bottom = 24.dp, top = 24.dp)
                        ) {
                            item {
                                Box(modifier = Modifier.background(CardWhite, RoundedCornerShape(32.dp)).fillMaxWidth().padding(20.dp)) {
                                    SummarySectionNeo(summaryData)
                                }
                            }
                            item { Spacer(modifier = Modifier.height(20.dp)) }
                            item {
                                Box(modifier = Modifier.background(CardWhite, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)).fillMaxWidth().padding(horizontal = 20.dp)) {
                                    RecentTransactionsHeaderNeo()
                                }
                            }
                            if (transactions.isEmpty()) {
                                item {
                                    Box(modifier = Modifier.background(CardWhite, RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)).fillMaxWidth().padding(20.dp), contentAlignment = Alignment.Center) {
                                        Text("Belum ada transaksi hari ini ✨", color = TextBody, fontWeight = FontWeight.Medium)
                                    }
                                }
                            } else {
                                items(transactions) { tx ->
                                    Box(modifier = Modifier.background(CardWhite).fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp)) {
                                        TransactionRowNeo(tx)
                                    }
                                }
                                item {
                                    Box(modifier = Modifier.background(CardWhite, RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)).fillMaxWidth().height(24.dp))
                                }
                            }
                        }
                    }
                } else {
                    // Mobile Portrait Mode (Original Layout)
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 120.dp), // Space for floating nav
                    ) {
                        item {
                            Box(modifier = Modifier.padding(top = 24.dp, start = 20.dp, end = 20.dp)) {
                                TopHeaderNeo(userProfile)
                            }
                        }
                        item {
                            Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp)) {
                                SearchSectionNeo(searchQuery, onQueryChange = { searchQuery = it })
                            }
                        }
                        item {
                            Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                                RevenueBannerNeo(revenueState)
                            }
                        }
                        item {
                            Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 28.dp)) {
                                ActionGridNeo(onTransaksiClick = { context.startActivity(Intent(context, TransactionActivity::class.java)) })
                            }
                        }
                        item {
                            Box(modifier = Modifier.background(CardWhite, RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)).fillMaxWidth().padding(horizontal = 20.dp, vertical = 24.dp)) {
                                SummarySectionNeo(summaryData)
                            }
                        }
                        item {
                            Box(modifier = Modifier.background(CardWhite).fillMaxWidth().padding(horizontal = 20.dp)) {
                                RecentTransactionsHeaderNeo()
                            }
                        }
                        if (transactions.isEmpty()) {
                            item {
                                Box(modifier = Modifier.background(CardWhite).fillMaxWidth().padding(20.dp), contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "Belum ada transaksi hari ini ✨",
                                        color = TextBody,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        } else {
                            items(transactions) { tx ->
                                Box(modifier = Modifier.background(CardWhite).fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp)) {
                                    TransactionRowNeo(tx)
                                }
                            }
                        }
                        // Fill remaining background white behind list
                        item {
                            Box(modifier = Modifier.background(CardWhite).fillMaxWidth().height(60.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SidebarNeoItem(icon: ImageVector, label: String, isSelected: Boolean) {
    Surface(
        color = if (isSelected) CardWhite else Color.Transparent,
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                icon,
                contentDescription = label,
                tint = if (isSelected) BrandBlue else Color.White.copy(alpha = 0.7f),
                modifier = Modifier.size(28.dp)
            )
            Text(label, color = if (isSelected) BrandBlue else Color.White.copy(alpha = 0.7f), fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun TopHeaderNeo(userProfile: UserProfile) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    userProfile.name.take(1).uppercase(),
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Column {
                Text(
                    text = "Halo, ${userProfile.name} 👋",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = userProfile.shopName,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }
        IconButton(
            onClick = { /* TODO */ },
            modifier = Modifier
                .background(Color.White.copy(alpha = 0.15f), CircleShape)
                .size(44.dp)
        ) {
            Icon(Icons.Rounded.Notifications, contentDescription = "Notifikasi", tint = Color.White)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchSectionNeo(query: String, onQueryChange: (String) -> Unit) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text("Cari transaksi berjalan...", color = Color.White.copy(alpha = 0.6f)) },
        leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null, tint = Color.White.copy(alpha = 0.8f)) },
        shape = RoundedCornerShape(20.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
            focusedBorderColor = Color.White,
            unfocusedContainerColor = Color.White.copy(alpha = 0.1f),
            focusedContainerColor = Color.White.copy(alpha = 0.2f),
            cursorColor = Color.White,
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White
        ),
        singleLine = true
    )
}

@Composable
fun RevenueBannerNeo(state: RevenueState) {
    Surface(
        shape = RoundedCornerShape(28.dp),
        color = CardWhite,
        modifier = Modifier.fillMaxWidth().offset(y = 10.dp),
        shadowElevation = 16.dp, // Premium soft shadow feeling natively
    ) {
        Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = if (state.isActive) Color(0xFFDCFCE7) else Color(0xFFFEE2E2),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(if (state.isActive) Color(0xFF16A34A) else Color(0xFFEF4444)))
                        Text("POS ${if(state.isActive) "Active" else "Offline"}", color = if(state.isActive) Color(0xFF166534) else Color(0xFF991B1B), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Text(state.activeHours, color = TextBody, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            }

            Column {
                Text("Pendapatan Hari Ini", color = TextBody, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        "Rp ${"%,d".format(state.amount).replace(',', '.')}",
                        color = TextHeadline,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Surface(
                        color = Color(0xFFEFF6FF),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            "+${state.trendPercentage}%",
                            color = BrandBlue,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ActionGridNeo(onTransaksiClick: () -> Unit = {}) {
    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
        Text("Layanan Utama", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextHeadline)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            PlayfulMenuIcon(Icons.Rounded.ShoppingCart, "Transaksi", BrandBlue, onClick = onTransaksiClick)
            PlayfulMenuIcon(Icons.Rounded.Menu, "Laporan", BrandBlue)
            PlayfulMenuIcon(Icons.Rounded.Star, "Produk", BrandBlue)
            PlayfulMenuIcon(Icons.Rounded.Build, "Printer", BrandBlue)
        }

        // Engaging massive CTA like modern promo cards
        Surface(
            modifier = Modifier.fillMaxWidth().height(100.dp),
            shape = RoundedCornerShape(24.dp),
            color = BrandBlue
        ) {
            val bgPattern = Brush.horizontalGradient(listOf(Color(0xFF3B82F6), Color(0xFF1E3A8A)))
            Row(
                modifier = Modifier.background(bgPattern).padding(20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(verticalArrangement = Arrangement.Center) {
                    Text("Buat Pesanan Baru", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text("Mulai transaksi dengan cepat", color = Color.White.copy(alpha = 0.8f), fontSize = 13.sp)
                }
                Box(
                    modifier = Modifier.size(56.dp).clip(CircleShape).background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Rounded.Add, contentDescription = "Add", tint = BrandBlue, modifier = Modifier.size(32.dp))
                }
            }
        }
    }
}

@Composable
fun PlayfulMenuIcon(icon: ImageVector, title: String, iconColor: Color, onClick: () -> Unit = {}) {
    Column(
        modifier = Modifier.clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = CardWhite,
            shadowElevation = 4.dp,
            modifier = Modifier.size(68.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = title, tint = iconColor, modifier = Modifier.size(32.dp))
            }
        }
        Text(title, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextHeadline)
    }
}

@Composable
fun SummarySectionNeo(data: SummaryData) {
    Column {
        Text("Ringkasan Performa", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextHeadline)
        Spacer(modifier = Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            NeoSummaryCard(
                modifier = Modifier.weight(1f),
                title = "Total Penjualan",
                value = data.totalOrders.toString(),
                icon = Icons.Rounded.Check,
                brandColor = BrandBlue
            )
            NeoSummaryCard(
                modifier = Modifier.weight(1f),
                title = "Item Terlaris",
                value = data.bestSellingProduct,
                icon = Icons.Rounded.Star,
                brandColor = AccentOrange
            )
        }
    }
}

@Composable
fun NeoSummaryCard(modifier: Modifier, title: String, value: String, icon: ImageVector, brandColor: Color) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        color = BgCanvas
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(icon, contentDescription = null, tint = brandColor, modifier = Modifier.size(20.dp))
                Text(title, color = TextBody, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            }
            Text(value, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = TextHeadline)
        }
    }
}

@Composable
fun RecentTransactionsHeaderNeo() {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Riwayat Transaksi", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextHeadline)
        TextButton(onClick = { /* TODO */ }) {
            Text("Semua", color = BrandBlue, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun TransactionRowNeo(tx: TransactionItem) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier.size(52.dp).clip(CircleShape).background(BgCanvas),
            contentAlignment = Alignment.Center
        ) {
            Text(tx.customerName.take(1).uppercase(), color = TextHeadline, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
        }
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(tx.customerName, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextHeadline)
            Text("${tx.id} • ${tx.time}", color = TextBody, fontSize = 13.sp)
        }
        Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("Rp ${"%,d".format(tx.amount).replace(',', '.')}", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextHeadline)
            Surface(
                color = tx.status.color,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    tx.status.label,
                    color = tx.status.textColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
fun AppBottomNavigationNeo() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .navigationBarsPadding(),
        contentAlignment = Alignment.BottomCenter
    ) {
        Surface(
            shape = RoundedCornerShape(32.dp),
            color = CardWhite,
            shadowElevation = 16.dp,
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 24.dp).fillMaxWidth().height(80.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BottomNavItem(Icons.Rounded.Home, "Beranda", true)
                BottomNavItem(Icons.Rounded.Menu, "Riwayat", false)

                // Spacer for massive immersive scan button to prevent overlap
                Spacer(modifier = Modifier.width(60.dp))

                BottomNavItem(Icons.Rounded.Info, "Statistik", false)
                BottomNavItem(Icons.Rounded.Person, "Profil", false)
            }
        }

        // Massive immersive scan button placed outside Surface to avoid clipping
        Box(
            modifier = Modifier
                .padding(bottom = 28.dp)
                .size(64.dp)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(listOf(BrandBlue, BrandCyan))
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Rounded.Search, contentDescription = "Scan", tint = Color.White, modifier = Modifier.size(28.dp))
        }
    }
}

@Composable
fun BottomNavItem(icon: ImageVector, label: String, isSelected: Boolean) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            icon,
            contentDescription = label,
            tint = if (isSelected) BrandBlue else TextBody.copy(alpha = 0.5f),
            modifier = Modifier.size(28.dp)
        )
        if (isSelected) {
            Box(modifier = Modifier.size(4.dp).clip(CircleShape).background(BrandBlue))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    AplicashTheme {
        HomeScreen(
            userProfile = UserProfile("Juragan", "Toko Kelontong Berkah"),
            revenueState = RevenueState(2450000L, 12, true, "08:00 - 22:00"),
            summaryData = SummaryData(128, "Kopi Susu Aren"),
            transactions = listOf(
                TransactionItem("ORD-001", "Budi Santoso", 45000, "10:45", TransactionStatus.COMPLETED),
                TransactionItem("ORD-002", "Siti Aminah", 120000, "10:30", TransactionStatus.PENDING)
            )
        )
    }
}