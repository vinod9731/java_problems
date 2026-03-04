Starburst (Trino) POC – Payment to Posting Linkage for AML Monitoring
  Objective: Validate federated querying across payment and ledger systems without ETL.
Goal: Identify unmatched transactions and detect AML compliance issues.
Enable analytics on distributed data sources using Starburst/Trino.
Technologies Used: Starburst (Trino), Hive Metastore, PostgreSQL, MinIO, Docker, Parquet.



  Core Technologies and Components
Starburst / Trino: Distributed SQL query engine enabling federated analytics.
Coordinator: Accepts queries and distributes tasks across workers.
Workers: Execute distributed query processing.
Hive Metastore: Stores metadata for data lake tables.
PostgreSQL: Simulated ledger database.
MinIO: Object storage containing payment datasets.
Parquet: Columnar storage optimized for analytics.


  System Architecture and Data Flow
Payment transaction files stored in MinIO as Parquet format.
Hive Metastore registers tables and metadata.
PostgreSQL stores ledger posting records.
Starburst queries both sources simultaneously.
Results used for AML monitoring and reconciliation.


  AML Monitoring Scenarios
Scenario 1: Identify payments initiated but not posted in ledger.
Scenario 2: Detect high-value transactions (> $10,000) without AML/KYC validation.
Queries executed using Starburst SQL across federated data sources.
Demonstrates reconciliation monitoring and AML compliance validation.
