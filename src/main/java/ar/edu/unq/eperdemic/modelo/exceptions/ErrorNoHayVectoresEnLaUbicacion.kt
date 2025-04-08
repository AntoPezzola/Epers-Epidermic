package ar.edu.unq.eperdemic.modelo.exceptions

class ErrorNoHayVectoresEnLaUbicacion : Exception() {
    override val message: String
        get() = "en la ubicacion no hay vectores para poder infectar"
}
