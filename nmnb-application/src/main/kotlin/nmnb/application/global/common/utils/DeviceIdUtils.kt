package nmnb.application.global.common.utils

import nmnb.domain.user.User

object DeviceIdUtils {
    fun formatDeviceId(user: User, deviceId: String) = formatDeviceId(user.email, deviceId)

    fun formatDeviceId(email: String, deviceId: String) = "$email:$deviceId"
}
