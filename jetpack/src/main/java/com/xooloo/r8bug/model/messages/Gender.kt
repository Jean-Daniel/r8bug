package com.xooloo.r8bug.model.messages

public enum class Gender {
    Boy,
    Girl
}

public fun Gender?.toJson(): String {
    return when (this) {
        Gender.Boy -> "male"
        Gender.Girl -> "female"
        null -> "unknown"
    }
}

public fun genderFromJson(value: String?): Gender? = when (value) {
    "male" -> Gender.Boy
    "female" -> Gender.Girl
    else -> null
}

