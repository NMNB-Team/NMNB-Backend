package nmnb.domain.user.generator

import com.aventrix.jnanoid.jnanoid.NanoIdUtils
import org.hibernate.engine.spi.SharedSessionContractImplementor
import org.hibernate.id.IdentifierGenerator

class CustomIdGenerator : IdentifierGenerator {
    override fun generate(session: SharedSessionContractImplementor?, `object`: Any?): Any {
        return NanoIdUtils.randomNanoId(
            NanoIdUtils.DEFAULT_NUMBER_GENERATOR,
            CUSTOM_STRING.toCharArray(),
            ID_LENGTH,
        ) }

    companion object {
        private const val ID_LENGTH: Int = 16
        private const val CUSTOM_STRING = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ@"
    }
}
