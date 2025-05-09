package nmnb.application.exception

import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.ConstraintViolationException
import nmnb.common.response.base.BaseResponse
import nmnb.common.response.dto.ErrorReasonDTO
import nmnb.common.response.exception.GeneralException
import nmnb.common.response.status.ErrorStatus
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.ServletWebRequest
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@RestControllerAdvice(annotations = [RestController::class])
class ExceptionAdvice : ResponseEntityExceptionHandler() {
    private fun handleException(
        e: Exception,
        errorStatus: ErrorStatus,
        errorMessage: String,
        headers: HttpHeaders,
        request: WebRequest,
    ): ResponseEntity<Any>? {
        val body = BaseResponse.onFailure(
            errorStatus.code,
            errorStatus.message,
            errorMessage,
        )
        return super.handleExceptionInternal(e, body, headers, errorStatus.httpStatus, request)
    }

    @ExceptionHandler
    fun exception(e: Exception, request: WebRequest): ResponseEntity<Any>? {
        return handleException(e, ErrorStatus.INTERNAL_SERVER_ERROR, e.message.toString(), HttpHeaders.EMPTY, request)
    }

    @ExceptionHandler
    fun validation(e: ConstraintViolationException, request: WebRequest): ResponseEntity<Any>? {
        val errorMessage = e.constraintViolations
            .map { it.message }
            .reduceOrNull { acc, msg -> "$acc, $msg" }
            ?: "Validation error occurred"
        return handleExceptionInternalConstraint(e, ErrorStatus.valueOf(errorMessage), HttpHeaders.EMPTY, request)
    }

    private fun handleExceptionInternalConstraint(
        e: Exception,
        errorCommonStatus: ErrorStatus,
        headers: HttpHeaders,
        request: WebRequest,
    ): ResponseEntity<Any>? {
        val body = BaseResponse.onFailure(errorCommonStatus.code, errorCommonStatus.message, null)
        return super.handleExceptionInternal(e, body, headers, errorCommonStatus.httpStatus!!, request)
    }

    @ExceptionHandler(value = [GeneralException::class])
    fun onThrowException(
        generalException: GeneralException,
        request: HttpServletRequest,
    ): ResponseEntity<Any>? {
        val errorReasonHttpStatus = generalException.getErrorReasonHttpStatus()
        return handleExceptionInternal(generalException, errorReasonHttpStatus, HttpHeaders(), request)
    }

    private fun handleExceptionInternal(
        e: Exception,
        reason: ErrorReasonDTO,
        headers: HttpHeaders,
        request: HttpServletRequest,
    ): ResponseEntity<Any>? {
        val body = BaseResponse.onFailure(reason.code, reason.message, null)
        val webRequest: WebRequest = ServletWebRequest(request)

        return super.handleExceptionInternal(e, body, headers, reason.httpStatus, webRequest)
    }

    override fun handleMethodArgumentNotValid(
        e: MethodArgumentNotValidException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest,
    ): ResponseEntity<Any>? {
        val errors = LinkedHashMap<String, String>()
        e.bindingResult
            .fieldErrors
            .forEach {
                    fieldError,
                ->
                errors[fieldError.field] = fieldError.defaultMessage ?: ""
            }
        return handleExceptionInternalArgs(e, request, errors)
    }

    private fun handleExceptionInternalArgs(
        e: Exception,
        request: WebRequest,
        errorArgs: Map<String, String>,
    ): ResponseEntity<Any>? {
        val body = BaseResponse.onFailure(
            ErrorStatus.BAD_REQUEST.code,
            ErrorStatus.BAD_REQUEST.message,
            errorArgs,
        )
        return super.handleExceptionInternal(e, body, HttpHeaders.EMPTY, ErrorStatus.BAD_REQUEST.httpStatus!!, request)
    }
}
