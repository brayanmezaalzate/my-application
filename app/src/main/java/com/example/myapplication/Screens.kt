package com.example.myapplication

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley

// --- 1. PANTALLA PRINCIPAL (HOME) ---
@Composable
fun HomeScreen(navController: NavController, viewModel: TaskViewModel) {

    val context = LocalContext.current
    var consejo by remember { mutableStateOf("Cargando consejo del día...") }

    LaunchedEffect(Unit) {
        val queue = Volley.newRequestQueue(context)
        val url = "https://dummyjson.com/quotes/random"

        val request = JsonObjectRequest(
            Request.Method.GET,
            url,
            null,
            { response ->
                consejo = response.getString("quote")
            },
            {
                consejo = "Recuerda cuidar tu salud todos los días"
            }
        )

        queue.add(request)
    }

    val tasks by viewModel.localTasks.collectAsState()

    val totalTareas = tasks.size
    val completadas = tasks.count { it["completed"] == "1" || it["completed"] == "true" }
    val pendientes = totalTareas - completadas

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Gestión de Tareas", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Text(
                text = "Consejo del día: $consejo",
                modifier = Modifier.padding(16.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Resumen de Tareas", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("$totalTareas", style = MaterialTheme.typography.titleLarge)
                        Text("Total", style = MaterialTheme.typography.bodySmall)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("$pendientes", style = MaterialTheme.typography.titleLarge, color = Color(0xFFD32F2F))
                        Text("Pendientes", style = MaterialTheme.typography.bodySmall)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("$completadas", style = MaterialTheme.typography.titleLarge, color = Color(0xFF388E3C))
                        Text("Completadas", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(onClick = { navController.navigate("create_step_1") }, modifier = Modifier.fillMaxWidth()) {
            Text("Crear Nueva Tarea")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { navController.navigate("local_tasks") }, modifier = Modifier.fillMaxWidth()) {
            Text("Ver Mis Tareas")
        }
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedButton(onClick = { navController.navigate("api_tasks") }, modifier = Modifier.fillMaxWidth()) {
            Text("Tareas de la API")
        }
    }
}

// --- 2. PANTALLA CREAR TAREA - PASO 1 ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTaskStep1Screen(navController: NavController, viewModel: TaskViewModel) {
    var title by remember { mutableStateOf(viewModel.draftTitle) }
    var description by remember { mutableStateOf(viewModel.draftDescription) }
    var expanded by remember { mutableStateOf(false) }
    val priorities = listOf("Alta", "Media", "Baja")
    var selectedPriority by remember { mutableStateOf(viewModel.draftPriority) }

    Column(modifier = Modifier.padding(16.dp).fillMaxSize()) {
        Text("Paso 1: Detalles de la Tarea", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = title,
            onValueChange = { title = it; viewModel.draftTitle = it },
            label = { Text("Título") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = description,
            onValueChange = { description = it; viewModel.draftDescription = it },
            label = { Text("Descripción") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
            OutlinedTextField(
                value = selectedPriority,
                onValueChange = {},
                readOnly = true,
                label = { Text("Prioridad") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                priorities.forEach { selection ->
                    DropdownMenuItem(
                        text = { Text(selection) },
                        onClick = {
                            selectedPriority = selection
                            viewModel.draftPriority = selection
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = { navController.navigate("create_step_2") },
            enabled = title.isNotBlank() && description.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Siguiente")
        }
    }
}

// --- 3. PANTALLA CREAR TAREA - PASO 2 ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTaskStep2Screen(navController: NavController, viewModel: TaskViewModel) {
    val datePickerState = rememberDatePickerState()
    var errorMessage by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp).fillMaxSize()) {
        Text("Paso 2: Fecha Límite", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))

        DatePicker(state = datePickerState, modifier = Modifier.weight(1f))

        if (errorMessage.isNotEmpty()) {
            Text(errorMessage, color = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.height(8.dp))
        }

        Button(onClick = {
            val selectedDate = datePickerState.selectedDateMillis
            if (selectedDate == null) {
                errorMessage = "Por favor selecciona una fecha"
            } else if (selectedDate < System.currentTimeMillis() - 86400000) {
                errorMessage = "La fecha no puede ser en el pasado"
            } else {
                val formattedDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(selectedDate))

                viewModel.saveTask(
                    viewModel.draftTitle,
                    viewModel.draftDescription,
                    viewModel.draftPriority,
                    formattedDate
                )

                viewModel.draftTitle = ""
                viewModel.draftDescription = ""
                viewModel.draftPriority = "Media"

                navController.navigate("home") {
                    popUpTo("home") { inclusive = true }
                }
            }
        }, modifier = Modifier.fillMaxWidth()) {
            Text("Guardar Tarea")
        }
    }
}
// --- 4. PANTALLA LISTA TAREAS LOCALES ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocalTasksScreen(navController: NavController, viewModel: TaskViewModel) {
    val tasksLocal by viewModel.localTasks.collectAsState()
    var filterState by remember { mutableStateOf("Todos") }

    val filteredTasks = tasksLocal.filter { task ->
        val isCompleted = task["completed"] == "1" || task["completed"] == "true"
        when (filterState) {
            "Pendientes" -> !isCompleted
            "Completadas" -> isCompleted
            else -> true
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("create_step_1") }) {
                Text("+", style = MaterialTheme.typography.headlineMedium)
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text("Mis Tareas Locales", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                FilterChip(
                    selected = filterState == "Todos",
                    onClick = { filterState = "Todos" },
                    label = { Text("Todos") }
                )
                FilterChip(
                    selected = filterState == "Pendientes",
                    onClick = { filterState = "Pendientes" },
                    label = { Text("Pendientes") }
                )
                FilterChip(
                    selected = filterState == "Completadas",
                    onClick = { filterState = "Completadas" },
                    label = { Text("Completadas") }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn {
                items(filteredTasks) { task ->
                    val cardColor = when (task["priority"]) {
                        "Alta" -> Color(0xFFFFCDD2)
                        "Media" -> Color(0xFFFFF9C4)
                        "Baja" -> Color(0xFFC8E6C9)
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = cardColor)
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text(
                                "Título: ${task["title"]}",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text("Prioridad: ${task["priority"]}")
                            Text("Fecha: ${task["dueDate"]}")

                            val isCompleted = task["completed"] == "1" || task["completed"] == "true"
                            Text("Estado: ${if (isCompleted) "Completada ✅" else "Pendiente ⏳"}")

                            Row(modifier = Modifier.padding(top = 8.dp)) {
                                Button(
                                    onClick = { viewModel.deleteTask(task["id"]!!.toInt()) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.error
                                    )
                                ) {
                                    Text("Eliminar")
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                Button(onClick = {
                                    viewModel.draftTitle = task["title"] ?: ""
                                    viewModel.draftDescription = task["description"] ?: ""
                                    viewModel.draftPriority = task["priority"] ?: "Media"
                                    viewModel.draftIdToEdit = task["id"]!!.toInt()
                                    navController.navigate("edit_task")
                                }) {
                                    Text("Editar")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- 5. PANTALLA EDITAR TAREA ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTaskScreen(navController: NavController, viewModel: TaskViewModel) {
    var title by remember { mutableStateOf(viewModel.draftTitle) }
    var description by remember { mutableStateOf(viewModel.draftDescription) }
    var expanded by remember { mutableStateOf(false) }
    val priorities = listOf("Alta", "Media", "Baja")
    var selectedPriority by remember { mutableStateOf(viewModel.draftPriority) }

    Column(modifier = Modifier.padding(16.dp).fillMaxSize()) {
        Text(
            "Modificar Tarea (Formulario Pre-llenado)",
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Título") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Descripción") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedPriority,
                onValueChange = {},
                readOnly = true,
                label = { Text("Prioridad") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded)
                },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                priorities.forEach { selection ->
                    DropdownMenuItem(
                        text = { Text(selection) },
                        onClick = {
                            selectedPriority = selection
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedButton(
                onClick = {
                    viewModel.draftIdToEdit = null
                    navController.popBackStack()
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Cancelar")
            }

            Spacer(modifier = Modifier.width(16.dp))

            Button(
                onClick = {
                    viewModel.saveTask(
                        title,
                        description,
                        selectedPriority,
                        "Actualizada"
                    )
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = true }
                    }
                },
                enabled = title.isNotBlank() && description.isNotBlank(),
                modifier = Modifier.weight(1f)
            ) {
                Text("Guardar")
            }
        }
    }
}

// --- 6. PANTALLA TAREAS API ---
@Composable
fun ApiTasksScreen(navController: NavController, viewModel: TaskViewModel) {
    val apiState by viewModel.apiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchApiTasks()
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Tareas desde JSONPlaceholder", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))

        when (apiState) {
            is ApiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is ApiState.Error -> {
                val errorMessage = (apiState as ApiState.Error).message
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            errorMessage,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { viewModel.fetchApiTasks() }) {
                            Text("Reintentar")
                        }
                    }
                }
            }

            is ApiState.Success -> {
                val tasks = (apiState as ApiState.Success).tasks

                LazyColumn {
                    items(tasks) { apiTask ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        apiTask.title,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Text(
                                        "Estado API: ${if (apiTask.completed) "Completada" else "Pendiente"}",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                Button(onClick = {
                                    viewModel.saveApiTaskLocally(apiTask)
                                    navController.navigate("local_tasks")
                                }) {
                                    Text("Guardar")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}