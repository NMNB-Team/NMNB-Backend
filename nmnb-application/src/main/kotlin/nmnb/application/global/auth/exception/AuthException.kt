package nmnb.application.global.auth.exception

import nmnb.common.response.base.BaseErrorCode
import nmnb.common.response.exception.GeneralException

class AuthException(code: BaseErrorCode) : GeneralException(code)
