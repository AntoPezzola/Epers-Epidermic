package ar.edu.unq.eperdemic.modelo.exceptions

import java.lang.Exception

class UbicacionMuyLejana : Exception() {
    override val message: String
        get() = "No es posible llegar desde la actual ubicación del vector a la nueva por medio de un camino."

}
