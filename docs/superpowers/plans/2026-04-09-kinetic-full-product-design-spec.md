# KINETIC — Full Product Design Specification
# Version: 1.0 | Date: 2026-04-09 | Status: APPROVED — Ready for implementation

---

## 1. PRODUCT OVERVIEW

KINETIC is a **white-label fitness platform** sold to gyms. Each gym deploys a branded version.

### Ecosystem
```
┌─────────────────────────────────────────────────────────────┐
│                     KINETIC ECOSYSTEM                       │
├─────────────────┬──────────────────┬────────────────────────┤
│  CLIENT APP     │  TRAINER APP     │  ADMIN DASHBOARD       │
│  Android Kotlin │  Android Kotlin  │  Next.js (Vercel)      │
│  (existing)     │  (new APK)       │  (new)                 │
├─────────────────┴──────────────────┴────────────────────────┤
│                     BACKEND                                  │
│  FastAPI (Python) ──────── Firebase CF (Node/Python)        │
│  Gemini Vision API         Firestore + Storage + Auth        │
│  Custom ML (future)        Razorpay Payments                 │
└─────────────────────────────────────────────────────────────┘
```

---

## 2. CLIENT APP — DESIGN SPEC

### 2.1 Onboarding Screen (NEW — highest priority)

**Purpose:** Set user context that personalizes the entire app.

**Flow:**
```
Screen 1: Welcome + gym branding
Screen 2: Goal selection (ONE pick)
  ○ Weight Loss     → calorie deficit mode, cardio-heavy templates
  ○ Muscle Gain     → calorie surplus mode, strength templates, high protein
  ○ Endurance       → maintenance calories, cardio/HIIT templates
  ○ Rehabilitation  → conservative templates, injury-aware defaults
  ○ General Fitness → balanced defaults

Screen 3: Basic profile
  Name, age, weight (kg), height (cm)
  → BMI calculated using South Asian cutoffs (23 = overweight, not 25)

Screen 4: Region selection
  Home state → home district (stored in UserProfile)
  Used for regional food database

Screen 5: Mode selection
  (only if no trainer assigned)
  ○ Self-Guided — browse + customize templates
  ○ Full AI — AI generates everything for me

Screen 6: Optional wearable connect
  "Connect Google Health" → Health Connect permission
```

**Data written to Firestore + UserActivityStore:**
```kotlin
UserProfile {
  goal: GoalType,
  targetCalories: Int,       // calculated from goal + profile
  targetProteinG: Int,       // calculated from goal
  homeState: String,
  homeDistrict: String,
  weightKg: Float,
  heightCm: Float,
  ageYears: Int,
  mode: UserMode             // SELF_GUIDED | FULL_AI | PT_ASSIGNED
}
```

---

### 2.2 Food Scanner — AICalorieScannerScreen (FULL REBUILD)

**Architecture:**
```
Android → POST /scan (image + region hint)
    FastAPI → Gemini Vision → top 3 food labels + confidence
Android → GET /nutrition (dish + state + district)
    Firebase CF → IFCT 2017 + Gemini regional adjustment → macros
```

**State Machine:**
```
Idle → Capturing → Analyzing → Results → Confirming → Logged
                                        ↘ Searching → Confirming ↗
```

**UI Components:**

`RegionContextChip` — floating top-center
```
[📍 Tirunelveli, Tamil Nadu]
Tappable → opens region selector sheet
```

`ScannerOverlay` — layered on CameraX
```
Lime corner brackets (Canvas, animated)
Lime scan line (existing ScanningLineAnimation — keep)
[ SCAN MEAL ] FAB — center bottom, Idle state only
```

`ResultsBottomSheet` — slides up over frozen camera frame
```kotlin
data class FoodResult(
  val label: String,
  val localizedName: String,     // in user's language
  val confidencePct: Int,
  val calories: Int,
  val protein: Float,
  val carbs: Float,
  val fat: Float,
  val portionSizeG: Int,         // default portion for this dish in this region
  val regionNote: String?        // "Tirunelveli style — higher ghee content"
)
```

`SearchFallbackSheet` — shown when "None of these?" tapped
```
Search box + results from IFCT + regional DB
```

`PortionConfirmSheet` — shown after food confirmed
```
"How much did you eat?"
[slider or quick-tap: Half / Normal / Large]
→ adjusts calories proportionally
→ [ LOG TO DIET ] button
```

