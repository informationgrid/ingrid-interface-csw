#!/usr/bin/env bash
if [ -z $1 ]; then
    echo "No release version set";
    echo "bash release.sh <version> [--skipTests] [--skipBuild]"
    exit;
fi

echo "SNAPSHOT-versions removed and changes.xml updated?"
read

# make a copy of this script and execute everything below
# since during a switch branch it can happen that the script
# is not there or modified and cannot be executed
tail -n +17 ./release.sh > ./_release-copy.sh
sh ./_release-copy.sh $@ &
exit

# idiomatic parameter and option handling in sh
MAVEN_OPTIONS=
while test $# -gt 0
do
    case "$1" in
        --skipTests) echo "Skipping Tests"
            MAVEN_OPTIONS="$MAVEN_OPTIONS -DskipTests"
            ;;
        --skipBuild) echo "Skipping Build"
            MAVEN_OPTIONS="$MAVEN_OPTIONS -DnoReleaseBuild"
            ;;
        --*) echo "bad option $1"
            ;;
        *) echo "Release-Version: $1"
            RELEASE_VERSION=$1
            ;;
    esac
    shift
done

# exit when any command fails
set -e

featureVersion=${RELEASE_VERSION:2:1}
nextFeatureVersion=$((featureVersion+1))
newSnapshotVersion=${RELEASE_VERSION:0:2}${nextFeatureVersion}.0-SNAPSHOT
echo "Release Version is: $RELEASE_VERSION"
echo "Next Development Version is: $newSnapshotVersion"
echo "Maven Options: $MAVEN_OPTIONS"

# update license header and commit changes if any
mvn license:update-file-header
if ! git diff-index --quiet HEAD --; then
    git add -u
    git commit -m "Update license header"
fi

# make sure master is up to date
git checkout master
git pull
git checkout develop

# release start
mvn jgitflow:release-start -DallowUntracked -DreleaseVersion=$RELEASE_VERSION -DdevelopmentVersion=$newSnapshotVersion $MAVEN_OPTIONS

# finish release
mvn jgitflow:release-finish -DallowUntracked $MAVEN_OPTIONS

# remove copy of script file
rm -- "$0"
