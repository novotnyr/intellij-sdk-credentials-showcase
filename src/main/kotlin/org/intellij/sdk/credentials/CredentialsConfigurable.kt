package org.intellij.sdk.credentials

import com.intellij.openapi.components.service
import com.intellij.openapi.options.BoundConfigurable
import com.intellij.platform.ide.progress.ModalTaskOwner
import com.intellij.platform.ide.progress.runWithModalProgressBlocking
import com.intellij.ui.dsl.builder.AlignX
import com.intellij.ui.dsl.builder.bindText
import com.intellij.ui.dsl.builder.panel

class CredentialsConfigurable : BoundConfigurable("Credentials") {
    private val apiKeyService get() = service<ApiKeyService>()

    var apiKey: String = ""

    override fun createPanel() = panel {
        row("API Key:") {
            passwordField()
                .bindText(this@CredentialsConfigurable::apiKey)
                .align(AlignX.FILL)
        }
    }

    override fun apply() {
        super.apply()
        invoke("Securely storing API key") {
            apiKeyService.save(apiKey)
        }
    }

    override fun reset() {
        apiKey = invoke("Retrieving API key") {
            apiKeyService.find().orEmpty()
        }
        super.reset()
    }

    override fun disposeUIResources() {
        apiKey = ""
        super.disposeUIResources()
    }

    private fun <T> invoke(title: String, action: suspend () -> T): T {
        return runWithModalProgressBlocking(ModalTaskOwner.guess(), title) {
            action()
        }
    }
}
