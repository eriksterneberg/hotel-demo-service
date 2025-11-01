.PHONY: help build test run clean check coverage format docker-build docker-run db-setup db-stop security-scan perf-test

# Default target
help: ## Show this help message
	@echo 'Usage: make [target]'
	@echo ''
	@echo 'Available targets:'
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | sort | awk 'BEGIN {FS = ":.*?## "}; {printf "  \033[36m%-20s\033[0m %s\n", $$1, $$2}'

build: ## Build the project
	./gradlew build -x test

test: ## Run all tests
	./gradlew test

run: ## Run the application
	./gradlew bootRun

clean: ## Clean build artifacts
	./gradlew clean
	rm -rf build/

check: ## Run all code quality checks
	./gradlew check

coverage: ## Generate test coverage report
	./gradlew jacocoTestReport
	@echo "Coverage report available at: build/reports/jacoco/test/html/index.html"

format: ## Format code using Google Java Style
	./gradlew googleJavaFormat

db-start: ## Start Cassandra database
	docker-compose up -d cassandra

db-setup: ## Start Cassandra and initialize schema
	docker-compose up -d cassandra
	@echo "Waiting for Cassandra to be ready..."
	@sleep 10
	docker exec -i hotel-demo-cassandra cqlsh < src/main/resources/cassandra/schema.cql
	docker exec -i hotel-demo-cassandra cqlsh < src/main/resources/cassandra/test-data.cql

db-stop: ## Stop Cassandra
	docker-compose down

security-scan: ## Run security vulnerability scan
	./gradlew dependencyCheckAnalyze

perf-test: ## Run performance tests
	./gradlew performanceTest

.DEFAULT_GOAL := help
