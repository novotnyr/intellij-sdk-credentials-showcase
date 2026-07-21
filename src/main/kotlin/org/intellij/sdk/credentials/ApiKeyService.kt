package org.intellij.sdk.credentials

import com.intellij.credentialStore.CredentialAttributes
import com.intellij.credentialStore.OneTimeString
import com.intellij.credentialStore.generateServiceName
import com.intellij.ide.passwordSafe.PasswordSafe
import com.intellij.openapi.application.EDT
import com.intellij.openapi.components.Service
import com.intellij.util.Ephemeral
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

private const val SERVICE_NAME = "Credentials Showcase"

typealias ApiKey = String

@Service
class ApiKeyService(val cs: CoroutineScope) {
    private val passwordSafe get() = PasswordSafe.instance

    private val serviceName = generateServiceName(SERVICE_NAME, "API Key")

    private val credentialAttributes = CredentialAttributes(serviceName, "API_KEY")

    fun set(apiKey: ApiKey) {
        cs.launch {
            passwordSafe.setPassword(credentialAttributes, apiKey)
        }
    }

    suspend fun get(): ApiKey? = withContext(Dispatchers.IO) {
        passwordSafe.getPassword(credentialAttributes)
    }
}
