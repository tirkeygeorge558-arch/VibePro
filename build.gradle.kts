// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
  alias(libs.plugins.android.application) apply false
  alias(libs.plugins.kotlin.compose) apply false
  alias(libs.plugins.google.devtools.ksp) apply false
  alias(libs.plugins.roborazzi) apply false
  alias(libs.plugins.secrets) apply false
  alias(libs.plugins.google.services) apply false
}

// Restore debug.keystore from debug.keystore.base64 if missing (e.g. when checked out from GitHub)
val debugKeystoreFile = file("${rootDir}/debug.keystore")
val base64KeystoreFile = file("${rootDir}/debug.keystore.base64")
if (!debugKeystoreFile.exists() && base64KeystoreFile.exists()) {
  try {
    val decoded = java.util.Base64.getDecoder().decode(base64KeystoreFile.readText().trim())
    debugKeystoreFile.writeBytes(decoded)
  } catch (_: Exception) {}
}
