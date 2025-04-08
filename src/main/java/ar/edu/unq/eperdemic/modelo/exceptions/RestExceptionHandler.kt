package ar.edu.unq.eperdemic.modelo.exceptions

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class RestExceptionHandler {

    @ExceptionHandler(ErrorEspecieNoExiste::class)
    fun handleErrorEspecieNoExiste(exc: NoSuchElementException): ResponseEntity<ErrorResponse> {
        val httpStatus = HttpStatus.BAD_REQUEST
        return buildResponseEntity(httpStatus, exc)
    }

    private fun buildResponseEntity(httpStatus: HttpStatus, exc: Exception, errors: List<String>? = null): ResponseEntity<ErrorResponse> {
        val error = ErrorResponse(
            message = exc.message,
            status = httpStatus.value(),
            errors = errors
        )
        return ResponseEntity(error, httpStatus)
    }


    @ExceptionHandler(ErrorIdInexistente::class)
    fun handleErrorIdInexistente(exc: NoSuchElementException): ResponseEntity<ErrorResponse> {
        val httpStatus = HttpStatus.BAD_REQUEST
        return buildResponseEntity(httpStatus, exc)
    }

    @ExceptionHandler(ErrorEspecieExiste::class)
    fun handleErrorEspecieExiste(exc: NoSuchElementException): ResponseEntity<ErrorResponse> {
        val httpStatus = HttpStatus.BAD_REQUEST
        return buildResponseEntity(httpStatus, exc)
    }



}