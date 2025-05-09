package nmnb.domain.user.generator.annotation

import nmnb.domain.user.generator.CustomIdGenerator
import org.hibernate.annotations.IdGeneratorType

@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
@IdGeneratorType(CustomIdGenerator::class)
annotation class UserId
