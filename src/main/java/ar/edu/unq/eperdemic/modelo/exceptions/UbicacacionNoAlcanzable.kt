package ar.edu.unq.eperdemic.modelo.exceptions

import java.lang.Exception

class UbicacacionNoAlcanzable : Exception() {
    override val message: String
        get() = "se intenta mover a un vector a través de un tipo de camino que no puede atravesar"
}
