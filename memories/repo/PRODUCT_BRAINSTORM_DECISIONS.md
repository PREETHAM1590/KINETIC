# KINETIC — Full Product Brainstorm Decisions
# Session: 2026-04-09 | Status: COMPLETE — All decisions made, ready to build

> **AI HANDOFF NOTE:** Read this BEFORE touching any plan or code.
> All decisions below are USER-APPROVED. Do NOT re-ask these questions.
> Token tip: This file + CURRENT_STATUS.md = full context in 5 minutes.

---

## BUSINESS MODEL

**White-label SaaS (C)**
- KINETIC is sold TO gyms who rebrand it
- "PowerGym by KINETIC" — each gym gets custom-branded version
- Each gym = separate deployment with their own gymId in Firestore
- Multi-branch support (chain gyms — Gold's, Cult.fit style)
- Admin dashboard shows: switch between branches OR all branches

---

## FULL ECOSYSTEM — 4 PRODUCTS

```
1. KINETIC Client App (Android — existing, extending)
2. KINETIC Trainer App (Android — new, separate APK)
3. KINETIC Admin Dashboard (Next.js web — new)
4. KINETIC Backend (FastAPI + Firebase — new)
```

---

## PRODUCT 1 — CLIENT APP

### User Modes (3 tiers, set by membership purchase)
| Mode | How Activated | What They Get |
|---|---|---|
| PT Mode | Gym assigns trainer (purchased PT package) | Trainer-assigned workouts + chat |
| Self-Guided | Client chooses | Pre-built templates + customize |
| Full AI | Client chooses anytime | AI generates complete workout + diet |
> Client can freely toggle Self-Guided ↔ Full AI. PT Mode only when trainer assigned.

### Goal-Based Onboarding (KEYSTONE — feeds everything)
```
User picks ONE goal:
Weight Loss / Muscle Gain / Endurance / Rehabilitation / General Fitness

→ Sets targetCalories in UserActivityStore (replaces hardcoded 2500)
→ Determines default workout template type
→ Changes CoachEngine message tone
→ Adjusts macro targets (protein higher for muscle gain)
→ Highlights relevant membership features
→ Sets BMI context (South Asian cutoffs: 23, not 25)
```

### Food Scanner (AICalorieScannerScreen — full rebuild)
**Architecture: Two-hop**
```
Android (image + region hint)
    ↓ POST /scan
FastAPI (Python) — Gemini Vision → top 3 food labels + confidence %
    
Android (food label + district)
    ↓ GET /nutrition?dish=X&state=Y&district=Z  
Firebase CF — IFCT 2017 base + Gemini regional adjustment + Firestore cache
    → calories + macros
```

**NO barcode scanner** — Gemini Vision handles all:
- Home-cooked Indian food ✅
- Restaurant/street food ✅
- Packaged food (reads label text) ✅
- Mixed plate ✅

**Regional Intelligence:**
- Hybrid: GPS for travel context + profile for home region
- District-level granularity (700+ districts)
- Data: IFCT 2017 base → LLM regional adjustment → crowd-sourced (gradual)
- Prompt pattern: "User in [district], [state]. Dish: [X]. Adjust IFCT base for local preparation."

**Fallback UX:**
```
AI scans → top 3 guesses with confidence bars
User confirms OR taps "None of these?" → search box
```

**UI: Fullscreen bottom-sheet (Google Lens pattern)**
```
[📍 Tirunelveli, Tamil Nadu]   ← RegionContextChip (top-center)
[       LIVE CameraX           ]  ← fills entire screen
[  ┌──┐  Lime brackets  ┌──┐  ]
[  ──── lime scan line ────    ]
[      [ SCAN MEAL ] FAB       ]  ← Idle state only

Results (bottom sheet slides up):
┌─────────────────────────────┐
│ Pesarattu  82%  ████████    │
│ Dosa       11%  ██          │
│ Uttapam     7%  █           │
│ ──────────────────────────  │
│ None of these?  Search →    │
└─────────────────────────────┘
```

**Scanner State Machine:**
`Idle → Capturing → Analyzing → Results → Confirming → Logged`
`                                        ↘ Searching → Confirming ↗`

### Treadmill Intelligence (3-tier)
```
Tier 1: Wearable present → Google Health Connect auto-syncs (steps, HR, calories)
Tier 2: No wearable → camera scans treadmill display
         Gemini Vision reads: speed (km/h) + incline (%) + duration (mins)
         Formula: steps ≈ (speed × duration × stride_factor) / incline_adjustment
Tier 3: Manual → user enters speed + incline + duration → same formula
```

### Google Health Connect (Wearables)
- Syncs from ANY Android wearable (Fitbit, Galaxy Watch, Garmin, Mi Band)
- Steps, heart rate, sleep quality → feed into CoachEngine
- "Your resting HR is elevated today — consider a lighter session"

### Gym Services (Dynamic — membership-gated)
```
ServiceItem schema:
{
  id, name, icon, category,
  bookingType: INSTANT | APPROVAL | PREPAID,
  includedInTiers: ["premium", "elite"],
  payPerUsePrice: 199,  // ₹ if not in membership tier
  duration, capacity
}
```
- Admin adds service → client sees it instantly (no app update)
- Membership tier check: included → book free; not included → pay-per-use
- Categories: Classes / Facilities / Trainer Sessions / Shop
- BookingType set per-service by admin

### Membership Pause
- Client can pause for X days, resumes automatically
- Admin can pause from dashboard too

### Trainer Feedback
- Client rates/reviews their assigned trainer after sessions

### Progress Photos
- Weekly/monthly upload
- Visible to client + assigned trainer only
- Encrypted storage (Firebase Storage)
- Side-by-side comparison view

### Injury / Limitation Tracking
- Client flags: "bad knee", "shoulder injury", "lower back pain"
- AI trainer + templates auto-exclude/modify those exercises
- Trainer app shows limitations as warning badge on client card
- Critical for safety + gym legal liability

### Offline Mode
- Active workout works fully offline (gym basements = no signal)
- Workout plan cached locally on session load
- Sets/reps/weight recorded locally
- Syncs to Firestore when connection returns

### Language Support
- Phase 1: Hindi + English
- Phase 2: Tamil, Telugu, Kannada, Malayalam, Bengali
- strings.xml localization (Android standard)
- Food scanner returns dish names in user's preferred language

### South Asian Health Context
```
BMI obesity cutoff: 23 (not 25) for South Asians
Higher visceral fat risk at lower BMI
Higher T2 diabetes + PCOD prevalence
CoachEngine aware: "Your BMI is 24 — borderline for South Asian guidelines"
Macro recommendations adjusted for South Asian diet patterns
```

### E2E Encrypted Chat (Client ↔ Trainer)
- Signal Protocol (libsignal-protocol-java) — same as WhatsApp, open source, free
- Keys generated on device, stored in EncryptedSharedPreferences
- Only ciphertext stored in Firestore — KINETIC servers never see messages
- Progress photos + workout notes + chat in one place

---

## PRODUCT 2 — TRAINER APP (Separate APK)

### Home Screen (C — split layout)
```
┌─────────────────────────────────────┐
│  TODAY'S ACTIVITY FEED              │  ← top: what happened today across all clients
│  "Priya completed Leg Day ✅"        │
│  "Rahul missed workout ❌"           │
│  "Arjun hit new squat PR 💪"        │
├─────────────────────────────────────┤
│  CLIENT ROSTER                      │  ← bottom: all assigned clients
│  [Priya] [Rahul] [Arjun] [Meena]   │
└─────────────────────────────────────┘
```

### Client Detail (C — dashboard cards)
- Workout card: assigned vs actual (prescribed sets/reps/weight vs what client did)
- Diet card: calorie log, macro trends, meal scan history
- Metrics card: weight, body fat %, measurements (manually entered by client)
- Consistency card: streak, attendance rate, missed sessions pattern
- Injury/limitations warning badge
- Progress photos (encrypted)

### Workout Assignment (D — all three methods)
- Templates: reusable programs (PPL Day 1, Beginner Full Body)
- Clone: duplicate last week + adjust targets for progression
- Scratch: fully custom for one-off sessions

### Workout Tracking (Assigned vs Actual)
```
Trainer assigns: Squat 3×8 @ 60kg
Client performs: Squat 3×10 @ 65kg (exceeded — good)
Trainer sees: prescribed vs actual — not live video, just data
Synced via Firestore after client completes session
```

### TrainerInsightEngine (mirrors CoachEngine)
```
Same architecture: rules → TrainerViewModel → TrainerInsightBanner

Rules:
A — Risk: "Priya's performance dropped 3 sessions in a row — at risk of dropout"
B — Programming: "Arjun exceeded squat target 4× in a row — increase target"
C — Workload: "3 clients have no workout assigned this week"
```

### Notifications (C — smart filtering)
- Push for CRITICAL only: missed 2+ sessions, new personal best, client message
- Everything else in activity feed
- Trainer controls which events push in Settings

### E2E Encrypted Chat (same Signal Protocol as client app)

---

## PRODUCT 3 — ADMIN DASHBOARD (Next.js, white-label)

### Tech Stack
- **Frontend:** Next.js (App Router, client components)
- **Hosting:** Vercel (zero infra management)
- **Backend:** Firebase CF + Firestore (shared with app)
- **Auth:** Firebase Auth (role-based: owner / manager / trainer)

### Home Screen (D — unified summary)
```
┌──────────────────────────────────────────────────┐
│  3 CRITICAL NUMBERS                              │
│  [5 pending approvals] [3 expiring this week]   │
│  [2 clients unassigned]                          │
├──────────────────────────┬───────────────────────┤
│  MEMBERSHIP ALERTS       │  TRAINER WORKLOAD     │
│  Priya expires in 3d     │  Raj: 12 clients      │
│  Rahul hasn't visited    │  Meena: 4 clients     │
└──────────────────────────┴───────────────────────┘
```

### Sections
1. **Memberships** — active/expired/paused, tier changes, revenue, auto-renewal
2. **Bookings** — pending approvals, today's facility schedule, capacity management
3. **Trainers** — assign to clients, workload balancing, trainer performance
4. **Services Catalog** — add/edit/disable services (dynamic → client sees instantly)
5. **Revenue Analytics:**
   - MRR (monthly recurring revenue)
   - Churn rate + at-risk members (no visit 14+ days)
   - Most booked services
   - Trainer retention rate (which trainer keeps clients longest)
   - Peak hours heatmap
6. **Gym Entry** — biometrics/face scan sync (attendance log)
7. **Multi-branch** — switch between branches or see all (chain gyms)

### White-label
- Gym uploads logo, sets brand colors
- Client app + trainer app + dashboard all rebrand automatically
- Each gym = separate deployment (separate gymId, separate Firebase project or tenant)

---

## PRODUCT 4 — BACKEND

### FastAPI (Python)
```
POST /scan
  Input: image (JPEG), region_hint {state, district}
  Process: Gemini Vision API → top 3 food labels + confidence
  Output: [{label, confidence, region_adjusted_name}]

POST /scan-treadmill
  Input: image (JPEG of treadmill display)
  Process: Gemini Vision → extract speed, incline, duration
  Output: {speed_kmh, incline_pct, duration_mins, estimated_steps, calories}

Future: swap Gemini Vision for custom-trained MobileNetV3
        (same endpoint contract — Android never changes)
```

### Firebase Cloud Functions
```
GET /nutrition?dish=X&state=Y&district=Z
  1. Check Firestore cache (dish + region)
  2. Cache miss → lookup IFCT 2017 base data
  3. Call Gemini Pro with regional context prompt
  4. Store in cache → return {calories, protein, carbs, fat, fiber}

POST /booking → booking management
POST /membership → pause/renew/upgrade
POST /chat-message → store E2E ciphertext
```

### Firestore Structure
```
gyms/{gymId}/
  members/{userId}
  trainers/{trainerId}
  services/{serviceId}
  bookings/{bookingId}
  analytics/{date}

users/{userId}/
  profile, goal, region, injuryFlags, membershipTier
  workouts/{sessionId}
  meals/{mealId}
  messages/{conversationId}

nutrition_cache/{dish_district}/
  calories, protein, carbs, fat, last_updated
```

### Payments — Razorpay
- Membership renewals (auto-debit)
- PT package purchase
- Pay-per-use service booking
- In-app supplement shop
- UPI support (mandatory for India)

---

## AI LAYER — 4 MODELS

| Model | MVP | Long-term |
|---|---|---|
| Food Vision | Gemini Vision API | Fine-tuned MobileNetV3 on Indian food |
| Regional Nutrition | Gemini Pro prompts | Fine-tune on crowd-sourced corrections |
| AI Personal Trainer | Gemini Pro structured prompts | Fine-tune on KINETIC user progression data |
| CoachEngine / TrainerInsightEngine | Rule-based (already built) | ML anomaly detection |

### Separate Training Agent (future)
```
ResearchAgent (autonomous):
├── Search Kaggle/HuggingFace for Indian food datasets
├── Download + preprocess
├── Fine-tune MobileNetV3
├── Evaluate accuracy
└── Export → push to FastAPI
Runs independently of app build
```

---

## BUILD ORDER (Phased)

```
PHASE 1 (Android — already done):
✅ CoachEngine + CoachBanner + UserActivityStore + screen integrations

PHASE 2 (Android — next):
├── Goal-based Onboarding redesign
├── Food Scanner full rebuild (AICalorieScannerScreen)
├── Treadmill scan feature
├── Google Health Connect integration
├── Injury/limitation tracking
├── Predictive Reports + Behavior-based Membership upgrades
└── Active Workout Intelligence (adaptive rest, last session ghost)

PHASE 3 (Backend):
├── FastAPI setup + Gemini Vision integration
├── Firebase CF + IFCT 2017 nutrition lookup
├── Firestore schema + rules
└── Razorpay payment integration

PHASE 4 (Trainer App):
├── New Android project
├── Home feed + client roster
├── Client detail dashboard
├── Workout assignment (template/clone/scratch)
├── TrainerInsightEngine
└── E2E chat

PHASE 5 (Admin Dashboard):
├── Next.js project setup
├── Firebase Auth + role-based access
├── All admin sections
├── Revenue analytics
└── White-label branding system

PHASE 6 (Advanced):
├── Multi-language (Hindi first)
├── Offline mode hardening
├── Progress photos
├── Biometrics/face entry
├── Custom food model training (separate agent)
└── Crowd-sourced nutrition corrections
```

---

## KEY TECHNICAL DECISIONS SUMMARY

| Decision | Choice | Reason |
|---|---|---|
| Food recognition | Gemini Vision (no barcode) | Handles all food types including unlabeled Indian |
| Regional data | IFCT 2017 + LLM adjustment + crowd-source | Most accurate for Indian food available |
| Regional granularity | District-level (700+) | Maximum accuracy for hyper-local dishes |
| Fallback UX | Top 3 + search | Best of both, minimal friction |
| Scanner UI | Fullscreen bottom-sheet | Google Lens pattern, most immersive |
| Backend vision | FastAPI (Python) | ML integration path for future custom model |
| Backend data | Firebase CF | Decoupled, callable by trainer app + dashboard |
| Admin frontend | Next.js on Vercel | Firebase ecosystem, zero infra |
| Chat | In-app E2E (Signal Protocol) | Own the experience, zero per-message cost |
| Business model | White-label SaaS | Gyms pay, rebrand, own their users |
| User modes | 3 tiers (PT/Self-guided/AI) | Membership-driven, client-controlled |
| Payments | Razorpay + UPI | India-standard |

---

*Last updated: 2026-04-09*
*All decisions user-approved. No re-confirmation needed.*
