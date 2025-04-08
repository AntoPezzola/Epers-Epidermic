package ar.edu.unq.eperdemic.modelo.exceptions

import java.sql.SQLException

class ErrorIdInexistente : SQLException() {
    override val message: String
        get() = "El id del patógeno no existe"


}