**ViewModel — AIScannerViewModel:**
```kotlin
sealed class ScannerState {
  object Idle : ScannerState()
  object Capturing : ScannerState()
  object Analyzing : ScannerState()
  data class Results(val items: List<FoodResult>) : ScannerState()
  object Searching : ScannerState()
  data class Confirming(val selected: FoodResult) : ScannerState()
  object Logged : ScannerState()
}
```

**Regional Context:**
```kotlin
data class RegionContext(
  val homeState: String,           // from UserProfile
  val homeDistrict: String,        // from UserProfile
  val currentGpsState: String?,    // from GPS if traveling
  val currentGpsDistrict: String?, // from GPS if traveling
  val activeRegion: String         // GPS if traveling, home if not
)
```

---

### 2.3 Treadmill Scanner

**Entry:** "Log Cardio" → "Treadmill" → camera icon

**Flow:**
```
Camera opens → user points at treadmill display
Gemini Vision reads: speed (km/h), incline (%), time (mins)
App calculates:
  steps ≈ (speed_kmh × duration_mins × 1000) / (60 × stride_length_m)
  calories = MET × weight_kg × duration_hrs
  (MET varies by speed + incline)
Shows: "X steps, Y cal burned — does this look right?"
User confirms → logged to workout session
```

**Fallback:** Manual entry if scan fails.

---

### 2.4 Gym Services Screen (NEW)

**Route:** `Screen.Services`

**Layout:**
```
Top: Category tabs [Classes] [Facilities] [Trainers] [Shop]
Body: ServiceCard grid per category

ServiceCard:
┌──────────────────────────────┐
│ [icon]  Steam Room           │
│         30 min slots         │
│  ✅ Included in your plan    │  ← OR "₹199 per session"
│         [ BOOK SLOT ]        │
└──────────────────────────────┘
```

**Booking flow (per bookingType):**
- `INSTANT` → tap Book → pick slot → confirmed immediately
- `APPROVAL` → tap Book → pick slot → "Pending gym approval" → push when approved
- `PREPAID` → tap Book → pick slot → Razorpay payment → auto-confirmed

**Membership gate:**
```kotlin
fun canBookFree(service: ServiceItem, membershipTier: String): Boolean {
  return service.includedInTiers.contains(membershipTier)
}
// false → show pay-per-use price → Razorpay flow
```

---

### 2.5 Workout Modes

**Self-Guided:**
```
WorkoutsScreen → Browse Templates (by goal type, muscle group, duration)
              → Customize: add/remove/replace exercises
              → Save as "My Workout"
              → Start → ActiveWorkoutScreen
```

**Full AI Mode:**
```
WorkoutsScreen → "Generate My Plan" button (prominent)
AI generates weekly plan:
  Input: goal, current lifts, available days, equipment, injury flags
  Output: structured weekly program → rendered as workout cards
User can modify → Start session
```

**PT Mode:**
```
WorkoutsScreen → "This Week's Plan" (assigned by trainer)
Shows assigned exercises with trainer's targets
Client MUST do minimum — CAN exceed (more reps, more weight)
Actual vs assigned synced to trainer app via Firestore
```

---

### 2.6 Chat (E2E Encrypted)

**Signal Protocol implementation:**
```
1. On first launch: generate identity key pair, store in EncryptedSharedPreferences
2. Upload public key to Firestore users/{userId}/publicKey
3. On first message to trainer: fetch trainer's public key, establish session
4. All messages encrypted on device before writing to Firestore
5. Only ciphertext stored: messages/{conversationId}/msgs/{msgId}/ciphertext
6. Recipient decrypts on device using their private key
```

**UI:** Simple chat screen — text + photo attachment. No voice.

---

### 2.7 Injury Tracking

**Profile screen → "Manage Limitations":**
```
[+ Add Limitation] 
Knee (left/right/both) | Shoulder | Lower Back | Wrist | Hip | Ankle
Severity: Minor / Moderate / Cannot use
Duration: Temporary (select end date) / Permanent
```

**Effect on AI/Templates:**
```kotlin
// In workout generation:
fun filterExercises(exercises: List<Exercise>, injuries: List<InjuryFlag>): List<Exercise> {
  return exercises.filter { exercise ->
    injuries.none { injury -> exercise.contraindicatedFor.contains(injury.bodyPart) }
  }
}
```

