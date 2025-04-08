package ar.edu.unq.eperdemic.modelo.exceptions

class ErrorEspecieNoExiste: RuntimeException()  {
    override val message: String
        get() = "La especie no existe"
}