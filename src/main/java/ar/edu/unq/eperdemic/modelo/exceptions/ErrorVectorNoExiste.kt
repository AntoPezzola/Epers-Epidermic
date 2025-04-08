package ar.edu.unq.eperdemic.modelo.exceptions

class ErrorVectorNoExiste : Exception() {
    override val message: String
        get() = "El vector no existe en la base de datos"
}
