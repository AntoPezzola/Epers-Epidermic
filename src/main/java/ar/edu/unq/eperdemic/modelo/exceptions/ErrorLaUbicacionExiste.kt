package ar.edu.unq.eperdemic.modelo.exceptions

import java.lang.RuntimeException

class ErrorLaUbicacionExiste : RuntimeException() {
    override val message: String
        get() = "La ubicacion ya existe en la base de datos"

    companion object {

        private val serialVersionUID = 1L
    }
}
