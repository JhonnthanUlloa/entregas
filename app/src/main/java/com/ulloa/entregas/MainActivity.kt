
package com.ulloa.entregas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.ulloa.entregas.ui.theme.AppEntregasTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppEntregasTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

// --- NAVIGATION ---
sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Main : Screen("main")
    object Dashboard : Screen("dashboard")
    object EncomiendasList : Screen("encomiendas_list")
    object Profile : Screen("profile")
    object Search : Screen("search")
    object Assign : Screen("assign")
    object CentroAcopio : Screen("centro_acopio")
    object ConsultarMensajero : Screen("consultar_mensajero")
    object Redigitalizar : Screen("redigitalizar")
    object EncomiendaDetail : Screen("encomienda_detail/{encomiendaId}") {
        fun createRoute(encomiendaId: String) = "encomienda_detail/$encomiendaId"
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.Login.route) {
        composable(Screen.Login.route) {
            LoginScreen(navController = navController)
        }
        composable(Screen.Main.route) {
            MainScreen(rootNavController = navController)
        }
        composable(Screen.Search.route) { SearchScreen(navController) }
        composable(Screen.Assign.route) { AssignScreen(navController) }
        composable(Screen.CentroAcopio.route) { CentroAcopioScreen(navController) }
        composable(Screen.ConsultarMensajero.route) { ConsultarMensajeroScreen(navController) }
        composable(Screen.Redigitalizar.route) { RedigitalizarScreen(navController) }
        composable(Screen.EncomiendaDetail.route) { backStackEntry ->
            val encomiendaId = backStackEntry.arguments?.getString("encomiendaId")
            EncomiendaDetailScreen(navController = navController, encomiendaId = encomiendaId ?: "N/A")
        }
    }
}

// --- DUMMY DATA MODELS ---
data class DashboardMetric(val title: String, val value: String, val icon: ImageVector)
data class EncomiendaItem(val id: String, val direccion: String, val tipo: String, val estado: String)

// Íconos corregidos con referencias que existen en la librería básica
val dummyMetrics = listOf(
    DashboardMetric("Descargadas", "150", Icons.Default.ArrowDownward), // Reemplazo para FileDownload
    DashboardMetric("Abiertas", "35", Icons.Default.Star),          // Reemplazo para Folder
    DashboardMetric("Cerradas", "115", Icons.Default.CheckCircle),
    DashboardMetric("Pendientes", "5", Icons.Default.Refresh)
)

val dummyEncomiendas = listOf(
    EncomiendaItem("ENV-001", "Calle Falsa 123, Springfield", "Entrega Domicilio", "Abierta"),
    EncomiendaItem("ENV-002", "Av. Siempreviva 742, Capital", "Venta Público", "Abierta"),
    EncomiendaItem("ENV-003", "Carrera 8 #10-20, Centro", "Recolección", "Cerrada"),
    EncomiendaItem("ENV-004", "Transversal 5, Apto 101", "Entrega Domicilio", "Pendiente")
)

// --- UI SCREENS ---

@Composable
fun LoginScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Home,
            contentDescription = "Logo de la App",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(100.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Gestión de Envíos",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo Electrónico") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = { navController.navigate(Screen.Main.route) {
                popUpTo(Screen.Login.route) { inclusive = true }
            } },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Iniciar Sesión")
        }
    }
}

@Composable
fun MainScreen(rootNavController: NavHostController) {
    val bottomNavController = rememberNavController()
    Scaffold(
        bottomBar = { AppBottomNavigation(navController = bottomNavController) }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            BottomNavGraph(
                bottomNavController = bottomNavController,
                rootNavController = rootNavController
            )
        }
    }
}

@Composable
fun AppBottomNavigation(navController: NavHostController) {
    val items = listOf(
        Screen.Dashboard,
        Screen.EncomiendasList,
        Screen.Profile
    )
    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { screen ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = when (screen) {
                            Screen.Dashboard -> Icons.Default.Apps // Reemplazo para GridView
                            Screen.EncomiendasList -> Icons.AutoMirrored.Filled.List
                            Screen.Profile -> Icons.Default.AccountCircle
                            else -> Icons.Default.Warning
                        },
                        contentDescription = null
                    )
                },
                label = {
                    Text(
                        when (screen) {
                            Screen.Dashboard -> "Dashboard"
                            Screen.EncomiendasList -> "Envíos"
                            Screen.Profile -> "Perfil"
                            else -> ""
                        }
                    )
                },
                selected = currentRoute == screen.route,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

