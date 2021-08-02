package com.xooloo.r8bug.model.messages

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.JsonDataException
import org.threeten.bp.LocalDate
import java.util.*


// ========================================================================
//                             JSON Models
// ========================================================================

/**
 * User fields
 */
@JsonClass(generateAdapter = true)
class Account(
    // ---- UserPublic
    uuid: UUID, // ro
    username: String, // ro
    firstName: String,
    gender: Gender?,
    birthDate: org.threeten.bp.LocalDate,
    profile: UserProfile,

    // ---- Specific Fields
    @field:Json(name = "is_validated") internal val _isValidated: Boolean? = null,
    // User phone (unique)
    @field:Json(name = "phone") val phone: String?,
    // Was the phone number verified (SMS verification)
    @field:Json(name = "phone_verified") val isPhoneVerified: Boolean, // ro
    @field:Json(name = "parent") val parent: Parent?,
    // True if the user is over 13 years old, depends of parent approval otherwise.
    @field:Json(name = "is_parent_validated") val isParentValidated: Boolean,
    // True if this is the user account of the linked parent (determined by phone number matching)
    @field:Json(name = "is_parent") val isParent: Boolean, // ro
    // Children accounts linked to the parent. Only present if the user is a parent.
    @field:Json(name = "children") val children: List<Child>?, // ro
    @field:Json(name = "has_password") val hasPassword: Boolean = false
) : User(uuid, username, firstName, gender, birthDate, profile) {

    // Must be provided by the server, but keep fallback for compat.
    val isValidated: Boolean
        get() = _isValidated ?: (parent?.isNotEmpty == true && isParentValidated) || (isPhoneVerified && !isChild)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as Account

        if (isValidated != other.isValidated) return false
        if (phone != other.phone) return false
        if (isPhoneVerified != other.isPhoneVerified) return false
        if (parent != other.parent) return false
        if (isParentValidated != other.isParentValidated) return false
        if (isParent != other.isParent) return false
        if (children != other.children) return false
        if (hasPassword != other.hasPassword) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + isValidated.hashCode()
        result = 31 * result + (phone?.hashCode() ?: 0)
        result = 31 * result + isPhoneVerified.hashCode()
        result = 31 * result + (parent?.hashCode() ?: 0)
        result = 31 * result + isParentValidated.hashCode()
        result = 31 * result + isParent.hashCode()
        result = 31 * result + (children?.hashCode() ?: 0)
        result = 31 * result + hasPassword.hashCode()
        return result
    }

    class Builder(account: Account) {
        val uuid: UUID = account.uuid
        var username: String = account.username
        var firstName: String = account.firstName
        var gender: Gender? = account.gender
        var birthDate: LocalDate = account.birthDate
        var profile: UserProfile = account.profile

        var isValidated: Boolean? = account._isValidated
        var phone: String? = account.phone
        var isPhoneVerified: Boolean = account.isPhoneVerified
        var parent: Parent? = account.parent
        var isParentValidated: Boolean = account.isParentValidated
        var isParent: Boolean = account.isParent
        var children: List<Child>? = account.children
        var hasPassword: Boolean = account.hasPassword

        inline fun profile(action: UserProfile.Builder.() -> Unit) {
            profile = profile.with(action)
        }

        fun build(): Account {
            return Account(
                uuid, username, firstName, gender, birthDate,
                profile, isValidated, phone, isPhoneVerified, parent,
                isParentValidated, isParent, children, hasPassword
            )
        }
    }

    inline fun with(action: Builder.() -> Unit): Account {
        return Builder(this).also(action).build()
    }
}

@JsonClass(generateAdapter = true)
data class Parent(
    // email address of parent account
    @field:Json(name = "email") val email: String? = null,
    @field:Json(name = "phone_number") val phoneNumber: String? = null,
    @field:Json(name = "instant_messenging") val imIdentifier: String? = null,
    @field:Json(name = "im_protocol") val imType: IMType? = null
) {

    val isEmpty: Boolean
        get() = email.isNullOrEmpty() && phoneNumber.isNullOrEmpty() && imIdentifier.isNullOrEmpty()

    val isNotEmpty: Boolean
        get() = !isEmpty

    val identifier: String?
        get() = phoneNumber ?: email ?: imIdentifier
}

@JsonClass(generateAdapter = true)
class Child(
    // ---- UserPublic
    uuid: UUID, // ro
    username: String, // ro
    firstName: String,
    gender: Gender?,
    birthDate: LocalDate,
    profile: UserProfile,

    // ---- Specific Fields
    @field:Json(name = "parent_role") val parentRole: ParentRole? = null
) : User(uuid, username, firstName, gender, birthDate, profile) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as Child

        if (parentRole != other.parentRole) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + (parentRole?.hashCode() ?: 0)
        return result
    }
}


// ========================================================================
//                            Custom Types
// ========================================================================
enum class ParentRole {
    Mother,
    Father,
    Other
}

fun ParentRole?.toJson(): String = when (this) {
    ParentRole.Mother -> "mother"
    ParentRole.Father -> "father"
    ParentRole.Other -> "other"
    null -> "undefined"
}

fun parentRoleFromJson(value: String): ParentRole? = when (value) {
    "mother" -> ParentRole.Mother
    "father" -> ParentRole.Father
    "other" -> ParentRole.Other
    // undefined
    else -> null
}

enum class IMType {
    Unknown,
    WhatsApp,
    Messenger,
    WeChat,
    Telegram,

    // In case new type are added
    Unsupported;
}

fun IMType.toJson(): String = when (this) {
    IMType.WhatsApp -> "whatsapp"
    IMType.Messenger -> "messenger"
    IMType.WeChat -> "wechat"
    IMType.Telegram -> "telegram"
    IMType.Unknown -> "unknown"
    IMType.Unsupported -> "unknown"
}

internal fun imTypeFromJson(value: String): IMType = when (value) {
    "whatsapp" -> IMType.WhatsApp
    "messenger" -> IMType.Messenger
    "wechat" -> IMType.WeChat
    "telegram" -> IMType.Telegram
    "unknown" -> IMType.Unknown
    else -> IMType.Unsupported
}

@Suppress("EnumEntryName") // iOS start with a lower 'i'
enum class OSFamily {
    Android,
    iOS
}

internal fun OSFamily.toJson(): String = when (this) {
    OSFamily.iOS -> "ios"
    OSFamily.Android -> "android"
}

internal fun osFamilyFromJson(value: String): OSFamily = when (value) {
    "ios" -> OSFamily.iOS
    "android" -> OSFamily.Android
    else -> throw JsonDataException("Unknown os family: $value")
}
