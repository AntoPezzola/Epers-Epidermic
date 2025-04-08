package ar.edu.unq.eperdemic.modelo.exceptions

data class ErrorResponse(
    val message: String?,
    val status: Int,
    val errors: List<String>?
)
