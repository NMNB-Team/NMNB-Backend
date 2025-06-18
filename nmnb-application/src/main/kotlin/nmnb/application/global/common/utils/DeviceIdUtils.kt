package nmnb.application.global.common.utils

import nmnb.domain.user.User

object DeviceIdUtils {
    fun deviceIdFormatter(user: User, deviceId: String) = deviceIdFormatter(user.email, deviceId)

    fun deviceIdFormatter(email: String, deviceId: String) = "$email:$deviceId"
}
