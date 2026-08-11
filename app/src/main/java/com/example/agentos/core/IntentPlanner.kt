package com.example.agentos.core

/**
 * First planning layer. It intentionally produces a conservative plan from explicit keywords;
 * an LLM planner can later implement the same interface without changing the execution contract.
 */
class IntentPlanner(private val registry: CapabilityRegistry) {

    fun plan(intent: AgentIntent): ExecutionPlan {
        val text = intent.text.lowercase()
        val steps = buildList {
            add(
                PlanStep(
                    title = "Inspect request and project context",
                    capabilityId = "android.inspect",
                ),
            )

            if (text.containsAny("build", "apk", "android", "app", "compose")) {
                add(
                    PlanStep(
                        title = "Prepare Android build/test workflow",
                        capabilityId = "android.journey",
                    ),
                )
            }
            if (text.containsAny("termux", "shell", "command", "terminal")) {
                add(
                    PlanStep(
                        title = "Execute approved Termux operations",
                        capabilityId = "termux.shell",
                        requiresApproval = true,
                    ),
                )
            }
            if (text.containsAny("git", "github", "repository", "repo", "commit", "pr")) {
                add(
                    PlanStep(
                        title = "Inspect and prepare repository changes",
                        capabilityId = "git.workspace",
                        requiresApproval = true,
                    ),
                )
            }
            if (text.containsAny("browser", "website", "web", "page")) {
                add(
                    PlanStep(
                        title = "Use controlled browser capability",
                        capabilityId = "browser.control",
                        requiresApproval = true,
                    ),
                )
            }

            add(
                PlanStep(
                    title = "Verify outcome and report evidence",
                    capabilityId = "android.journey",
                ),
            )
        }.filter { registry.canExecute(it.capabilityId) }

        return ExecutionPlan(intentId = intent.id, steps = steps)
    }

    private fun String.containsAny(vararg values: String): Boolean = values.any(::contains)
}
