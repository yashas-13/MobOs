package com.example.agentos.core

/**
 * Capability registry is deliberately data-only at this layer. Concrete executors are injected
 * by the Android/Termux/remote bridges, keeping planning independent from privileged execution.
 */
class CapabilityRegistry(
    capabilities: List<AgentCapability> = defaultCapabilities(),
) {
    private val byId = capabilities.associateBy { it.id }

    fun all(): List<AgentCapability> = byId.values.toList()

    fun get(id: String): AgentCapability? = byId[id]

    fun canExecute(id: String): Boolean = byId[id]?.enabled == true

    fun requiresApproval(id: String): Boolean = byId[id]?.requiresApproval ?: true

    companion object {
        fun defaultCapabilities(): List<AgentCapability> = listOf(
            AgentCapability(
                id = "android.inspect",
                displayName = "Android Inspector",
                kind = CapabilityKind.LOCAL_ANDROID,
                description = "Inspect package, SDK, permissions and device state.",
                requiresApproval = false,
            ),
            AgentCapability(
                id = "android.journey",
                displayName = "Android Journey",
                kind = CapabilityKind.ADB,
                description = "Run an explicit, verifiable user journey on a test device.",
            ),
            AgentCapability(
                id = "termux.shell",
                displayName = "Termux Shell",
                kind = CapabilityKind.TERMUX,
                description = "Execute commands through the local Termux bridge.",
            ),
            AgentCapability(
                id = "git.workspace",
                displayName = "Git Workspace",
                kind = CapabilityKind.GIT,
                description = "Inspect and modify a repository with reviewable changes.",
            ),
            AgentCapability(
                id = "browser.control",
                displayName = "Browser Control",
                kind = CapabilityKind.BROWSER,
                description = "Navigate and inspect a browser through a controlled bridge.",
            ),
            AgentCapability(
                id = "remote.shell",
                displayName = "Remote Shell",
                kind = CapabilityKind.REMOTE,
                description = "Execute approved operations on a paired workstation.",
            ),
        )
    }
}
