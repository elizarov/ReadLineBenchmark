@echo off

call gradlew --no-daemon clean jmhJar
if %ERRORLEVEL% neq 0 exit /b %ERRORLEVEL%

set ARGS=-jar benchmarks.jar -foe true -f 2

echo JDK_18 C2
%JDK_18%\bin\java %ARGS% -o result_jdk_18_C2.txt
if %ERRORLEVEL% neq 0 exit /b %ERRORLEVEL%

echo JDK_18 C1
%JDK_18%\bin\java -XX:TieredStopAtLevel=1 %ARGS% -o result_jdk_18_C1.txt
if %ERRORLEVEL% neq 0 exit /b %ERRORLEVEL%

echo JDK_11 C2
%JDK_11%\bin\java %ARGS% -o result_jdk_11_C2.txt
