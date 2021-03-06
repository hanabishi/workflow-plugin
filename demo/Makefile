build: releases
	docker build -t jenkinsci/workflow-demo .

run: build
	docker run -p 8080:8080 -p 8081:8081 -p 8022:22 -ti jenkinsci/workflow-demo

PLUGINS=\
	org.jenkins-ci.plugins:durable-task \
	org.jenkins-ci.plugins:git \
	org.jenkins-ci.plugins:git-client \
	org.jenkins-ci.plugins:javadoc \
	org.jenkins-ci.plugins:junit \
	org.jenkins-ci.plugins:mailer \
	org.jenkins-ci.plugins:mapdb-api \
	org.jenkins-ci.plugins:mercurial \
	org.jenkins-ci.plugins:scm-api \
	org.jenkins-ci.plugins:script-security \
	org.jenkins-ci.plugins:subversion \
	org.jenkins-ci.plugins.workflow:workflow-aggregator \
	org.jenkins-ci.plugins.workflow:workflow-api \
	org.jenkins-ci.plugins.workflow:workflow-basic-steps \
	org.jenkins-ci.plugins.workflow:workflow-cps \
	org.jenkins-ci.plugins.workflow:workflow-durable-task-step \
	org.jenkins-ci.plugins.workflow:workflow-job \
	org.jenkins-ci.plugins.workflow:workflow-scm-step \
	org.jenkins-ci.plugins.workflow:workflow-step-api \
	org.jenkins-ci.plugins.workflow:workflow-stm \
	org.jenkins-ci.plugins.workflow:workflow-support

# TODO using ADD with a remote URL would be more convenient, but docker-build claims that URLs like http://jenkins-updates.cloudbees.com/download/plugins/mercurial/*latest*/mercurial.hpi give a 404 (in fact they are 302 → 200)
# anyway it seems to download the entire content disregarding Last-Modified, making it unusable
releases:
# TODO should we pass -U to mvn? Slower but more precise.
	[ -f jenkins-war.war ] || mvn org.apache.maven.plugins:maven-dependency-plugin:2.8:copy -Dartifact=org.jenkins-ci.main:jenkins-war:RELEASE:war -DoutputDirectory=. -Dmdep.stripVersion
	set -e; for ga in $(PLUGINS); do a=$$(echo $$ga | sed 's/.*\://'); [ -f JENKINS_HOME/plugins/$$a.jpi ] || (mvn org.apache.maven.plugins:maven-dependency-plugin:2.8:copy -Dartifact=$$ga:RELEASE:hpi -DoutputDirectory=JENKINS_HOME/plugins -Dmdep.stripVersion && mv JENKINS_HOME/plugins/$$a.hpi JENKINS_HOME/plugins/$$a.jpi); touch JENKINS_HOME/plugins/$$a.jpi.pinned; done

build-snapshot: build
	mvn -f .. -DskipTests clean install
	cp -v ../*/target/*.hpi snapshot/plugins
# TODO need to write snapshot/Dockerfile
	docker build -t jenkinsci/workflow-demo-snapshot snapshot

run-snapshot: build-snapshot
	docker run -p 8080:8080 -p 8081:8081 -p 8022:22 -ti jenkinsci/workflow-demo-snapshot

clean:
	rm -rf jenkins-war.war JENKINS_HOME/plugins snapshot/plugins

push:
	docker push jenkinsci/workflow-demo
