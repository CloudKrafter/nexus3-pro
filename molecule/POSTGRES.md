# Postgres database

FOR PRO VERSION ONLY

please review these steps first: https://help.sonatype.com/repomanager3/installation-and-upgrades/configuring-nexus-repository-pro-for-h2-or-postgresql#ConfiguringNexusRepositoryProforH2orPostgreSQL-ConfiguringforExternalPostgreSQL(Preferred)

Then make sure your Postgres instance has the following requirements: https://help.sonatype.com/repomanager3/product-information/sonatype-nexus-repository-system-requirements#SonatypeNexusRepositorySystemRequirements-PostgreSQL(Recommended)BluePRO

NOTE: You have to import a Pro license. It may look like Nexus will work with postgres without importing a license, but it doesn't. Nexus will start but you won't be able to login.

When ready add the following variables to your playbook;
```yaml

nexus_setup_postgres_db: false # when true, the nexus.properties file will be prepared for postgres support but will NOT be active
nexus_enable_postgres_db: false # when true Nexus Repository will use Postgres, otherwise it will use the default OrientDB
nexus_postgres_db_host: localhost
nexus_postgres_db_name: nexus
nexus_postgres_db_port: 5432
nexus_postgres_db_username: nexus
nexus_postgres_db_password: nexus

```
