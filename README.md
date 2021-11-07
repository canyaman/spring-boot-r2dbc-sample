# R2DBC Spring Boot Webflux Sample Project 
* generate schema (DDL script)
* h2-console (Web based database management console)

WebFlux and r2dbc is not support schema generation and h2-console which are built in features of the Spring Boot MVC.

JPA schema generation gradle plugin and h2-console autoconfiguration library solve the problem.

## Configuration

### Gradle Plugin
Add JPA to schema file generation plugin. The spring boot is not using JPA, only gradle plugin and h2-console using jdbc connections and drivers. 
Application runtime is only using r2dbc driver and connections.

This plugin is using hibernate generate schema tool. You can use JPA annotations (javax.persistence.) with Spring annotations (org.springframework.data.) annotations at the same time without any issue [HBM2DDL](https://docs.jboss.org/hibernate/orm/5.5/userguide/html_single/Hibernate_User_Guide.html#schema-generation)  

build.gradle
```groovy
plugins {
    // Other plugins...
    
    // JPA schema generation
    id "io.github.divinespear.jpa-schema-generate" version "0.4.0"
}
```

### Dependencies
build.gradle
```yaml
dependencies {
    implementation('org.springframework.boot:spring-boot-starter-data-r2dbc')
    implementation('org.springframework.boot:spring-boot-starter-webflux')
    implementation('javax.persistence:javax.persistence-api')

    // PostgreSQL Database
    runtimeOnly('io.r2dbc:r2dbc-postgresql')
    runtimeOnly('org.postgresql:postgresql')
    // H2 Database
    runtimeOnly('io.r2dbc:r2dbc-h2')
    runtimeOnly('com.h2database:h2')

    generateSchemaLibs('org.hibernate:hibernate-core:5.5.7.Final')

    implementation ('me.yaman.can:spring-boot-webflux-h2-console:0.0.1')
}
```

### Schema Generation
"outputDirectory" is depends on configuration. In this case postgres schema file is generated in the build directory.
If you are using flyway or liquibase (You can use even with r2dbc application.) to manage migrations, you don't want to use the schema file without any modification. In that case, you can modify the file from the build directory.


build.gradle
```groovy
generateSchema {
	vendor = 'hibernate'
	packageToScan = ["me.yaman.can.database.r2dbc.entity".toString()]
	scriptAction = 'create'
	format = true
	createOutputFileName = "V001__schema.sql"
	lineSeparator = 'CRLF'
	targets {
		postgres {
			outputDirectory = file("$buildDir/db/migration/postgres")
			databaseProductName = 'PostgreSQL Custom Dialect'
			properties = [
					'hibernate.hbm2ddl.delimiter': ';',
					'hibernate.dialect': 'org.hibernate.dialect.PostgreSQL10Dialect',
					'hibernate.physical_naming_strategy': 'org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy',
					'hibernate.implicit_naming_strategy': 'org.springframework.boot.orm.jpa.hibernate.SpringImplicitNamingStrategy'
			]
		}
		h2 {
			outputDirectory = file("src/main/resources/db/migration/h2")
			databaseProductName = 'H2 Custom Dialect'
			properties = [
					'hibernate.hbm2ddl.delimiter': ';',
					'hibernate.dialect': 'org.hibernate.dialect.H2Dialect',
					'hibernate.physical_naming_strategy': 'org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy',
					'hibernate.implicit_naming_strategy': 'org.springframework.boot.orm.jpa.hibernate.SpringImplicitNamingStrategy'
			]
		}
	}
}
```

### Runtime Configuration
Flyway and Liquibase are also depends on JDBC driver.
spring.sql.init.* properties can be used.
[Spring Boot R2dbc using basic sql scripts](https://docs.spring.io/spring-boot/docs/current/reference/html/howto.html#howto.data-initialization.using-basic-sql-scripts)

application.yml
```yaml
spring:
  sql:
    init:
      mode: always
      schemaLocations: classpath:/db/migration/h2/V001__schema.sql
      #dataLocations:
```

### H2 Console Configuration
Spring autoconfiguration has to be enabled before (default is enabled). spring-boot-webflux-h2-console is autoconfiguration library and run as a separate web server.  
spring.datasource part is optional for h2-console login. 
Even if it is optional, it is highly recommended. You don't have to sign in username, password and driver details; if it is available.

application.yml
```yaml
spring:
  h2.console:
    enabled: true
    path: /h2-console
  datasource:
    url: 'jdbc:h2:mem:citydb'
    driverClassName: org.h2.Driver
    username: 'sa'
    password: ''
```

## Demo
```console
.\gradlew :app:generateSchema
.\gradlew :app:bootRun
```
First command generate h2 schema sql file and spring boot application is configured basic sql scripts by the generated sql file.

After bootRun task,
```console
.s.d.r.c.RepositoryConfigurationDelegate : Bootstrapping Spring Data R2DBC repositories in DEFAULT mode.
.s.d.r.c.RepositoryConfigurationDelegate : Finished Spring Data repository scanning in 47 ms. Found 1 R2DBC repository interfaces.
m.y.c.w.h.H2ConsoleAutoConfiguration     : H2 Page
m.y.c.w.h.H2ConsoleAutoConfiguration     : Connect to database jdbc:h2:mem:citydb
m.y.c.w.h.H2ConsoleAutoConfiguration     : spring.datasource.url is jdbc:h2:mem:citydb
m.y.c.w.h.H2ConsoleAutoConfiguration     : H2 Console JDBC datasource is configured
m.y.c.w.h.H2ConsoleAutoConfiguration     : H2 Console page /h2-console is configured for datasource jdbc:h2:mem:citydb
o.s.b.web.embedded.netty.NettyWebServer  : Netty started on port 8080
m.y.c.w.h.H2ConsoleAutoConfiguration     : H2 Console started on port 57506
m.y.c.w.h.H2ConsoleAutoConfiguration     : H2 Web Console server running at http://192.168.1.106:57506 (others can connect)
m.y.can.database.r2dbc.ApplicationKt     : Started ApplicationKt in 3.048 seconds (JVM running for 3.49)
```
now you can manage database manually via h2-console http://localhost:8080/h2-console
