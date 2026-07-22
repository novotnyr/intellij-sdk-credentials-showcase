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
        apiKeyService.save(apiKey)
    }

    override fun reset() {
        apiKey = runWithModalProgressBlocking(ModalTaskOwner.guess(), "Retrieving API key") {
            apiKeyService.find().orEmpty()
        }
        super.reset()
    }

    override fun disposeUIResources() {
        apiKey = ""
        super.disposeUIResources()
    }
}
