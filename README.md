# Ride Price Matcher

An Android application that helps users identify ride offers whose visible price matches user-defined price rules. Built with Kotlin, Jetpack Compose, and Supabase.

## Architecture

- **UI Layer**: Jetpack Compose with Material3, dark/light mode support
- **ViewModel Layer**: StateFlow-based MVVM
- **Domain Layer**: Price parsing, text normalization, acceptance phrase matching
- **Data Layer**: Supabase remote + Room local cache
- **Services**: AccessibilityService (text observation), OverlayService (match notifications), ScreenCaptureService (optional)

## Setup

1. Clone the repository
2. Create `local.properties` in the project root:
   ```
   SUPABASE_ANON_KEY=your_publishable_key_here
   ```
3. Sync Gradle and build

## Supabase Configuration

- URL: `https://rhwpbnzbevufolojjimh.supabase.co`
- Use only the **publishable anon key** in the Android client
- **Never** embed the service-role/secret key
- Enable RLS on all tables
- Use Edge Functions for privileged admin operations

## Database Tables

- `profiles` — user profiles with role and blocked status
- `entitlements` — subscription state (server-authoritative)
- `rewarded_ad_rewards` — ad watch history
- `languages` — supported language configurations
- `phrases` — acceptance phrases per language
- `user_preferences` — price rules and settings
- `audit_logs` — admin action history

## Security

- No automatic ride acceptance
- No hidden click injection
- No secret keys in APK
- Local processing preferred
- Server is authoritative for entitlements

## Testing

```bash
./gradlew test          # Unit tests
./gradlew lint          # Static analysis
./gradlew assembleDebug # Build debug APK
```

## License

Proprietary — All rights reserved.
