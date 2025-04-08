package ar.edu.unq.eperdemic.modelo.exceptions


class ErrorPatogenoExistente() : Exception(){
    override val message: String
        get() = "El patogeno ya existe en la base de datos"
}