---

## 3. TRAINER APP — DESIGN SPEC

### 3.1 Home Screen
```
┌─────────────────────────────────────────────────────┐
│  TrainerInsightBanner (AI alerts for trainer)       │
├─────────────────────────────────────────────────────┤
│  TODAY'S ACTIVITY FEED                              │
│  ┌─────────────────────────────────────────────┐   │
│  │ ✅ Priya  Leg Day completed — all targets hit │  │
│  │ ❌ Rahul  Missed Push Day                    │  │
│  │ 💪 Arjun  Squat PR: 80kg (+10kg)            │  │
│  └─────────────────────────────────────────────┘   │
├─────────────────────────────────────────────────────┤
│  MY CLIENTS (roster)                                │
│  [Priya ✅] [Rahul ❌] [Arjun 💪] [Meena ⏳]       │
└─────────────────────────────────────────────────────┘
```

### 3.2 Client Detail Screen
```
┌─────────────────────────────────────────────────────┐
│  [Photo]  Priya Sharma, 28                         │
│  🔥 14-day streak  |  Score: 82/100                │
│  ⚠️ Left knee — minor limitation                  │
├───────────────┬─────────────────────────────────────┤
│ WORKOUT CARD  │  DIET CARD                          │
│ Last: Leg Day │  Today: 1840/2200 cal              │
│ 8/10 sets ✅  │  Protein: 142g ✅                  │
├───────────────┴─────────────────────────────────────┤
│ METRICS CARD           │  CONSISTENCY CARD          │
│ 68kg → 65kg (3 wks)   │  Attendance: 87%           │
│ [Progress Photos]      │  Missed: 2 this month      │
├─────────────────────────────────────────────────────┤
│ [ ASSIGN WORKOUT ] [ MESSAGE ] [ VIEW FULL HISTORY ]│
└─────────────────────────────────────────────────────┘
```

### 3.3 Workout Assignment
```
Method A — Template:
  Browse template library → select → override targets per client → assign

Method B — Clone:
  View client's last session → duplicate → adjust targets → assign
  "Priya did Squat 3×8@55kg last week → increase to 60kg"

Method C — Scratch:
  Pick exercises → set targets → save as template (optional) → assign
```

### 3.4 TrainerInsightEngine Rules
```kotlin
// Risk detection
if (client.performanceTrend.last3Sessions == DECLINING) →
  WARNING: "Priya's performance dropping — check in"

if (client.missedSessionsInRow >= 2) →
  WARNING: "Rahul missed 2+ sessions — dropout risk"

// Programming suggestions  
if (client.exceededTargetConsecutiveSessions >= 4) →
  NUDGE: "Arjun keeps exceeding squat target — increase it"

if (client.recoveryMetric == POOR && nextSessionIsHeavy) →
  NUDGE: "Meena's recovery score low — consider deload"

// Trainer workload
if (clientsWithNoWorkoutAssigned.count >= 3) →
  INFO: "3 clients have no workout this week"

if (clientsWithNoCheckin.count >= 5) →
  INFO: "5 clients haven't had a check-in in 7+ days"
```

### 3.5 Notifications
```
PUSH (critical only):
- Client missed 2+ sessions in a row
- Client hit personal best
- Client sent a message
- Client's assigned workout is past-due (no completion logged)

IN-APP FEED (everything else):
- Workout completed
- Diet logged
- Measurements updated
- Progress photo added
```

---

## 4. ADMIN DASHBOARD — DESIGN SPEC

### 4.1 Tech Stack
```
Framework:  Next.js 14 (App Router, client components)
Hosting:    Vercel
Auth:       Firebase Auth (roles: owner | manager | trainer)
DB:         Firestore (shared with apps)
UI:         Tailwind CSS + shadcn/ui (dark theme, KINETIC brand)
```

