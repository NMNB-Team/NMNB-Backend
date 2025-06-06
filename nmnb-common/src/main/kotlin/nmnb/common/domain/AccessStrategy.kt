package nmnb.common.domain

import software.amazon.awssdk.services.s3.model.ObjectCannedACL

enum class AccessStrategy(
    val cannedAcl: ObjectCannedACL,
    val usePresignedUrl: Boolean,
) {
    PUBLIC_READ(ObjectCannedACL.PUBLIC_READ, false),
    PRIVATE(ObjectCannedACL.PRIVATE, true),
}
