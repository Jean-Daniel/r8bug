package com.xooloo.r8bug.model.messages

import androidx.room.DatabaseView
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import org.threeten.bp.LocalDate
import org.threeten.bp.temporal.ChronoUnit
import java.util.*


// ========================================================================
//                             Shared Models
// ========================================================================

@Entity
@JsonClass(generateAdapter = true)
public open class User(
    @field:Json(name = "uuid") @PrimaryKey public val uuid: UUID, // ro
    @field:Json(name = "username") public val username: String, // ro
    @field:Json(name = "first_name") public val firstName: String,
    @field:Json(name = "gender") public val gender: Gender?,
    @field:Json(name = "date_birth") public val birthDate: LocalDate,
    @field:Json(name = "profile") @Embedded(prefix = "profile_") public val profile: UserProfile
) {
    public val age: Int
        get() = birthDate.until(LocalDate.now(), ChronoUnit.YEARS).toInt()

    public val isChild: Boolean
        get() = age < 13

    override fun equals(other: Any?): Boolean {
        if (other === this)
            return true

        if (other !is User)
            return false

        return uuid == other.uuid &&
            username == other.username &&
            firstName == other.firstName &&
            gender == other.gender &&
            birthDate == other.birthDate &&
            profile == other.profile
    }

    override fun hashCode(): Int {
        var result = uuid.hashCode()
        result = 31 * result + username.hashCode()
        result = 31 * result + firstName.hashCode()
        result = 31 * result + (gender?.hashCode() ?: 0)
        result = 31 * result + birthDate.hashCode()
        result = 31 * result + profile.hashCode()
        return result
    }
}

/**
 * User profile editable fields
 */
@JsonClass(generateAdapter = true)
public class UserProfile(
    // Real first name as filled by parent
    @field:Json(name = "first_name") public val firstName: String?,
    @field:Json(name = "last_name") public val lastName: String?,
    // Account locale (used when sending email)
    @field:Json(name = "locale") public val locale: String? = null,
    // Account time zone (user when sending planned push notifications) in TZ string format
    @field:Json(name = "timezone") public val timezone: String? = null,
    // Is usage collection enabled.
    @field:Json(name = "collect_data") public val collectData: Boolean = false,
    @field:Json(name = "school") @Embedded(prefix = "school_") public val school: School? = null
) {
    public class Builder(profile: UserProfile) {
        // Real first name as filled by parent
        public var firstName: String? = profile.firstName
        public var lastName: String? = profile.lastName

        // Account locale (used when sending email)
        public var locale: String? = profile.locale

        // Account time zone (user when sending planned push notifications) in TZ string format
        public var timezone: String? = profile.timezone

        // Is usage collection enabled.
        public var collectData: Boolean = profile.collectData
        public var school: School? = profile.school

        public fun build(): UserProfile {
            return UserProfile(firstName, lastName, locale, timezone, collectData, school)
        }
    }

    public inline fun with(action: Builder.() -> Unit): UserProfile {
        return Builder(this).also(action).build()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UserProfile

        if (firstName != other.firstName) return false
        if (lastName != other.lastName) return false
        if (locale != other.locale) return false
        if (timezone != other.timezone) return false
        if (collectData != other.collectData) return false
        if (school != other.school) return false

        return true
    }

    override fun hashCode(): Int {
        var result = firstName?.hashCode() ?: 0
        result = 31 * result + (lastName?.hashCode() ?: 0)
        result = 31 * result + (locale?.hashCode() ?: 0)
        result = 31 * result + (timezone?.hashCode() ?: 0)
        result = 31 * result + collectData.hashCode()
        result = 31 * result + (school?.hashCode() ?: 0)
        return result
    }
}

@JsonClass(generateAdapter = true)
public data class School(
    @field:Json(name = "id") val id: Int?, // School ID in Xooloo School Database
    @field:Json(name = "name") val name: String?, // Establishment name (ro)
    @field:Json(name = "class") val grade: String? = null // Class/Form/Grade name
)

// ========================================================================
//                            Database Types
// ========================================================================

@DatabaseView("SELECT User.*, FriendData.name as friendName FROM User LEFT JOIN FriendData USING(uuid)")
public data class UserWithFriendName(
    @Embedded
    val user: User,
    val friendName: String?
) {

    val uuid: UUID
        get() = user.uuid

    val screenName: String
        get() = friendName ?: user.firstName

    val profile: UserProfile
        get() = user.profile
}
