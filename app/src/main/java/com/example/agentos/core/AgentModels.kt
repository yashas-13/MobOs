package com.example.agentos.core

import java.util.UUID

/** Stable domain model for intent -> plan -> execution -> verification. */
data class AgentIntent(
    val id: String = UUID.randomUUID().toString(),
    val text: String,
    val requestedAtEpochMs: Long = System.currentTimeMillis(),
    val risk: RiskLevel = RiskLevel.LOW,
)

enum class RiskLevel { LOW, MEDIUM, HIGH, CRITICAL }

enum class ExecutionState { QUEUED, PLANNING, WAITING_APPROVAL, EXECUTING, VERIFYING, SUCCEEDED, FAILED, CANCELLED }

enum class CapabilityKind { LOCAL_ANDROID, TERMUX, ADB, GIT, BROWSER, REMOTE, NETWORK, FILESYSTEM }

data class AgentCapability(
    val id: String,
    val displayName: String,
    val kind: CapabilityKind,
    val description: String,
    val requiresApproval: Boolean = true,
    val enabled: Boolean = true,
)

data class PlanStep(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val capabilityId: String,
    val requiresApproval: Boolean = false,
)

data class ExecutionPlan(
    val intentId: String,
    val steps: List<PlanStep>,
    val generatedAtEpochMs: Long = System.currentTimeMillis(),
)

data class VerificationResult(
    val passed: Boolean,
    val checks: List<String>,
    val failures: List<String> = emptyList(),
)
