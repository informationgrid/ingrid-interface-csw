@echo off
setlocal ENABLEDELAYEDEXPANSION

svn list https://213.144.28.209:444/svn/ingrid/ingrid-interface-csw/tags
set /p tag="Enter tag-version (e.g. 3.3.0): "

set COUNT=0
set svnCall=svn log --limit 1 https://213.144.28.209:444/svn/ingrid/ingrid-interface-csw/tags/ingrid-interface-csw-%tag%
for /f %%a in ('%svnCall%') do (
    set /A COUNT=!COUNT! + 1
    if !COUNT! == 2 ( set rev=%%a )
)

REM trim spaces to right (work around)
set rev=%rev%##
set rev=%rev: ##=##%
set rev=%rev:##=%
echo Using revision: %rev%
#svn diff -r %rev%:HEAD src\main\webapp\WEB-INF\spring.xml > spring.xml.patch

pause