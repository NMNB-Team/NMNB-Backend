package nmnb.webflux.global.exception

import jakarta.validation.ConstraintViolationException
import nmnb.common.response.base.BaseResponse
import nmnb.common.response.exception.GeneralException
import nmnb.common.response.status.ErrorStatus
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import reactor.core.publisher.Mono

@RestControllerAdvice
class ExceptionAdvice {
    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolation(
        ex: ConstraintViolationException,
    ): Mono<ResponseEntity<BaseResponse<String>>> {
        val errorMessage = ex.constraintViolations
            .joinToString(", ") { it.message }

        val response = BaseResponse.onFailure(
            ErrorStatus.BAD_REQUEST.code,
            ErrorStatus.BAD_REQUEST.message,
            errorMessage,
        )

        return Mono.just(
            ResponseEntity
                .status(ErrorStatus.BAD_REQUEST.httpStatus)
                .body(response),
        )
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(
        ex: MethodArgumentNotValidException,
    ): Mono<ResponseEntity<BaseResponse<Map<String, String>>>> {
        val errors = ex.bindingResult
            .fieldErrors
            .associate { it.field to (it.defaultMessage ?: "잘못된 값입니다.") }

        val response = BaseResponse.onFailure(
            ErrorStatus.BAD_REQUEST.code,
            ErrorStatus.BAD_REQUEST.message,
            errors,
        )

        return Mono.just(
            ResponseEntity
                .status(ErrorStatus.BAD_REQUEST.httpStatus)
                .body(response),
        )
    }

    @ExceptionHandler(GeneralException::class)
    fun handleGeneralException(
        ex: GeneralException,
    ): Mono<ResponseEntity<BaseResponse<Nothing?>>> {
        val reason = ex.getErrorReasonHttpStatus()

        val response = BaseResponse.onFailure(
            reason.code,
            reason.message,
            null,
        )

        return Mono.just(
            ResponseEntity
                .status(reason.httpStatus)
                .body(response),
        )
    }

    @ExceptionHandler(Exception::class)
    fun handleAllExceptions(ex: Exception): Mono<ResponseEntity<BaseResponse<String?>>> {
        ex.printStackTrace()

        val response = BaseResponse.onFailure(
            ErrorStatus.INTERNAL_SERVER_ERROR.code,
            ErrorStatus.INTERNAL_SERVER_ERROR.message,
            ex.message,
        )

        return Mono.just(
            ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response),
        )
    }
}
