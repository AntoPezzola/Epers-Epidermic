package ar.edu.unq.eperdemic.modelo.exceptions

class ErrorEspecieExiste: RuntimeException()  {
    override val message: String
        get() = "El patogeno no está creado en DB"
}