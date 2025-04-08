package ar.edu.unq.eperdemic.modelo.exceptions

import java.lang.Exception
import javax.validation.ConstraintViolationException

class ErrorDatosIngresadosIncorrectos(var e : List<String>) : Exception() {
    override val message: String
        get() =e.first()
}