package org.intellij.sdk.credentials

import com.intellij.credentialStore.CredentialAttributes
import com.intellij.credentialStore.Credentials
import com.intellij.credentialStore.generateServiceName
import com.intellij.ide.passwordSafe.PasswordSafe
import com.intellij.openapi.components.Service
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val SERVICE_NAME = "Credentials Showcase"

typealias ApiKey = String

@Service
class ApiKeyService {
    private val passwordSafe get() = PasswordSafe.instance

    private val serviceName = generateServiceName(SERVICE_NAME, "API Key")

    private val credentialAttributes = CredentialAttributes(serviceName, "API_KEY")

    suspend fun save(apiKey: ApiKey) = withContext(Dispatchers.IO) {
        passwordSafe[credentialAttributes] = Credentials(null, apiKey)
    }

    suspend fun find(): ApiKey? = withContext(Dispatchers.IO) {
        passwordSafe[credentialAttributes]?.password?.toString()
    }
}
