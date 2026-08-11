# MobOs — AgentOS for Android

**Stop prompting. Start delegating.**

MobOs is evolving into a mobile-first AgentOS control plane: an Android application that turns natural-language intent into a permission-aware plan, delegates work to capabilities/agents, observes execution, and verifies the result.

## Core loop

```text
Intent → Inspect → Plan → Skills → Approval → Execute → Observe → Verify → Learn
```

## Architecture

```text
Android APK
├── Intent / Console UI
├── Agent planner
├── Skills runtime
├── Capability registry
├── Permission / approval policy
├── Execution timeline
├── Project memory
└── Verification / Journeys
       │
       ├── Android / ADB
       ├── Termux bridge
       ├── Git / GitHub
       ├── Browser bridge
       └── Remote workstation bridge
```

The Android application remains the **control plane**. Privileged shell, remote, browser, and device operations are exposed through explicit capability bridges instead of being hidden inside the UI.

## Current engineering baseline

The project uses a pinned version catalog and a dated compatibility snapshot. Current verified sources include:

- Android Gradle Plugin 9.1.1
- JDK 17
- compileSdk / targetSdk 37
- Kotlin 2.4.10
- KSP 2.3.10
- Compose BOM 2026.06.00
- Activity 1.13.0
- Lifecycle 2.11.0
- Navigation 2.9.8
- Room 2.8.4
- CameraX 1.6.1
- WorkManager 2.11.2

Versions are intentionally pinned; the project does **not** use dynamic Gradle versions.

## Agent Skills

AgentOS follows the Agent Skills directory model. Android-specific instructions live under `.agent/skills/agentos-android/` and can reference scripts, documentation, and other resources.

The runtime is designed to consume current official Android Skills rather than freezing a copied snapshot of Google's documentation.

## Build

The repository includes a GitHub Actions pipeline that builds the debug APK with JDK 17, Android API 37, and Gradle 9.3.1 and uploads the APK as a workflow artifact.

For local development, open the project in a current Android Studio release and let Gradle resolve the pinned toolchain.

## Product direction

The long-term goal is a system where repeated human workflows can become reusable skills and eventually automations:

```text
Human workflow
    ↓
Agent observes
    ↓
Skill generated
    ↓
Capability permissions declared
    ↓
Workflow verified
    ↓
Automation
```

The UI is intentionally designed to make agent execution visually understandable and screen-recordable: **UNDERSTAND → PLAN → EXECUTE → VERIFY → DONE**.
