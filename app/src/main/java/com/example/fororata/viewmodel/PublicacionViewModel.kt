package com.example.fororata.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.fororata.data.db.Comentario
import com.example.fororata.data.db.Publicacion
import com.example.fororata.data.repository.PublicacionRepository
import com.example.fororata.model.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class PublicacionViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = PublicacionRepository(application.applicationContext)

    // Estado del formulario (para crear/editar)
    private val _estado = MutableStateFlow(PublicacionUIState())
    val estado: StateFlow<PublicacionUIState> = _estado.asStateFlow()

    // Lista de publicaciones
    private val _publicaciones = MutableStateFlow<List<Publicacion>>(emptyList())
    val publicaciones: StateFlow<List<Publicacion>> = _publicaciones.asStateFlow()

    // Publicación seleccionada para ver detalles
    private val _publicacionSeleccionada = MutableStateFlow<Publicacion?>(null)
    val publicacionSeleccionada: StateFlow<Publicacion?> = _publicacionSeleccionada.asStateFlow()

    // Comentarios de la publicación seleccionada
    private val _comentarios = MutableStateFlow<List<Comentario>>(emptyList())
    val comentarios: StateFlow<List<Comentario>> = _comentarios.asStateFlow()

    // Estadísticas de la publicación seleccionada
    private val _estadisticas = MutableStateFlow<PublicacionConEstadisticas?>(null)
    val estadisticas: StateFlow<PublicacionConEstadisticas?> = _estadisticas.asStateFlow()

    init {
        cargarPublicaciones()
    }

    // Cargar todas las publicaciones
    fun cargarPublicaciones() {
        viewModelScope.launch {
            repository.obtenerTodasPublicaciones().collect { lista ->
                _publicaciones.value = lista
            }
        }
    }

    // Cargar publicación con sus comentarios y estadísticas
    fun cargarPublicacionConDetalles(id: Int) {
        viewModelScope.launch {
            val publicacion = repository.obtenerPublicacionPorId(id)
            _publicacionSeleccionada.value = publicacion

            if (publicacion != null) {
                // Cargar estadísticas
                _estadisticas.value = repository.obtenerEstadisticasPublicacion(id)

                // Cargar comentarios
                repository.obtenerComentariosFlow(id).collect { lista ->
                    _comentarios.value = lista
                }
            }
        }
    }

    // Cambios en el formulario
    fun onTituloChange(valor: String) {
        _estado.update {
            it.copy(titulo = valor, errores = it.errores.copy(titulo = null))
        }
    }

    fun onContenidoChange(valor: String) {
        _estado.update { currentState ->
            val comentarios = currentState.comentarios.toMutableList()
            if (comentarios.isEmpty()) {
                comentarios.add(ComentariosUIState(texto = valor))
            } else {
                comentarios[0] = comentarios[0].copy(texto = valor)
            }
            currentState.copy(comentarios = comentarios)
        }
    }

    // Crear nueva publicación (SIN estrellas)
    fun crearPublicacion(autorId: Int, onResultado: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            if (!validarPublicacion()) {
                onResultado(false, "Por favor completa todos los campos correctamente")
                return@launch
            }

            val contenido = _estado.value.comentarios.firstOrNull()?.texto ?: ""
            val publicacion = Publicacion(
                titulo = _estado.value.titulo,
                contenido = contenido,
                autorId = autorId
            )

            val id = repository.crearPublicacion(publicacion)
            if (id > 0) {
                limpiarFormulario()
                onResultado(true, "Publicación creada exitosamente")
            } else {
                onResultado(false, "Error al crear la publicación")
            }
        }
    }

    // Cargar publicación para editar
    fun cargarPublicacionParaEditar(id: Int) {
        viewModelScope.launch {
            val publicacion = repository.obtenerPublicacionPorId(id)
            if (publicacion != null) {
                _publicacionSeleccionada.value = publicacion
                _estado.value = PublicacionUIState(
                    id = publicacion.id,
                    titulo = publicacion.titulo,
                    comentarios = mutableListOf(
                        ComentariosUIState(texto = publicacion.contenido)
                    )
                )
            }
        }
    }

    // Actualizar publicación existente
    fun actualizarPublicacion(onResultado: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            if (!validarPublicacion()) {
                onResultado(false, "Por favor completa todos los campos correctamente")
                return@launch
            }

            val publicacionActual = _publicacionSeleccionada.value
            if (publicacionActual == null) {
                onResultado(false, "No hay publicación seleccionada")
                return@launch
            }

            val contenido = _estado.value.comentarios.firstOrNull()?.texto ?: ""
            val publicacionActualizada = publicacionActual.copy(
                titulo = _estado.value.titulo,
                contenido = contenido,
                fechaModificacion = System.currentTimeMillis()
            )

            val exito = repository.actualizarPublicacion(publicacionActualizada)
            if (exito) {
                limpiarFormulario()
                _publicacionSeleccionada.value = null
                onResultado(true, "Publicación actualizada exitosamente")
            } else {
                onResultado(false, "Error al actualizar la publicación")
            }
        }
    }

    // Eliminar publicación
    fun eliminarPublicacion(publicacion: Publicacion, onResultado: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val exito = repository.eliminarPublicacion(publicacion)
            if (exito) {
                onResultado(true, "Publicación eliminada exitosamente")
            } else {
                onResultado(false, "Error al eliminar la publicación")
            }
        }
    }

    // === COMENTARIOS CON CALIFICACIÓN ===

    // Agregar comentario con estrellas
    fun agregarComentarioConCalificacion(
        publicacionId: Int,
        autorId: Int,
        texto: String,
        estrellas: Int,
        onResultado: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            if (texto.isBlank()) {
                onResultado(false, "El comentario no puede estar vacío")
                return@launch
            }

            if (estrellas < 1 || estrellas > 5) {
                onResultado(false, "Debes seleccionar entre 1 y 5 estrellas")
                return@launch
            }

            val comentario = Comentario(
                publicacionId = publicacionId,
                autorId = autorId,
                texto = texto,
                estrellas = estrellas
            )

            val id = repository.agregarComentario(comentario)
            if (id > 0) {
                // Recargar estadísticas
                _estadisticas.value = repository.obtenerEstadisticasPublicacion(publicacionId)
                onResultado(true, "Comentario agregado exitosamente")
            } else {
                onResultado(false, "Error al agregar el comentario")
            }
        }
    }

    // Validar formulario de publicación
    fun validarPublicacion(): Boolean {
        val estado = _estado.value
        val contenido = estado.comentarios.firstOrNull()?.texto ?: ""

        val errores = PublicacionErrores(
            titulo = if (estado.titulo.isBlank()) "Título obligatorio" else null
        )

        val hayErrores = errores.titulo != null || contenido.isBlank()

        if (contenido.isBlank()) {
            _estado.update {
                it.copy(
                    errores = errores.copy(
                        comentarios = mutableListOf(
                            ComentariosErrores(texto = "El contenido es obligatorio")
                        )
                    )
                )
            }
        } else {
            _estado.update { it.copy(errores = errores) }
        }

        return !hayErrores
    }

    // Limpiar formulario
    fun limpiarFormulario() {
        _estado.value = PublicacionUIState()
        _publicacionSeleccionada.value = null
    }
}