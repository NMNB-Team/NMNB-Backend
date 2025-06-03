package nmnb.application.domain.user.exception

import nmnb.common.response.base.BaseErrorCode
import nmnb.common.response.exception.GeneralException

class PetException(code: BaseErrorCode) : GeneralException(code)
