# App Bootstrap Process

### Phase 1: Integrity & Schema Validation
For each file (`user.json`, `device.json`, `configs.json`)
- Check existence
- Validate JSON format
- Validate schema
- If invalid:
    - Attempt recovery (last known good)
    - Else mark as corrupted

### Phase 2: Load Application Metadata
- Read `app.properties`
- Validate:
    - Version format
    - Module version compatability

### Phase 3: Version & Update Check
- Try server version check
- cases:
    - Up-to-date &rarr; continue
    - Optional update &rarr; notify user
    - Mandatory update &rarr; block app until updated
    - Network failure &rarr; continue in offline mode

### Phase 4: Configuration Initialization
- If configs.json missing:
    - Create with defaults
- If present:
    - Validate schema
    - Apply defaults for missing fields

### Phase 5: User State Resolution
Based on user.json:
- File missing → First Run
- Exists + valid:
    - login_status = false → Logged out
    - login_status = true → Logged in
- Invalid/corrupted → Recovery flow

Also:
- Validate `auth_token` (expiry / server check)

### Phase 6: Device Context Setup
- Load device.json
- Validate workspace/device binding
- If mismatch:
    - Re-sync with server OR
    - Reset device state

---