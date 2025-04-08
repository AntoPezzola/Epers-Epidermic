package ar.edu.unq.eperdemic.modelo.exceptions

class ErrorUbicacionNoExiste : Exception() {
    override val message: String
        get() = "La Ubicacion no existe en la base de datos"
}
