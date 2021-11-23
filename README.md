# service-generation-utility

This console application is used for generating of Services based on the predefined list of `Freemarker` templates placed in the `/src/main/resources/META-INF/templates` folder. The arguments for templates are taken from DB by `schemacrawler`.

The list of Services to be generated must be given as a program argument(s). Supports these service:
* model
* soap api
* rest api
* kafka api

### Related components:
* PostgreSQL database for data persistence

### Usage
###### mandatory arguments
* `settings` - VM option - relative path to yaml-file with registry settings, ex.: `-Dsettings=settings.yaml`

###### optional arguments
* `module` - program argument(s) - the module(s) to be generated, ex.: `[--module=model --module=kafka-api ...]`

### Local development:
###### Prerequisites:
* Postgres database is configured and running
* registry-regulation is successfully deployed into the DB
* `setting.yaml` file is placed in the root folder, ex.:
```
settings:
  general:
    package: my.fav.registry
    register: registry
    version: 1.0
  kafka:
    retention-policy-in-days:
      read: 123456789
      write: 123456789
```

###### Configuration:
1. Check `src/main/resources/application-local.yaml` and customize database settings if needed (properties spring.datasource.url, username, password)

###### Steps:
1. (Optional) Package application into jar file with `mvn clean package`
2. Add `--module=model --module=kafka-api --module=rest-api --module=soap-api` to program run arguments, depending on the list of Services to be generated
3. Run application with your favourite IDE or via `java -jar ...` with jar file, created above

### License
service-generation-utility is Open Source software released under the Apache 2.0 license.
