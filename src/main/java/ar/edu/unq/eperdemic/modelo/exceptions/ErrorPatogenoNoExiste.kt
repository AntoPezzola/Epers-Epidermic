package ar.edu.unq.eperdemic.modelo.exceptions

class ErrorPatogenoNoExiste: Exception()  {
    override val message: String
        get() = "El patogeno no está creado en DB"
}