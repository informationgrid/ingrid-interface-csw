maven clean
maven scm:update -o
maven scm:prepare-release -Dmaven.test.skip=true -o 
maven scm:perform-release -Dmaven.test.skip=true -o
