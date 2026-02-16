// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
}

// Task to install Git hooks
tasks.register<Copy>("installGitHooks") {
    from(file("git-hooks/pre-commit"))
    into(file(".git/hooks"))

    doLast {
        // Set executable permission on the hook file
        file(".git/hooks/pre-commit").setExecutable(true)

        println("✅ Git hooks installed successfully!")
        println("Pre-commit hook will run lint and tests before each commit.")
        println("Use 'git commit --no-verify' to skip if needed.")
    }
}