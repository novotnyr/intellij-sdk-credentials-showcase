package org.intellij.sdk.credentials

import com.intellij.openapi.components.service
import com.intellij.openapi.options.BoundConfigurable
import com.intellij.platform.ide.progress.ModalTaskOwner
import com.intellij.platform.ide.progress.runWithModalProgressBlocking
import com.intellij.ui.components.JBPasswordField
import com.intellij.ui.dsl.builder.AlignX
import com.intellij.ui.dsl.builder.MutableProperty
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.dsl.builder.toMutableProperty

class CredentialsConfigurable : BoundConfigurable("Credentials") {
    private var apiKey: ApiKey = NO_API_KEY

    private val apiKeyProp: MutableProperty<ApiKey> = this::apiKey.toMutableProperty()

    override fun createPanel() = panel {
        row("API Key:") {
            passwordField()
                .bind(
                    JBPasswordField::getApiKey,
                    JBPasswordField::setApiKey,
                    apiKeyProp
                ).align(AlignX.FILL)
        }
    }

    override fun apply() {
        super.apply()
        invoke("Securely storing API key") {
            service<ApiKeyService>().save(apiKey)
        }
    }

    override fun reset() {
        apiKey = invoke("Retrieving API key") {
            service<ApiKeyService>().find()
        }
        super.reset()
    }

    override fun disposeUIResources() {
        apiKey.clear()
        apiKey = NO_API_KEY
        super.disposeUIResources()
    }

    private fun <T> invoke(title: String, action: suspend () -> T): T {
        return runWithModalProgressBlocking(ModalTaskOwner.guess(), title) {
            action()
        }
    }
}

private fun JBPasswordField.getApiKey(): ApiKey {
    return ApiKey(this.password)
}

private fun JBPasswordField.setApiKey(apiKey: ApiKey) {
    this.text = apiKey.toString()
}