### 4.2 Home Screen
```
┌─────────────────────────────────────────────────────────────┐
│  [GYM LOGO]  PowerGym Admin     Branch: Main ▼   [Profile] │
├─────────────────────────────────────────────────────────────┤
│  CRITICAL NUMBERS                                            │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────────┐  │
│  │  5 Pending   │  │ 3 Expiring   │  │ 2 Clients        │  │
│  │  Approvals   │  │ This Week    │  │ Unassigned       │  │
│  └──────────────┘  └──────────────┘  └──────────────────┘  │
├─────────────────────────────────────────────────────────────┤
│  MEMBERSHIP ALERTS          │  TRAINER WORKLOAD             │
│  Priya — expires in 3d      │  Raj: 12 clients              │
│  Rahul — no visit 14d       │  Meena: 4 clients             │
│  [View All Members]         │  [Manage Trainers]            │
├─────────────────────────────────────────────────────────────┤
│  TODAY'S BOOKINGS           │  REVENUE (this month)         │
│  Steam: 8/10 slots          │  MRR: ₹2,40,000              │
│  Pool: 3/6 slots            │  New: ₹18,000                 │
│  Classes: 2 pending ✅      │  Churn risk: 5 members        │
└─────────────────────────────────────────────────────────────┘
```

### 4.3 Services Catalog (Dynamic)
```
Admin adds new service:
  Name: Ice Bath
  Category: Facilities
  Icon: 🧊 (select from library)
  Duration: 15 min
  Capacity: 2 per slot
  Booking type: APPROVAL
  Included in tiers: [Elite]
  Pay-per-use price: ₹299
  → Save → instantly visible in client app (no app update)
```

### 4.4 Revenue Analytics
```
Charts:
- MRR trend (monthly bar chart)
- Churn rate % (line chart)
- At-risk members (no visit 14+ days) — table with action buttons
- Most booked services (horizontal bar)
- Trainer retention rate (which trainer clients stay longest)
- Peak hours heatmap (7-day × 24-hour grid)
```

### 4.5 Gym Entry (Biometrics/Face)
```
Entry device (tablet/Pi at door) → face scan → Firebase CF
→ logs attendance: users/{userId}/attendance/{date}
→ updates lastVisitTimestamp
→ client app + trainer app shows "Last visit: Today"
→ admin dashboard at-risk detection updated
```

### 4.6 White-label Branding
```
Admin uploads:
  - Gym logo (SVG/PNG)
  - Primary color (replaces Lime #D1FF26)
  - App name ("PowerGym")
  
Stored in: gyms/{gymId}/branding
Client app + trainer app reads on launch → applies theme
Dashboard header shows gym logo
```

---

## 5. BACKEND — DESIGN SPEC

### 5.1 FastAPI Endpoints
```python
POST /scan
  body: { image: base64, region: { state, district } }
  → calls Gemini Vision
  → returns: { results: [{ label, localizedName, confidence, regionNote }] }

POST /scan-treadmill
  body: { image: base64, userWeightKg: float }
  → calls Gemini Vision (read display)
  → calculates steps + calories
  → returns: { speed_kmh, incline_pct, duration_mins, steps, calories_burned }

GET /health → { status: "ok" }
```

### 5.2 Firebase Cloud Functions
```
GET  /nutrition?dish=X&state=Y&district=Z
POST /booking
GET  /bookings?gymId=X&date=Y
POST /membership/pause
POST /membership/upgrade
POST /payment/create-order   → Razorpay order
POST /payment/verify         → Razorpay webhook
POST /chat/message           → store ciphertext
GET  /chat/messages          → fetch for conversation
POST /attendance/log         → gym entry event
```

### 5.3 Firestore Schema
```
gyms/{gymId}
  ├── profile: { name, branding, branches[] }
  ├── members/{userId}: { tier, pausedUntil, trainerId }
  ├── trainers/{trainerId}: { name, clientIds[], specializations }
  ├── services/{serviceId}: { ServiceItem schema }
  ├── bookings/{bookingId}: { serviceId, userId, slot, status }
  └── analytics/{YYYY-MM}: { revenue, churn, attendance_heatmap }

users/{userId}
  ├── profile: { UserProfile schema }
  ├── publicKey: { signalPublicKey }
  ├── workouts/{sessionId}: { exercises, sets, assigned vs actual }
  ├── meals/{mealId}: { dish, calories, macros, region }
  ├── metrics/{date}: { weight, bodyFat, measurements }
  ├── attendance/{date}: { checkedIn: bool, timestamp }
  └── messages/{conversationId}/msgs/{msgId}: { ciphertext, timestamp }

nutrition_cache/{dish}_{district}
  ├── calories, protein, carbs, fat, fiber
  └── last_updated, source: "IFCT+LLM"
```

