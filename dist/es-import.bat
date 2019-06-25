@echo off
set LOCAL=%cd%
set CLASSPATH= %~dp0

java -cp %CLASSPATH%/ESMaintainTools-1.0.0-SNAPSHOT-jar-with-dependencies.jar lano.es.maintain.tools.ESAddImportDatabase %LOCAL% 10.100.23.107 lano_centro HSw3@jfUhG412 lano_centro lano_centro

@echo on