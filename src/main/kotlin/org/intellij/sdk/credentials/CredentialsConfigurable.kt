package org.intellij.sdk.credentials

import com.intellij.openapi.application.EDT
import com.intellij.openapi.application.ModalityState
import com.intellij.openapi.application.asContextElement
import com.intellij.openapi.components.service
import com.intellij.openapi.options.BoundConfigurable
import com.intellij.openapi.ui.DialogPanel
import com.intellij.platform.ide.progress.ModalTaskOwner
import com.intellij.platform.ide.progress.runWithModalProgressBlocking
import com.intellij.ui.components.JBPasswordField
import com.intellij.ui.dsl.builder.AlignX
import com.intellij.ui.dsl.builder.bindText
import com.intellij.ui.dsl.builder.panel
import com.jetbrains.rd.util.threading.coroutines.RdCoroutineScope.Companion.override
import com.sun.jna.platform.win32.COM.DispatchVTable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.reflect.KMutableProperty0

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
        apiKeyService.set(apiKey)
    }

    override fun reset() {
        apiKey = runWithModalProgressBlocking(ModalTaskOwner.guess(), "Retrieving API key") {
            apiKeyService.get().orEmpty()
        }
        super.reset()
    }
}
