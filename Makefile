release:
	(cd varmi; mvn versions:set -DnewVersion=$(version) -DprocessAllModules -DgenerateBackupPoms=false)
	(cd date-utils; mvn versions:set -DnewVersion=$(version) -DprocessAllModules -DgenerateBackupPoms=false)
	(cd junit-utils; mvn versions:set -DnewVersion=$(version) -DprocessAllModules -DgenerateBackupPoms=false)
	(cd jpa-lucene; mvn versions:set -DnewVersion=$(version) -DprocessAllModules -DgenerateBackupPoms=false)
	(cd graphql; mvn versions:set -DnewVersion=$(version) -DprocessAllModules -DgenerateBackupPoms=false)
	(cd permission; mvn versions:set -DnewVersion=$(version) -DprocessAllModules -DgenerateBackupPoms=false)

	(cd varmi; mvn -B deploy) || true
	(cd date-utils; mvn -B deploy) || true
	(cd junit-utils; mvn -B deploy) || true
	(cd jpa-lucene; mvn -B deploy) || true
	(cd graphql; mvn -B deploy) || true
	(cd permission; mvn -B deploy) || true

	git add *
	git commit -a -m release($(version))
