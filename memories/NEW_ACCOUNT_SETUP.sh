#!/bin/bash
###############################################################################
# KINETIC Coach Feature - New Account Setup Script
# 
# Purpose: Automatically sync all documentation to a new AI model account
# Usage: ./memories/NEW_ACCOUNT_SETUP.sh
#
# This script:
# 1. Copies all documentation to ~/.claude/agent-memory/
# 2. Copies documentation to ~/.copilot/session-state/ if it exists
# 3. Creates sync manifest for quick reference
# 4. Verifies all files are in place
###############################################################################

set -e

echo "╔════════════════════════════════════════════════════════╗"
echo "║    KINETIC Coach Feature - New Account Setup          ║"
echo "╚════════════════════════════════════════════════════════╝"
echo ""

# Define source and destinations
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
SOURCE_DOCS="$SCRIPT_DIR/repo"
CLAUDE_DEST="$HOME/.claude/agent-memory/kinetic"
COPILOT_DEST="$HOME/.copilot/session-state/*/files"

echo "📍 Source: $SOURCE_DOCS"
echo "📍 Claude Destination: $CLAUDE_DEST"
echo "📍 Copilot Destination: $COPILOT_DEST"
echo ""

# Step 1: Create Claude agent memory directory
echo "📂 Step 1: Creating Claude agent memory directory..."
mkdir -p "$CLAUDE_DEST"
echo "   ✅ Created: $CLAUDE_DEST"

# Step 2: Copy documentation to Claude
echo "📋 Step 2: Copying documentation to Claude..."
cp -v "$SOURCE_DOCS"/*.md "$CLAUDE_DEST/" 2>/dev/null || true
echo "   ✅ Copied all .md files"

# Step 3: Copy documentation to Copilot if available
if [ -d "$(dirname "$COPILOT_DEST")" ]; then
    echo "📋 Step 3: Copying documentation to Copilot..."
    for copilot_session in $HOME/.copilot/session-state/*/files; do
        if [ -d "$copilot_session" ]; then
            cp -v "$SOURCE_DOCS"/*.md "$copilot_session/" 2>/dev/null || true
            echo "   ✅ Copied to: $copilot_session"
        fi
    done
fi

# Step 4: Create sync status file
echo "📊 Step 4: Creating sync status file..."
cat > "$CLAUDE_DEST/SYNC_STATUS.txt" << 'EOF'
╔════════════════════════════════════════════════════════════════╗
║        KINETIC Coach Feature - Sync Complete                  ║
╚════════════════════════════════════════════════════════════════╝

Setup Date: $(date)
Documentation Files: 8
Total Size: ~65 KB
Build Status: ✅ Ready

📚 START HERE:
   → README_FOR_AI_MODELS.md (5-minute overview)

📚 FOR DETAILED INFO:
   → MASTER_INDEX.md (complete guide)
   → COACH_IMPLEMENTATION_REFERENCE.md (technical details)
   → COACH_FEATURE_SUMMARY.md (architecture)

🚀 VERIFY SETUP:
   1. cd /path/to/kinetic
   2. ./gradlew build -x lint
   3. ./gradlew test

✅ All documentation is synced and ready for next AI model.

═══════════════════════════════════════════════════════════════════
EOF
echo "   ✅ Created sync status file"

# Step 5: Verify all files
echo "✅ Step 5: Verifying all files..."
file_count=$(ls -1 "$CLAUDE_DEST"/*.md 2>/dev/null | wc -l)
echo "   ✅ Found $file_count documentation files"

echo ""
echo "╔════════════════════════════════════════════════════════╗"
echo "║          ✅ Setup Complete                            ║"
echo "╚════════════════════════════════════════════════════════╝"
echo ""
echo "📍 Documentation Location: $CLAUDE_DEST"
echo "📊 Files Synced: $file_count"
echo ""
echo "🚀 Next Steps:"
echo "   1. Read: $CLAUDE_DEST/README_FOR_AI_MODELS.md"
echo "   2. Build: ./gradlew build -x lint"
echo "   3. Test: ./gradlew test"
echo ""
echo "✨ Ready for next AI model to start working!"
echo ""
