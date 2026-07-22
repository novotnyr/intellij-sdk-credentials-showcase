package org.intellij.sdk.credentials

import com.intellij.credentialStore.Credentials

class ApiKey(private val value: CharArray) {
    fun toCredentials() = Credentials(user = null, value)

    fun clear() {
        value.fill('0')
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ApiKey

        return value.contentEquals(other.value)
    }

    override fun hashCode() = value.contentHashCode()

    override fun toString() = String(value)
}

val NO_API_KEY = ApiKey(CharArray(0))

fun Credentials?.toApiKey(): ApiKey {
    return this?.password?.toCharArray()
        ?.let { ApiKey(it) }
        ?: NO_API_KEY
}


