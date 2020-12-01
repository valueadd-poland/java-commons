version:
	mvn versions:set -DnewVersion=$(version) -DprocessAllModules -DgenerateBackupPoms=false

release:
	mvn -B -pl permission,graphql,varmi,varmi-spring,jpa-lucene deploy
