package ar.edu.unq.eperdemic.ar.edu.unq.eperdemic.modelo.exceptions

class ExceptionGenericNotFoundID( id: Long) :
    Exception("El registro de id $id no está persistido") {
}
