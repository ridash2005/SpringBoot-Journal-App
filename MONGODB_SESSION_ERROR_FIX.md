# MongoDB Session Error Fix - June 5, 2026

## Problem
GitHub Actions CI/CD build was failing with:
```
java.lang.IllegalStateException: Failed to get session
  at com.mongodb.client.internal.MongoClientDelegate$DelegateOperationExecutor.getClientSession
```

## Root Cause
Even with `-DskipTests`, the Maven `compile` and `package` phases were loading the Spring application context, which automatically tried to create MongoDB indexes because `auto-index-creation: true` was enabled in `application.yml`. Since MongoDB isn't running in GitHub Actions, the connection failed.

## Solution Implemented

### 1. Created CI/CD Configuration Profile
**File**: `src/main/resources/application-ci.yml`
- Disables MongoDB auto-index-creation: `auto-index-creation: false`
- Provides dummy values for external services (Gmail, Google OAuth, etc.)
- Prevents Spring from attempting to connect to unavailable services during build

### 2. Updated GitHub Actions Workflow  
**File**: `.github/workflows/build.yml`
- Added `-Dspring.profiles.active=ci` to Maven compile step
- Added `-Dspring.profiles.active=ci` to Maven package step
- This activates the CI profile, disabling auto-index-creation

## Changes Made

```yaml
# Before
- name: Build and Compile (No Tests)
  run: mvn clean compile -DskipTests

- name: Package Application
  run: mvn package -DskipTests

# After
- name: Build and Compile (No Tests)
  run: mvn clean compile -DskipTests -Dspring.profiles.active=ci

- name: Package Application
  run: mvn package -DskipTests -Dspring.profiles.active=ci
```

## How Spring Profiles Work
- **Default**: Uses `application.yml` (auto-index-creation enabled) for local development
- **CI**: Uses `application-ci.yml` when Maven flag `-Dspring.profiles.active=ci` is set
- **Local tests**: Still use default profile with `docker-compose up`

## Verification Steps
To verify the fix works:

1. **In GitHub Actions**: Build should now complete successfully without MongoDB errors
2. **Locally**: No changes needed - development still works with `docker-compose up` + `mvn test`

## Future Improvements (Optional)
If you want to run tests in CI/CD with actual MongoDB:
- Add `testcontainers` dependency for embedded MongoDB
- Configure test profile to use TestContainers MongoDB
- Example: See https://www.testcontainers.org/modules/databases/mongodb/
