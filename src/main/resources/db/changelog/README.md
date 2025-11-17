# Liquibase — Team Rules & Conventions

This repo uses a **single folder of timestamped change files** + optional env-specific seeds.
The goal is zero coordination: everyone can add migrations safely without guessing “next release”.

Create a new file under db/changelog/changes:
YYYYMMDD-HHMM-<short-slug>.yaml

databaseChangeLog:
  - changeSet:
      id: YYYYMMDD-HHMM-<short-slug>
      author: <your-initials>
      changes:
        # your change here

# Run with Spring Boot:
Dev: SPRING_PROFILES_ACTIVE=dev (applies changes/ + seeds/dev)
Test: Testcontainers sets spring.liquibase.contexts=it (applies changes/ + seeds/it)
Prod: no contexts (applies only changes/)

# Required Practices
One logical change per file. Small, reversible steps.
Never rename/move a file that has been merged; it changes the logical file path and breaks checksums.

No destructive changes (drops/renames) without expand-and-contract (zero downtime):
Phase 1: add new column (nullable), backfill, dual-write in app.  
Phase 2 (next deploy): switch reads, make non-null.  
Phase 3 (later): drop old column.  

Money types: use NUMERIC(12,2); no DOUBLE PRECISION.  
Timestamps: prefer TIMESTAMP WITH TIME ZONE (TIMESTAMPTZ) + NOW().  
FKs & indexes: always create explicit indexes for FK columns and hot sort/filter columns.  
Rollback: provide rollback only if safe; otherwise document “requires forward fix”.