### 5.4 Payments (Razorpay)
```
Flows:
1. Membership renewal → create order → Razorpay checkout → webhook → update Firestore
2. PT package purchase → same → unlock PT mode
3. Pay-per-use booking → same → confirm booking
4. Shop checkout → same → order created

UPI, cards, netbanking all handled by Razorpay SDK
```

---

## 6. AI MODELS — SPEC

### 6.1 Food Vision (Gemini Vision — MVP)
```python
# FastAPI prompt
response = gemini_vision.generate(
  image=image_bytes,
  prompt=f"""
    Identify the food in this image.
    The user is from {district}, {state}, India.
    Return JSON: {{
      "results": [
        {{"label": "...", "localized_name": "...", "confidence": 0.82,
          "region_note": "Tirunelveli style — higher ghee content"}},
        ...  // top 3
      ]
    }}
    If packaged food, read the label.
    Return dish names in {user_language}.
  """
)
```

### 6.2 Regional Nutrition (Gemini Pro — MVP)
```python
# Firebase CF prompt
response = gemini_pro.generate(
  prompt=f"""
    Base nutrition (IFCT 2017) for {dish}: {ifct_base_json}
    User is in {district}, {state}, India.
    How does typical local preparation style in this region adjust 
    the nutritional profile? Consider: oil type, cooking method, 
    ingredient ratios, portion norms.
    Return ONLY JSON: {{
      "calories_per_100g": int,
      "protein_g": float,
      "carbs_g": float,
      "fat_g": float,
      "fiber_g": float,
      "typical_portion_g": int,
      "adjustment_reason": "..."
    }}
  """
)
```

### 6.3 AI Personal Trainer (Gemini Pro — MVP)
```python
response = gemini_pro.generate(
  prompt=f"""
    Generate a {days_per_week}-day workout plan for:
    Goal: {goal}
    Current fitness: {fitness_level}
    Equipment: {equipment_available}
    Injury limitations: {injury_flags}
    Current lifts (if known): {current_lifts}
    
    Return structured JSON with exercises, sets, reps, weights, rest times.
    Week should be progressive (not same every day).
    Avoid exercises contraindicated for: {injury_flags}
  """
)
```

---

## 7. SOUTH ASIAN HEALTH CONTEXT

```kotlin
object SouthAsianHealthConstants {
  const val BMI_OVERWEIGHT_CUTOFF = 23.0f   // vs 25 Western
  const val BMI_OBESE_CUTOFF = 27.5f         // vs 30 Western
  const val HIGH_DIABETES_RISK_BMI = 21.0f   // screening threshold

  fun getBmiCategory(bmi: Float): BmiCategory = when {
    bmi < 18.5f -> BmiCategory.UNDERWEIGHT
    bmi < 23.0f -> BmiCategory.NORMAL
    bmi < 27.5f -> BmiCategory.OVERWEIGHT      // coaches message here
    else -> BmiCategory.OBESE
  }
  
  // Macro adjustments for South Asian diets
  // Higher carb baseline (rice/roti heavy), lower natural protein
  fun getTargetProtein(weightKg: Float, goal: GoalType): Int = when(goal) {
    GoalType.MUSCLE_GAIN -> (weightKg * 2.0f).toInt()
    GoalType.WEIGHT_LOSS -> (weightKg * 1.8f).toInt()
    else -> (weightKg * 1.4f).toInt()
  }
}
```

---

## 8. OFFLINE MODE SPEC

```kotlin
// WorkoutSession persists locally via Room database
@Entity data class WorkoutSessionLocal(
  val sessionId: String,
  val exercises: String,  // JSON
  val synced: Boolean = false
)

// WorkoutRepository checks connectivity
class WorkoutRepository @Inject constructor(
  private val localDb: AppDatabase,
  private val firestore: FirebaseFirestore
) {
  suspend fun saveSession(session: WorkoutSession) {
    localDb.workoutDao().insert(session.toLocal())
    if (networkAvailable()) syncToFirestore(session)
    // else: SyncWorker picks it up when connection returns
  }
}

// SyncWorker — runs when network available
class SyncWorker(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {
  override suspend fun doWork(): Result {
    val unsynced = localDb.workoutDao().getUnsynced()
    unsynced.forEach { syncToFirestore(it) }
    return Result.success()
  }
}
```

---

*Spec version: 1.0*
*Approved: 2026-04-09*
*All decisions confirmed by user. Build when ready.*
