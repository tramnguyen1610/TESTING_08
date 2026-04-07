@echo off
cd /d D:\Test\KTAuto_Sele_Java_Nhom08
set MAVEN_HOME=C:\apache-maven-3.9.13-bin\apache-maven-3.9.13
set PATH=%MAVEN_HOME%\bin;%PATH%
mvn clean test -Dtest=LoginTest#TC01
pause

