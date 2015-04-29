

## How to start

Use JVM property before starting:

```
-DmigrationApp.settings.path=file:///PATH_TO/migration.properties
```

## Importing DB

Example:

```
rlwrap java -cp ~/.m2/repository/com/h2database/h2/1.4.183/h2-1.4.183.jar org.h2.tools.Shell -url jdbc:h2:file:/PATH_TO/old -user sa
```

Then:

```sql
RUNSCRIPT FROM '/PATH_TO/old-book-schema.sql';
RUNSCRIPT FROM '/PATH_TO/old-book-schema.sql';
```
