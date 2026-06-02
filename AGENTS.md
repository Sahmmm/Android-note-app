# AGENTS.md

## Cursor Cloud specific instructions

### Product

**NoteApp** (`com.example.noteapp`) — Android notes app (Java, single `:app` module). Notes are stored locally as JSON on device; no backend services.

### Prerequisites (VM image / snapshot)

These are **not** installed by the repo `update_script`; they must exist on the Cloud VM (one-time setup):

| Component | Location / version |
|-----------|-------------------|
| **Android SDK** | `$HOME/Android/Sdk` — `platforms;android-35`, `build-tools;35.0.0`, `platform-tools` |
| **JetBrains JDK 21** | `$HOME/.jdks/jbrsdk-21.0.11-linux-x64-b1163.116` — required by `gradle/gradle-daemon-jvm.properties` (`toolchainVendor=jetbrains`, `toolchainVersion=21`). OpenJDK 21 alone is **not** sufficient for the Gradle daemon. |
| **Environment** | `~/.bashrc` exports `ANDROID_HOME`, `ANDROID_SDK_ROOT`, and `JAVA_HOME` (JBR path) and extends `PATH` with `cmdline-tools/latest/bin` and `platform-tools`. |

`local.properties` is gitignored; the update script recreates `sdk.dir` when `$HOME/Android/Sdk` exists.

### Commands (from repo root)

| Task | Command |
|------|---------|
| Unit tests | `bash gradlew test` |
| Debug unit tests only | `bash gradlew testDebugUnitTest` |
| Lint (debug) | `bash gradlew lintDebug` — may fail on existing lint issues in app code |
| Debug APK | `bash gradlew assembleDebug` → `app/build/outputs/apk/debug/app-debug.apk` |
| Instrumented tests | `bash gradlew connectedDebugAndroidTest` — requires emulator or USB device |

Always use **`bash gradlew`** (not `./gradlew`) if execute bit is missing.

### Gotchas

- **Gradle daemon JVM**: Must match JetBrains vendor per `gradle/gradle-daemon-jvm.properties`. Set `JAVA_HOME` to the JBR install before invoking Gradle.
- **SDK discovery**: Gradle uses `local.properties` (`sdk.dir`) or `ANDROID_HOME` / `ANDROID_SDK_ROOT`.
- **Lint vs build**: `assembleDebug` and `test` can succeed while `lintDebug` fails due to pre-existing lint findings (e.g. API level / layout issues).
- **No emulator in default Cloud setup**: UI/manual testing needs an emulator or physical device; unit tests do not.
