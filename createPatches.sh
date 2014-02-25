#!/bin/bash

# first time call must include parameter --username=<USER> --password=<PASSWD> 
svn list "https://213.144.28.209:444/svn/ingrid/ingrid-interface-csw/tags/"
read -p "Enter tag-version (e.g. 3.3.0):, followed by [ENTER]: : " tag
COUNT=0
svnCall="svn log --limit 1 https://213.144.28.209:444/svn/ingrid/ingrid-interface-csw/tags/ingrid-interface-csw-$tag"
revisions=`$svnCall`
for line in $revisions ; do
    COUNT=$(($COUNT + 1))
    if [ $COUNT -eq 2 ]
    then
        revision=$line 
    fi 
done

revision=`echo "$revision" | sed -e 's/^ *//' -e 's/ *$//'`

echo "Using revision: $revision"
echo "Create patch file for src/main/release/conf/config.properties"
svn diff -r $revision:HEAD src/main/release/conf/config.properties > config.properties.patch
