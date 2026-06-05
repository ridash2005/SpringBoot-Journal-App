#!/bin/bash
# Test script for Journal App - Ensure build success and application works

set -e

echo "================================"
echo "Journal App Build & Test Script"
echo "================================"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Step 1: Clean and compile
echo -e "\n${YELLOW}[1/6]${NC} Cleaning and compiling..."
mvn clean compile -DskipTests
if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ Compilation successful${NC}"
else
    echo -e "${RED}✗ Compilation failed${NC}"
    exit 1
fi

# Step 2: Run tests
echo -e "\n${YELLOW}[2/6]${NC} Running unit tests..."
mvn test
if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ Tests passed${NC}"
else
    echo -e "${RED}✗ Tests failed${NC}"
    exit 1
fi

# Step 3: Package application
echo -e "\n${YELLOW}[3/6]${NC} Packaging application..."
mvn package -DskipTests
if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ Package created${NC}"
else
    echo -e "${RED}✗ Packaging failed${NC}"
    exit 1
fi

# Step 4: Verify JAR exists
echo -e "\n${YELLOW}[4/6]${NC} Verifying JAR file..."
if [ -f target/journalApp-1.0.0.jar ]; then
    JAR_SIZE=$(ls -lh target/journalApp-1.0.0.jar | awk '{print $5}')
    echo -e "${GREEN}✓ JAR file exists (Size: $JAR_SIZE)${NC}"
else
    echo -e "${RED}✗ JAR file not found${NC}"
    exit 1
fi

# Step 5: Check dependencies
echo -e "\n${YELLOW}[5/6]${NC} Checking dependencies..."
mvn dependency:analyze
echo -e "${GREEN}✓ Dependency analysis complete${NC}"

# Step 6: Display build info
echo -e "\n${YELLOW}[6/6]${NC} Build Information..."
echo "Application: Journal App"
echo "Version: 1.0.0"
echo "Java: 17"
echo "Build Status: SUCCESS"
echo -e "${GREEN}✓ All checks passed!${NC}"

echo -e "\n${GREEN}Ready for deployment!${NC}"
echo "Next step: Push changes to GitHub"
echo "  git add ."
echo "  git commit -m 'Fix: GoogleAuthController import and add health checks'"
echo "  git push origin master"
