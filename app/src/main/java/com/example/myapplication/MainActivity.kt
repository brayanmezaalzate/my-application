package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val viewModel: TaskViewModel = viewModel()

                    // El NavHost define qué pantalla mostrar según la "ruta" actual
                    NavHost(navController = navController, startDestination = "home") {

                        // 1. Pantalla Principal
                        composable("home") { HomeScreen(navController, viewModel) }

                        // 2. Crear Tarea - Paso 1 (Detalles)
                        composable("create_step_1") {
                            CreateTaskStep1Screen(navController, viewModel)
                        }

                        // 3. Crear Tarea - Paso 2 (Fecha)
                        composable("create_step_2") {
                            CreateTaskStep2Screen(navController, viewModel)
                        }

                        // 4. Lista de Tareas Locales
                        composable("local_tasks") {
                            LocalTasksScreen(navController, viewModel)
                        }

                        // 5. Pantalla Tareas API
                        composable("api_tasks") {
                            ApiTasksScreen(navController, viewModel)
                        }

                        // 6. Pantalla Editar Tarea
                        composable("edit_task") {
                            EditTaskScreen(navController, viewModel)
                        }
                    }
                }
            }
        }
    }
}