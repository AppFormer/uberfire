BASE_BRANCH=0.9.x
TARGET_USER=kiereleaseuser
TARGET_USER_REMOTE=kie
REMOTE_URL_1=git@github.com:kiereleaseuser/uberfire.git
REMOTE_URL_2=git@github.com:kiereleaseuser/uberfire-extensions.git
DATE=$(date "+%Y-%m-%d")

# clone the repository and branch for uberfire and uberfire-extensions
git clone git@github.com:uberfire/uberfire.git --branch $BASE_BRANCH
cd $WORKSPACE/uberfire
PR_BRANCH_1=uberfire-$DATE-$BASE_BRANCH
git checkout -b $PR_BRANCH_1 $BASE_BRANCH
git remote add $TARGET_USER_REMOTE $REMOTE_URL_1
cd $WORKSPACE
git clone git@github.com:uberfire/uberfire-extensions.git --branch $BASE_BRANCH
cd $WORKSPACE/uberfire-extensions
PR_BRANCH_2=uberire-extensions-$DATE-$BASE_BRANCH
git checkout -b $PR_BRANCH_2 $BASE_BRANCH
git remote add $TARGET_USER_REMOTE $REMOTE_URL_2

#UBERFIRE
# upgrades the version to next development version of Uberfire
cd $WORKSPACE/uberfire
mvn -Dfull versions:set -DoldVersion=$oldVersion -DnewVersion=$newVersion -DallowSnapshots=true -DgenerateBackupPoms=false

# git add and commit the version update changes 
git add .
commitMSG="update to next development version $newVersion"
git commit -m "$commitMSG"

# do a build of uberfire
mvn -B -e -U clean install -Dmaven.test.failure.ignore=true -Dgwt.memory.settings="-Xmx2g -Xms1g -XX:MaxPermSize=256m -XX:PermSize=128m -Xss1M"

# Raise a PR
SOURCE=uberfire
git push $TARGET_USER_REMOTE $PR_BRANCH_1
hub pull-request -m "$commitMSG" -b $SOURCE:$BASE_BRANCH -h $TARGET_USER:$PR_BRANCH_1

# UBERFIRE _EXTENSIONS
# upgrades the version to the release/tag version Uberfire-extensions
cd $WORKSPACE/uberfire-extensions 

# upgrades the version to the release/tag version
mvn -B -N versions:update-parent -Dfull -DparentVersion=[$newVersion] -DallowSnapshots=true -DgenerateBackupPoms=false
mvn -N -B versions:update-child-modules -Dfull -DallowSnapshots=true -DgenerateBackupPoms=false   

# git add and commit the version update changes 
git add .
git commit -m "$commitMSG"

# do a build of uberfire-extensions
mvn -B -e -U clean install -Dmaven.test.failure.ignore=true -Dgwt.memory.settings="-Xmx2g -Xms1g -XX:MaxPermSize=256m -XX:PermSize=128m -Xss1M"

# Raise a PR
SOURCE=uberfire
git push $TARGET_USER_REMOTE $PR_BRANCH_2
hub pull-request -m "$commitMSG" -b $SOURCE:$BASE_BRANCH -h $TARGET_USER:$PR_BRANCH_2
