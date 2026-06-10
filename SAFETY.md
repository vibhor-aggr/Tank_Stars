# Acquisition Notes

## Workspace-local JDK
- Purpose: compile and test the LibGDX/Gradle Java project because the host PATH exposes `java` but no `javac`.
- Source: Eclipse Temurin JDK binary from Adoptium GitHub releases or API.
- Provenance: Eclipse Adoptium project, OpenJDK distribution.
- License/terms: GPLv2 with Classpath Exception for OpenJDK; Eclipse Adoptium distribution terms.
- Version/checksum: Temurin `17.0.19+10`; SHA-256 `d8afc263758141a66e0e3aafc321e783f7016696f4eaea067d340a269037d331`.
- Setup command: download/extract into `/mnt/TBT_DATA/nipun/work/.cache/autonomy/toolchains/`.
- Security gate: daily scan `/mnt/TBT_DATA/nipun/work/.cache/autonomy/security/latest.json`, generated `2026-06-10T19:01:34.731962+00:00`, expires `2026-06-11T19:01:34.731962+00:00`.