@Composable
fun BottomNavGraph(bottomNavController: NavHostController, rootNavController: NavHostController) {
    NavHost(navController = bottomNavController, startDestination = Screen.Dashboard.route) {
        composable(Screen.Dashboard.route) { DashboardScreen(navController = rootNavController) }
        composable(Screen.EncomiendasList.route) { EncomiendasListScreen(navController = rootNavController) }
        composable(Screen.Profile.route) { ProfileScreen(navController = rootNavController) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dashboard") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    "Hola, Mensajero!",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Aquí está el resumen de tu jornada:",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    MetricCard(metric = dummyMetrics[0], modifier = Modifier.weight(1f))
                    MetricCard(metric = dummyMetrics[1], modifier = Modifier.weight(1f))
                }
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    MetricCard(metric = dummyMetrics[2], modifier = Modifier.weight(1f))
                    MetricCard(metric = dummyMetrics[3], modifier = Modifier.weight(1f))
                }
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }
            item {
                ActionButton(
                    text = "Entregar Encomiendas",
                    icon = Icons.AutoMirrored.Filled.List,
                    onClick = { navController.navigate(Screen.EncomiendasList.route) }
                )
            }
            item {
                ActionButton(
                    text = "Descargar Nuevas",
                    icon = Icons.Default.ArrowDownward, // Reemplazo para FileDownload
                    onClick = { /* TODO: Logic */ }
                )
            }
            item {
                ActionButton(
                    text = "Buscar Encomiendas",
                    icon = Icons.Default.Search,
                    onClick = { navController.navigate(Screen.Search.route) }
                )
            }
            item {
                ActionButton(
                    text = "Asignar Encomienda",
                    icon = Icons.Default.Assignment,
                    onClick = { navController.navigate(Screen.Assign.route) }
                )
            }
            item {
                ActionButton(
                    text = "Centro de Acopio",
                    icon = Icons.Default.LocalShipping,
                    onClick = { navController.navigate(Screen.CentroAcopio.route) }
                )
            }
            item {
                ActionButton(
                    text = "Consultar Mensajero",
                    icon = Icons.Default.PersonSearch,
                    onClick = { navController.navigate(Screen.ConsultarMensajero.route) }
                )
            }
            item {
                ActionButton(
                    text = "Redigitalizar",
                    icon = Icons.Default.Edit,
                    onClick = { navController.navigate(Screen.Redigitalizar.route) }
                )
            }
        }
    }
}

@Composable
fun MetricCard(metric: DashboardMetric, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(metric.icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))
            Text(metric.value, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text(metric.title, style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun ActionButton(text: String, icon: ImageVector, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(ButtonDefaults.IconSize))
        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
        Text(text)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EncomiendasListScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Encomiendas") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(dummyEncomiendas) { encomienda ->
                EncomiendaItemCard(
                    item = encomienda,
                    onClick = {
                        navController.navigate(Screen.EncomiendaDetail.createRoute(encomienda.id))
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EncomiendaItemCard(item: EncomiendaItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.AllInbox, // Reemplazo para Inbox
                contentDescription = "Encomienda",
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(item.id, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Text(item.direccion, style = MaterialTheme.typography.bodyMedium)
                Text(item.tipo, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
            }
            Spacer(modifier = Modifier.width(16.dp))
            SuggestionChip(
                onClick = {},
                label = { Text(item.estado) },
                colors = SuggestionChipDefaults.suggestionChipColors(
                    containerColor = when (item.estado) {
                        "Abierta" -> MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                        "Cerrada" -> MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f)
                        else -> Color.Gray.copy(alpha = 0.5f)
                    }
                )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EncomiendaDetailScreen(navController: NavController, encomiendaId: String) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle Envío #$encomiendaId") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item { DetailSection(title = "Información General", icon = Icons.Default.Info) {
                Text("Origen: Bodega Principal")
                Text("Destino: Cliente Final")
                Text("Dirección: Calle Falsa 123, Springfield")
                Text("Teléfono: 300 123 4567")
            }}

            item { DetailSection(title = "Estado de Entrega", icon = Icons.Default.Edit) {
                var entregado by remember { mutableStateOf(false) }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("El envío fue entregado", modifier = Modifier.weight(1f))
                    Switch(checked = entregado, onCheckedChange = { entregado = it })
                }
                Spacer(Modifier.height(8.dp))
                if (entregado) {
                    OutlinedTextField(value = "", onValueChange = {}, label = { Text("Nombre de quien recibe") }, modifier = Modifier.fillMaxWidth())
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(value = "", onValueChange = {}, label = { Text("Correo electrónico") }, modifier = Modifier.fillMaxWidth())
                } else {
                    OutlinedTextField(value = "", onValueChange = {}, label = { Text("Causal de devolución") }, modifier = Modifier.fillMaxWidth())
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(value = "", onValueChange = {}, label = { Text("Sub-causal") }, modifier = Modifier.fillMaxWidth())
                }
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value = "", onValueChange = {}, label = { Text("Observación") }, modifier = Modifier.fillMaxWidth())
            }}

            item { DetailSection(title = "Evidencia", icon = Icons.Default.Camera) { // Reemplazo para PhotoCamera
                Button(onClick = { /* TODO: Open Camera */ }, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Default.AddCircle, contentDescription = null)
                    Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                    Text("Adicionar Imagen")
                }
            }}

            item {
                Button(
                    onClick = { navController.navigateUp() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text("Guardar Entrega o Envío")
                }
            }
        }
    }
}

