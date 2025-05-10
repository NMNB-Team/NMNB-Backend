package nmnb.domain.auth

enum class SocialType(val value: String) {
    KAKAO("kakao"),
    ;

    companion object {
        fun from(value: String): SocialType =
            entries.find { it.value.equals(value, ignoreCase = true) }
                ?: throw IllegalArgumentException("Unsupported social type: $value")
    }
}
