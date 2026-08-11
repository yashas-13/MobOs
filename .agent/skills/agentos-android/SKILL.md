---
name: agentos-android
description: Build, modernize, test, verify, and troubleshoot Android applications using current official Android tooling, Android Skills, Compose, AndroidX, Gradle, device journeys, and permission-aware agent workflows.
metadata:
  author: yashas-13
  version: "0.1.0"
---

# AgentOS Android Engineering

Use this skill whenever the task changes Android source, Gradle configuration, AndroidX dependencies, Compose UI, device automation, APK builds, testing, or Android agent capabilities.

## Operating contract

1. Prefer official Android Developers documentation and the official Android Skills repository for current guidance.
2. Inspect the existing project before changing versions or architecture.
3. Resolve compatibility as a graph: AGP, Gradle, JDK, compileSdk, Kotlin, Compose, KSP, AndroidX, and third-party plugins.
4. Prefer stable dependencies for production unless the task explicitly requests preview APIs.
5. Never use dynamic dependency versions such as `+`.
6. Preserve existing application behavior while modernizing incrementally.
7. Treat shell, ADB, filesystem, browser, Git, and remote-machine operations as capabilities requiring explicit policy and, where appropriate, user approval.
8. Never embed API keys, signing passwords, SSH keys, or other secrets in source control.
9. For important UI changes, add a deterministic test and use an Android Journey when device-level behavior matters.
10. A task is not complete until the resulting state is buildable and the verification evidence is recorded.

## Intent workflow

For every non-trivial request:

`intent -> inspect -> plan -> select skills -> request permissions -> execute -> observe -> verify -> summarize evidence`

Do not turn the model into the privileged executor. The planner emits capability IDs and an execution plan; bridges perform the actual operation under policy.

## Current-stack policy

The project baseline is pinned in `gradle/libs.versions.toml`. Before a dependency migration, refresh against current official release information and update the compatibility notes in `references/current-stack.md`.

Compose dependencies must use the Compose BOM. Do not assign individual Compose library versions unless there is a documented exception.

## Verification ladder

Use the smallest sufficient verification set, escalating when the risk warrants it:

- source-level checks
- unit tests
- `assembleDebug`
- lint/static analysis
- instrumentation tests
- Android Journey/device verification
- release build/signing validation

Report failures precisely; never claim a build passed without evidence.
