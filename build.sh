#!/bin/bash

# Build script for Y Language project

set -e

echo "🚀 Building Y Language Project..."

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Check if required tools are installed
check_dependencies() {
    print_status "Checking dependencies..."
    
    if ! command -v java &> /dev/null; then
        print_error "Java is not installed. Please install Java 17 or higher."
        exit 1
    fi
    
    if ! command -v node &> /dev/null; then
        print_error "Node.js is not installed. Please install Node.js 18 or higher."
        exit 1
    fi
    
    if ! command -v mvn &> /dev/null; then
        print_error "Maven is not installed. Please install Maven 3.6 or higher."
        exit 1
    fi
    
    print_status "All dependencies are installed."
}

# Build backend
build_backend() {
    print_status "Building backend..."
    cd backend
    
    print_status "Cleaning and compiling..."
    mvn clean compile
    
    print_status "Running tests..."
    mvn test
    
    print_status "Packaging..."
    mvn package -DskipTests
    
    cd ..
    print_status "Backend build completed successfully."
}

# Build frontend
build_frontend() {
    print_status "Building frontend..."
    cd frontend
    
    print_status "Installing dependencies..."
    npm install
    
    print_status "Running tests..."
    npm test -- --passWithNoTests
    
    print_status "Building..."
    npm run build
    
    cd ..
    print_status "Frontend build completed successfully."
}

# Build Docker images
build_docker() {
    print_status "Building Docker images..."
    
    print_status "Building backend Docker image..."
    docker build -t ylang-backend:latest ./backend
    
    print_status "Building frontend Docker image..."
    docker build -t ylang-frontend:latest ./frontend
    
    print_status "Docker images built successfully."
}

# Run tests
run_tests() {
    print_status "Running all tests..."
    
    cd backend
    mvn test
    cd ..
    
    cd frontend
    npm test -- --passWithNoTests
    cd ..
    
    print_status "All tests passed."
}

# Start services
start_services() {
    print_status "Starting services..."
    
    # Start backend in background
    print_status "Starting backend..."
    cd backend
    nohup mvn spring-boot:run > backend.log 2>&1 &
    BACKEND_PID=$!
    cd ..
    
    # Wait for backend to start
    print_status "Waiting for backend to start..."
    sleep 30
    
    # Start frontend in background
    print_status "Starting frontend..."
    cd frontend
    nohup npm run dev > frontend.log 2>&1 &
    FRONTEND_PID=$!
    cd ..
    
    print_status "Services started successfully."
    print_status "Backend PID: $BACKEND_PID"
    print_status "Frontend PID: $FRONTEND_PID"
    print_status "Backend logs: backend.log"
    print_status "Frontend logs: frontend.log"
}

# Stop services
stop_services() {
    print_status "Stopping services..."
    
    # Stop backend
    if [ ! -z "$BACKEND_PID" ]; then
        kill $BACKEND_PID 2>/dev/null || true
    fi
    
    # Stop frontend
    if [ ! -z "$FRONTEND_PID" ]; then
        kill $FRONTEND_PID 2>/dev/null || true
    fi
    
    print_status "Services stopped."
}

# Clean build artifacts
clean() {
    print_status "Cleaning build artifacts..."
    
    cd backend
    mvn clean
    cd ..
    
    cd frontend
    rm -rf dist node_modules
    cd ..
    
    print_status "Build artifacts cleaned."
}

# Main function
main() {
    case "${1:-build}" in
        "build")
            check_dependencies
            build_backend
            build_frontend
            ;;
        "test")
            run_tests
            ;;
        "docker")
            build_docker
            ;;
        "start")
            start_services
            ;;
        "stop")
            stop_services
            ;;
        "clean")
            clean
            ;;
        "all")
            check_dependencies
            clean
            build_backend
            build_frontend
            run_tests
            build_docker
            ;;
        *)
            echo "Usage: $0 {build|test|docker|start|stop|clean|all}"
            echo ""
            echo "Commands:"
            echo "  build   - Build backend and frontend"
            echo "  test    - Run all tests"
            echo "  docker  - Build Docker images"
            echo "  start   - Start services in background"
            echo "  stop    - Stop services"
            echo "  clean   - Clean build artifacts"
            echo "  all     - Run complete build and test pipeline"
            exit 1
            ;;
    esac
}

# Handle script interruption
trap stop_services EXIT

# Run main function
main "$@"