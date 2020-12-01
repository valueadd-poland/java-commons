version:
	(cd jpa-lucene; mvn versions:set -DnewVersion=$(version) -DprocessAllModules -DgenerateBackupPoms=false)
	(cd graphql; mvn versions:set -DnewVersion=$(version) -DprocessAllModules -DgenerateBackupPoms=false)
	(cd permission; mvn versions:set -DnewVersion=$(version) -DprocessAllModules -DgenerateBackupPoms=false)
	(cd varmi; mvn versions:set -DnewVersion=$(version) -DprocessAllModules -DgenerateBackupPoms=false)
	(cd varmi-spring; mvn versions:set -DnewVersion=$(version) -DprocessAllModules -DgenerateBackupPoms=false)

release:
	(cd jpa-lucene; mvn -B deploy)
	(cd graphql; mvn -B deploy)
	(cd permission; mvn -B deploy)
	(cd varmi; mvn -B deploy)
	(cd varmi-spring; mvn -B deploy)
#mvn -B -pl permission,graphql,varmi,varmi-spring,jpa-lucene deploy
