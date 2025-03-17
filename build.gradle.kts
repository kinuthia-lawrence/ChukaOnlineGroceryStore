// Root-level build.gradle.kts
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false

    // Declare the Google services plugin version here, but do not apply it
    id("com.google.gms.google-services") version "4.4.2" apply false
}

// No repositories block here because settings.gradle.kts enforces RepositoriesMode.FAIL_ON_PROJECT_REPOS