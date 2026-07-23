package com.example.myapplication
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.myapplication.network.RetrofitInstance
import com.example.myapplication.model.Task
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TaskViewModel(application: Application) : AndroidViewModel(application) {

    // Instanciamos tu base de datos aquí
    private val db = TaskDatabaseHelper(application)

    // Estado reactivo para tus tareas locales
    private val _localTasks = MutableStateFlow<List<Map<String, String>>>(emptyList())
    val localTasks: StateFlow<List<Map<String, String>>> = _localTasks

    // --- VARIABLES TEMPORALES AÑADIDAS ---
    // Sirven para recordar los datos mientras saltas de la pantalla Paso 1 al Paso 2,
    // o cuando tocas el botón de "Editar".
    var draftTitle = ""
    var draftDescription = ""
    var draftPriority = "Media"
    var draftIdToEdit: Int? = null

    init {
        loadLocalTasks()
    }

    // Método para cargar tareas desde tu SQLite
    fun loadLocalTasks() {
        viewModelScope.launch {
            val tasks = withContext(Dispatchers.IO) {
                db.getTasks()
            }
            _localTasks.value = tasks
        }
    }

    // Método para guardar o actualizar una tarea
    fun saveTask(title: String, description: String, priority: String, dueDate: String) {
        viewModelScope.launch(Dispatchers.IO) {

            if (draftIdToEdit == null) {
                // Es una tarea nueva
                db.insertTask(title, description, priority, dueDate)
            } else {
                // Es una tarea existente que estamos editando
                // Se envía 0 asumiendo que el estado completado es false por defecto
                db.updateTask(draftIdToEdit!!, title, description, priority, dueDate, 0)

                // Limpiamos el ID después de actualizar para que la próxima vez cree una nueva
                draftIdToEdit = null
            }

            loadLocalTasks() // Actualizamos la lista automáticamente para la UI
        }
    }

    // Método para eliminar
    fun deleteTask(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            db.deleteTask(id)
            loadLocalTasks()
        }
    }
    // --- ESTADOS DE LA API ---
    // Usamos un StateFlow para manejar el estado de la pantalla (Loading, Success, Error)
    private val _apiState = MutableStateFlow<ApiState>(ApiState.Loading)
    val apiState: StateFlow<ApiState> = _apiState

    // Método para consumir la API
    fun fetchApiTasks() {
        viewModelScope.launch {
            _apiState.value = ApiState.Loading
            try {
                // Traemos las tareas y tomamos solo las primeras 20 para no saturar la lista
                val tasks = RetrofitInstance.api.getTasks().take(20)
                _apiState.value = ApiState.Success(tasks)
            } catch (e: Exception) {
                _apiState.value = ApiState.Error("Error de conexión. Verifica tu internet.")
            }
        }
    }

    // Método para guardar una tarea de la API a tu base de datos local
    fun saveApiTaskLocally(apiTask: Task) {
        // Le asignamos la fecha de hoy automáticamente
        val today = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

        saveTask(
            title = apiTask.title,
            description = "Tarea importada desde JSONPlaceholder",
            priority = "Media",
            dueDate = today
        )
    }
}
// Sellamos la clase para manejar los 3 estados exigidos por la rúbrica
sealed class ApiState {
    object Loading : ApiState()
    data class Success(val tasks: List<Task>) : ApiState()
    data class Error(val message: String) : ApiState()
}