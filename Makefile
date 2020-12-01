version:
	mvn versions:set -DnewVersion=$(version) -DprocessAllModules -DgenerateBackupPoms=false

release:
	mvn versions:set -DnewVersion=$(version) -DprocessAllModules -DgenerateBackupPoms=false
