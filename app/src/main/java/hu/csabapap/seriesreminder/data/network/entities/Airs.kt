package hu.csabapap.seriesreminder.data.network.entities

import se.ansman.kotshi.JsonDefaultValue
import se.ansman.kotshi.JsonSerializable

@Target(AnnotationTarget.VALUE_PARAMETER,
        AnnotationTarget.FUNCTION,
        AnnotationTarget.CONSTRUCTOR,
        AnnotationTarget.FIELD,
        AnnotationTarget.PROPERTY_GETTER)
@MustBeDocumented
@Retention(AnnotationRetention.SOURCE)
@JsonDefaultValue // Makes this annotation a custom default value annotation
annotation class DefaultDay

@Target(AnnotationTarget.VALUE_PARAMETER,
        AnnotationTarget.FUNCTION,
        AnnotationTarget.CONSTRUCTOR,
        AnnotationTarget.FIELD,
        AnnotationTarget.PROPERTY_GETTER)
@MustBeDocumented
@Retention(AnnotationRetention.SOURCE)
@JsonDefaultValue // Makes this annotation a custom default value annotation
annotation class DefaultTime

@Target(AnnotationTarget.VALUE_PARAMETER,
        AnnotationTarget.FUNCTION,
        AnnotationTarget.CONSTRUCTOR,
        AnnotationTarget.FIELD,
        AnnotationTarget.PROPERTY_GETTER)
@MustBeDocumented
@Retention(AnnotationRetention.SOURCE)
@JsonDefaultValue // Makes this annotation a custom default value annotation
annotation class DefaultTimeZone

@JsonSerializable
data class Airs(
        @DefaultDay
        val day: String = "",
        @DefaultTime
        val time: String = "",
        @DefaultTimeZone
        val timezone: String = "") {
    companion object {
        @DefaultDay
        @JvmField
        var defaultDay = ""

        @DefaultTime
        @JvmField
        var defaultTime = ""

        @DefaultTimeZone
        @JvmField
        var defaultTimeZone = ""
    }
}