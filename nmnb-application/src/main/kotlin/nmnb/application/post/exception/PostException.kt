package nmnb.application.post.exception

import nmnb.common.response.base.BaseErrorCode
import nmnb.common.response.exception.GeneralException

class PostException(code: BaseErrorCode) : GeneralException(code)
