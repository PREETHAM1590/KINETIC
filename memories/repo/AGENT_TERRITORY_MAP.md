# KINETIC — Agent Territory Map
# READ THIS before touching ANY file. Respect boundaries strictly.
# Last updated: 2026-04-09

## TERRITORY RULES
- Each agent owns its folder EXCLUSIVELY
- NEVER write to another agent's folder
- ONLY shared file all agents may update: `kinetic/memories/repo/MULTI_AGENT_STATUS.md`
- Use APPEND-ONLY updates to MULTI_AGENT_STATUS.md (never overwrite other sections)

---

## 🟢 CLIENT APP AGENT(S)
**Owner:** client-app-orchestrator + phase2-implementation
**Folder:** `D:\Android PROJECTS\kinetic\`
**Package:** `com.kinetic.app`
**Branch:** `feature/kinetic-phase2-client-app`
**Files owned:** Everything under `kinetic/app/src/`
**DO NOT TOUCH:** Any other project folder

---

## 🟣 TRAINER APP AGENT(S)
**Owner:** trainer-app-orchestrator
**Folder:** `D:\Android PROJECTS\kinetic-trainer\`
**Package:** `com.kinetic.trainer`
**Files owned:** Everything under `kinetic-trainer/`
**DO NOT TOUCH:** `kinetic/`, `kinetic-admin/`, `kinetic-backend/`
**NOTE:** Theme is IDENTICAL to client app — already scaffolded at:
  - `kinetic-trainer/app/src/main/java/com/kinetic/trainer/ui/theme/` ✅
  - `kinetic-trainer/app/src/main/java/com/kinetic/trainer/ui/components/CommonComponents.kt` ✅
  - `kinetic-trainer/app/src/main/java/com/kinetic/trainer/data/models/TrainerModels.kt` ✅
  - `kinetic-trainer/app/src/main/java/com/kinetic/trainer/domain/TrainerInsightEngine.kt` ✅
  - `kinetic-trainer/app/src/main/java/com/kinetic/trainer/data/repository/TrainerRepository.kt` ✅
  - `kinetic-trainer/app/src/main/java/com/kinetic/trainer/ui/viewmodels/TrainerViewModels.kt` ✅
**STILL NEEDED:**
  - `di/AppModule.kt` (Hilt bindings)
  - `TrainerApplication.kt`
  - `MainActivity.kt`
  - `TrainerMessagingService.kt`
  - `ui/navigation/Screen.kt` + `NavGraph.kt`
  - `ui/screens/HomeScreen.kt`
  - `ui/screens/ClientDetailScreen.kt`
  - `ui/screens/WorkoutAssignmentScreen.kt`
  - `ui/screens/ChatScreen.kt`
  - `ui/components/TrainerInsightBanner.kt`
  - `app/src/test/.../TrainerInsightEngineTest.kt`

---

## 🔵 ADMIN DASHBOARD AGENT(S)
**Owner:** admin-dashboard-orchestrator
**Folder:** `D:\Android PROJECTS\kinetic-admin\`
**Files owned:** Everything under `kinetic-admin/`
**DO NOT TOUCH:** `kinetic/`, `kinetic-trainer/`, `kinetic-backend/`
**NOTE:** Base Next.js project created ✅, all deps installed ✅

---

## 🟡 BACKEND AGENT(S)
**Owner:** backend-orchestrator + fastapi-builder + firebase-builder
**Folder:** `D:\Android PROJECTS\kinetic-backend\`
**Sub-territories:**
  - fastapi-builder → `kinetic-backend/fastapi-service/` ONLY
  - firebase-builder → `kinetic-backend/firebase-functions/` ONLY
**DO NOT TOUCH:** `kinetic/`, `kinetic-trainer/`, `kinetic-admin/`

---

## 🔴 AI MODEL AGENT(S)
**Owner:** ai-model-orchestrator + calorie-ai-research-agent + data-pipeline-agent + training-pipeline-agent + fastapi-integration-agent + agent-a-ifct-data + agent-b-prompts-trainer + agent-c-health-spec-monitoring
**Folder:** `D:\Android PROJECTS\kinetic-backend\ai-layer\`
**Sub-territories:**
  - data-pipeline-agent → `ai-layer/data_pipeline/` ONLY
  - training-pipeline-agent → `ai-layer/training/` ONLY
  - fastapi-integration-agent → `ai-layer/fastapi_integration/` ONLY
  - agent-a-ifct-data → `ai-layer/data/` ONLY
  - agent-b-prompts-trainer → `ai-layer/prompts/` + `ai-layer/trainer_service/` ONLY
  - agent-c-health-spec-monitoring → `ai-layer/health/` + `ai-layer/docs/` + `ai-layer/monitoring/` ONLY
**DO NOT TOUCH:** Any other folder except `ai-layer/`

---

## 📋 SHARED MEMORY (all agents APPEND to these)
- `kinetic/memories/repo/MULTI_AGENT_STATUS.md` — task completion log
- `kinetic/memories/repo/CURRENT_STATUS.md` — overall project status
- `kinetic/memories/repo/PRODUCT_BRAINSTORM_DECISIONS.md` — READ ONLY (decisions locked)

## MEMORY UPDATE FORMAT
When completing a task, APPEND to MULTI_AGENT_STATUS.md:
```
| [Task Name] | ✅ DONE | [agent-name] | [timestamp] |
```

---

## CONFLICT RESOLUTION
If two agents need to write the same file:
1. The territory owner wins
2. Other agent documents the change needed in MULTI_AGENT_STATUS.md
3. Territory owner picks it up

## API CONTRACTS (shared, do not change without coordinating)
FastAPI endpoints (backend-agent owns):
  POST /scan → {results: [{label, confidence, region_adjusted_name}]}
  POST /scan-treadmill → {speed_kmh, incline_pct, duration_mins, estimated_steps, estimated_calories}
  POST /ai-trainer/workout-plan → WorkoutPlan
  POST /ai-trainer/diet-plan → DietPlan

Firebase CF endpoints (firebase-agent owns):
  GET /nutrition?dish&state&district → {calories, protein_g, carbs_g, fat_g, fiber_g, region_note}
  POST /booking → {bookingId, status, orderId?}
  POST /membership → {success, nextBillingDate}
  POST /chat-message → {messageId, deliveredAt}
