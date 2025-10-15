package com.example.fororata.viewmodel

import androidx.lifecycle.ViewModel
import com.example.fororata.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class PublicacionViewModel : ViewModel() {

    private val _estado = MutableStateFlow(PublicacionUIState())
    val estado: StateFlow<PublicacionUIState> = _estado

    fun onTituloChange(valor: String) {
        _estado.update {
            it.copy(titulo = valor, errores = it.errores.copy(titulo = null))
        }
    }

    fun onEstrellasChange(valor: Int) {
        _estado.update {
            it.copy(estrellas = valor, errores = it.errores.copy(estrellas = null))
        }
    }

    fun agregarComentario(texto: String) {
        val nuevoComentario = ComentariosUIState(
            id = _estado.value.comentarios.size + 1,
            texto = texto,
            estrellasComentario = 0
        )

        _estado.update { actual ->
            val nuevaLista = actual.comentarios.toMutableList()
            nuevaLista.add(nuevoComentario)
            actual.copy(comentarios = nuevaLista)
        }
    }

    fun actualizarComentario(index: Int, nuevoTexto: String) {
        _estado.update { actual ->
            val nuevaLista = actual.comentarios.toMutableList()
            if (index in nuevaLista.indices) {
                val comentario = nuevaLista[index]
                nuevaLista[index] = comentario.copy(
                    texto = nuevoTexto,
                    errores = comentario.errores.copy(texto = null)
                )
            }
            actual.copy(comentarios = nuevaLista)
        }
    }

    fun validarPublicacion(): Boolean {
        val estado = _estado.value

        // Validaciones principales
        val errores = PublicacionErrores(
            titulo = estado.titulo.takeIf { it.isBlank() }?.let { "Título obligatorio" },
            estrellas = estado.estrellas.takeIf { it == 0 }?.let { 1 },
            comentarios = estado.comentarios.map { c ->
                ComentariosErrores(
                    texto = c.texto.takeIf { it.isBlank() }?.let { "Comentario vacío" },
                    estrellasComentario = c.estrellasComentario.takeIf { it == 0 }?.let { 1 }
                )
            }.toMutableList()
        )

        // Determinar si hay errores
        val hayErrores = errores.titulo != null ||
                errores.estrellas != null ||
                errores.comentarios.any { it.texto != null || it.estrellasComentario != null }

        // Actualizar estado con los errores detectados
        _estado.update { it.copy(errores = errores) }

        return !hayErrores
    }

}