@Composable
fun DetailSection(title: String, icon: ImageVector, content: @Composable ColumnScope.() -> Unit) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(8.dp))
            Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
        Column(
            modifier = Modifier.padding(start = 0.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            content = content
        )
    }
}

@Composable
fun ProfileScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Perfil del Usuario", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = {
            navController.navigate(Screen.Login.route) {
                popUpTo(navController.graph.id) { inclusive = true }
            }
        }) {
            Text("Cerrar Sesión")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(navController: NavController) {
    Scaffold(topBar = {
        TopAppBar(title = { Text("Buscar Encomiendas") }, navigationIcon = {
            IconButton(onClick = { navController.navigateUp() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
            }
        })
    }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(value = "", onValueChange = {}, label = { Text("Desde") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = "", onValueChange = {}, label = { Text("Hasta") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = "", onValueChange = {}, label = { Text("Estado") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = "", onValueChange = {}, label = { Text("Buscar") }, modifier = Modifier.fillMaxWidth())
            Button(onClick = { /* TODO */ }, modifier = Modifier.fillMaxWidth()) {
                Text("Buscar")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssignScreen(navController: NavController) {
    Scaffold(topBar = {
        TopAppBar(title = { Text("Asignar Encomienda") }, navigationIcon = {
            IconButton(onClick = { navController.navigateUp() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
            }
        })
    }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(value = "", onValueChange = {}, label = { Text("Código Encomienda") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = "", onValueChange = {}, label = { Text("Nombre CAF") }, modifier = Modifier.fillMaxWidth())
            Button(onClick = { /* TODO */ }, modifier = Modifier.fillMaxWidth()) {
                Text("Aceptar")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CentroAcopioScreen(navController: NavController) {
    Scaffold(topBar = {
        TopAppBar(title = { Text("Entregar a Centro de Acopio") }, navigationIcon = {
            IconButton(onClick = { navController.navigateUp() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
            }
        })
    }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(value = "", onValueChange = {}, label = { Text("Código Encomienda") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = "", onValueChange = {}, label = { Text("Nombre CAF") }, modifier = Modifier.fillMaxWidth())
            Button(onClick = { /* TODO */ }, modifier = Modifier.fillMaxWidth()) {
                Text("Aceptar")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConsultarMensajeroScreen(navController: NavController) {
    Scaffold(topBar = {
        TopAppBar(title = { Text("Consultar Mensajero") }, navigationIcon = {
            IconButton(onClick = { navController.navigateUp() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
            }
        })
    }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(value = "", onValueChange = {}, label = { Text("Código Encomienda") }, modifier = Modifier.fillMaxWidth())
            Button(onClick = { /* TODO */ }, modifier = Modifier.fillMaxWidth()) {
                Text("Buscar")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RedigitalizarScreen(navController: NavController) {
    Scaffold(topBar = {
        TopAppBar(title = { Text("Redigitalizar") }, navigationIcon = {
            IconButton(onClick = { navController.navigateUp() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
            }
        })
    }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(value = "", onValueChange = {}, label = { Text("Código Encomienda") }, modifier = Modifier.fillMaxWidth())
            Button(onClick = { /* TODO */ }, modifier = Modifier.fillMaxWidth()) {
                Text("Guardar")
            }
        }
    }
}

@Preview(showBackground = true, name = "Login Screen Light")
@Composable
fun DefaultPreview() {
    AppEntregasTheme(darkTheme = false) {
        LoginScreen(rememberNavController())
    }
}

@Preview(showBackground = true, name = "Dashboard Screen Dark")
@Composable
fun DashboardPreview() {
    AppEntregasTheme(darkTheme = true) {
        val navController = rememberNavController()
        Scaffold(
            bottomBar = { AppBottomNavigation(navController = navController) }
        ) { padding ->
            Box(modifier = Modifier.padding(padding)) {
                DashboardScreen(navController = navController)
            }
        }
    }
}
