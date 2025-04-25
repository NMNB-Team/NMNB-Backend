package cv.nmnb.global.response.base

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder

@JsonPropertyOrder("code", "message", "result", "isSuccess")
class BaseResponse<T>(
    val code: String,
    val message: String,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val result: T,
    @JsonProperty("isSuccess")
    val isSuccess: Boolean = true,
) {
    companion object {
        fun <T> onSuccess(code: BaseCode, data: T): BaseResponse<T> {
            return of(true, code, data)
        }

        private fun <T> of(isSuccess: Boolean, code: BaseCode, result: T): BaseResponse<T> {
            return BaseResponse<T>(
                code.getReasonHttpStatus().code,
                code.getReasonHttpStatus().message,
                result,
                isSuccess,
            )
        }

        fun <T> onFailure(code: BaseCode, data: T): BaseResponse<T> {
            return of(false, code, data)
        }

        fun <T> onFailure(code: String, message: String, data: T): BaseResponse<T> {
            return BaseResponse<T>(code, message, data, false)
        }
    }
}
