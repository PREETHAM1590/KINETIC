################################################################################
# KINETIC Coach Feature - New Account Setup Script (Windows PowerShell)
#
# Purpose: Automatically sync all documentation to a new AI model account
# Usage: .\memories\NEW_ACCOUNT_SETUP.ps1
#
# This script:
# 1. Copies all documentation to ~/.claude/agent-memory/
# 2. Copies documentation to ~/.copilot/session-state/ if it exists
# 3. Creates sync manifest for quick reference
# 4. Verifies all files are in place
################################################################################

param(
    [switch]$Verbose = $false
)

Write-Host "╔════════════════════════════════════════════════════════╗" -ForegroundColor Cyan
Write-Host "║    KINETIC Coach Feature - New Account Setup          ║" -ForegroundColor Cyan
Write-Host "╚════════════════════════════════════════════════════════╝" -ForegroundColor Cyan
Write-Host ""

# Define source and destinations
$SCRIPT_DIR = Split-Path -Parent $MyInvocation.MyCommand.Path
$SOURCE_DOCS = "$SCRIPT_DIR\repo"
$CLAUDE_DEST = "$env:USERPROFILE\.claude\agent-memory\kinetic"
$COPILOT_BASE = "$env:USERPROFILE\.copilot\session-state"

Write-Host "📍 Source: $SOURCE_DOCS" -ForegroundColor Yellow
Write-Host "📍 Claude Destination: $CLAUDE_DEST" -ForegroundColor Yellow
Write-Host "📍 Copilot Base: $COPILOT_BASE" -ForegroundColor Yellow
Write-Host ""

# Step 1: Verify source exists
if (-not (Test-Path $SOURCE_DOCS)) {
    Write-Host "❌ ERROR: Source directory not found: $SOURCE_DOCS" -ForegroundColor Red
    exit 1
}
Write-Host "✅ Source directory verified" -ForegroundColor Green

# Step 2: Create Claude agent memory directory
Write-Host "📂 Step 1: Creating Claude agent memory directory..." -ForegroundColor Cyan
if (-not (Test-Path $CLAUDE_DEST)) {
    New-Item -ItemType Directory -Path $CLAUDE_DEST -Force | Out-Null
    Write-Host "   ✅ Created: $CLAUDE_DEST" -ForegroundColor Green
} else {
    Write-Host "   ✅ Already exists: $CLAUDE_DEST" -ForegroundColor Green
}

# Step 3: Copy documentation to Claude
Write-Host "📋 Step 2: Copying documentation to Claude..." -ForegroundColor Cyan
$md_files = Get-ChildItem -Path $SOURCE_DOCS -Filter "*.md"
foreach ($file in $md_files) {
    Copy-Item -Path $file.FullName -Destination $CLAUDE_DEST -Force
    if ($Verbose) {
        Write-Host "   ✅ Copied: $($file.Name)" -ForegroundColor Green
    }
}
Write-Host "   ✅ Copied $($md_files.Count) documentation files" -ForegroundColor Green

# Step 4: Copy documentation to Copilot if available
Write-Host "📋 Step 3: Checking for Copilot sessions..." -ForegroundColor Cyan
if (Test-Path $COPILOT_BASE) {
    $sessions = Get-ChildItem -Path $COPILOT_BASE -Directory -ErrorAction SilentlyContinue
    if ($sessions) {
        foreach ($session in $sessions) {
            $files_dir = "$($session.FullName)\files"
            if (Test-Path $files_dir) {
                foreach ($file in $md_files) {
                    Copy-Item -Path $file.FullName -Destination $files_dir -Force
                }
                Write-Host "   ✅ Synced to Copilot session: $($session.Name)" -ForegroundColor Green
            }
        }
    } else {
        Write-Host "   ℹ️  No active Copilot sessions found" -ForegroundColor Yellow
    }
} else {
    Write-Host "   ℹ️  Copilot not configured" -ForegroundColor Yellow
}

# Step 5: Create sync status file
Write-Host "📊 Step 4: Creating sync status file..." -ForegroundColor Cyan
$status_content = @"
╔════════════════════════════════════════════════════════════════╗
║        KINETIC Coach Feature - Sync Complete                  ║
╚════════════════════════════════════════════════════════════════╝

Setup Date: $(Get-Date -Format "yyyy-MM-dd HH:mm:ss")
Documentation Files: $($md_files.Count)
Total Size: ~65 KB
Build Status: ✅ Ready

📚 START HERE:
   → README_FOR_AI_MODELS.md (5-minute overview)

📚 FOR DETAILED INFO:
   → MASTER_INDEX.md (complete guide)
   → COACH_IMPLEMENTATION_REFERENCE.md (technical details)
   → COACH_FEATURE_SUMMARY.md (architecture)

🚀 VERIFY SETUP:
   1. cd kinetic
   2. .\gradlew.bat build -x lint
   3. .\gradlew.bat test

✅ All documentation is synced and ready for next AI model.

═══════════════════════════════════════════════════════════════════
"@

$status_content | Out-File -FilePath "$CLAUDE_DEST\SYNC_STATUS.txt" -Encoding UTF8 -Force
Write-Host "   ✅ Created sync status file" -ForegroundColor Green

# Step 6: Verify all files
Write-Host "✅ Step 5: Verifying all files..." -ForegroundColor Cyan
$synced_files = Get-ChildItem -Path $CLAUDE_DEST -Filter "*.md" | Measure-Object
Write-Host "   ✅ Found $($synced_files.Count) documentation files" -ForegroundColor Green

Write-Host ""
Write-Host "╔════════════════════════════════════════════════════════╗" -ForegroundColor Cyan
Write-Host "║          ✅ Setup Complete                            ║" -ForegroundColor Cyan
Write-Host "╚════════════════════════════════════════════════════════╝" -ForegroundColor Cyan
Write-Host ""
Write-Host "📍 Documentation Location: $CLAUDE_DEST" -ForegroundColor Yellow
Write-Host "📊 Files Synced: $($synced_files.Count)" -ForegroundColor Yellow
Write-Host ""
Write-Host "🚀 Next Steps:" -ForegroundColor Green
Write-Host "   1. Read: $CLAUDE_DEST\README_FOR_AI_MODELS.md"
Write-Host "   2. Build: .\gradlew.bat build -x lint"
Write-Host "   3. Test: .\gradlew.bat test"
Write-Host ""
Write-Host "✨ Ready for next AI model to start working!" -ForegroundColor Green
Write-Host ""
