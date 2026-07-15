package com.example.service

import com.example.WorkspaceFile

/**
 * Represents the structured aggregated context of the target repository,
 * including Git log history, file topology, environment parameters, and dependencies.
 */
data class RepositoryContext(
    val repoPath: String,
    val branchName: String,
    val totalFiles: Int,
    val repositorySize: String,
    val recentCommits: List<String>,
    val dependencyCount: Int,
    val dependencyManifest: Map<String, String>,
    val activeEnvironmentKeys: List<String>
) {
    /**
     * Formats the context into a clean, highly structured, terminal-friendly telemetry layout.
     */
    fun toFormattedContextString(): String {
        val commitsStr = if (recentCommits.isEmpty()) "None" else recentCommits.take(3).joinToString("\n  • ")
        val depsStr = if (dependencyManifest.isEmpty()) "None" else dependencyManifest.entries.take(5).joinToString(", ") { "${it.key} (${it.value})" }
        return """
            === AGGREGATED REPOSITORY CONTEXT ===
            Location: $repoPath [$branchName]
            Files tracked: $totalFiles ($repositorySize)
            Active .env keys: ${if (activeEnvironmentKeys.isEmpty()) "None" else activeEnvironmentKeys.joinToString(", ")}
            
            🌿 RECENT GIT COMMITS:
              • $commitsStr
              
            📦 DEPENDENCY MANIFEST SUMMARY:
              $depsStr${if (dependencyCount > 5) " (and ${dependencyCount - 5} more...)" else ""}
            =====================================
        """.trimIndent()
    }
}

/**
 * Service class responsible for scanning and aggregating project architecture,
 * VCS logs, and dependencies into a unified semantic payload.
 */
class RepositoryContextAggregator {
    /**
     * Aggregates live repository metrics into a single RepositoryContext object.
     */
    fun aggregate(
        projectFiles: List<WorkspaceFile>,
        gitCommits: List<String>,
        dependencies: Map<String, String>,
        envConfig: Map<String, String>
    ): RepositoryContext {
        val totalSize = projectFiles.sumOf { file ->
            val parts = file.size.split(" ")
            if (parts.size >= 2) {
                val num = parts[0].toDoubleOrNull() ?: 0.0
                val unit = parts[1].uppercase()
                when {
                    unit.contains("KB") -> (num * 1024).toLong()
                    unit.contains("MB") -> (num * 1024 * 1024).toLong()
                    else -> num.toLong()
                }
            } else 0L
        }
        
        val formattedSize = when {
            totalSize < 1024 -> "$totalSize B"
            totalSize < 1024 * 1024 -> String.format("%.2f KB", totalSize / 1024.0)
            else -> String.format("%.2f MB", totalSize / (1024.0 * 1024.0))
        }

        return RepositoryContext(
            repoPath = "/var/www/agentic-os-sandbox",
            branchName = "main",
            totalFiles = projectFiles.size,
            repositorySize = formattedSize,
            recentCommits = gitCommits,
            dependencyCount = dependencies.size,
            dependencyManifest = dependencies,
            activeEnvironmentKeys = envConfig.keys.toList()
        )
    }
}
