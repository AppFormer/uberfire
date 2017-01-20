def updateVersions=
"""
sh /home/jenkins/workspace/DSL-UF-Release-0.9.x/scripts/release/UF_updateNextVersion.sh
"""

// ******************************************************

job("UF_updateToNextDevelopmentVersion") {

  description("This job: <br> updates the UF and UF-extensions repositories to a new developmenmt version <br> for 0.7.x, 0.8.x or 0.9.x branches <br> IMPORTANT: Created automatically by Jenkins job DSL plugin. Do not edit manually! The changes will get lost next time the job is generated.")
 
  parameters {
    stringParam("newVersion", "0.9.x", "Edit the new UF version")
    stringParam("oldVersion", "0.9.0-SNAPSHOT", "Edit the old version for Uberfire/Uberfire-extensions")
  }

  label("kie-releases")

  logRotator {
    numToKeep(10)
  }

  jdk("jdk1.8")

  wrappers {
    timeout {
      absolute(30)
    }
    timestamps()
    colorizeOutput()
    preBuildCleanup()
    toolenv("APACHE_MAVEN_3_2_3", "JDK1_8")
  }

  configure { project ->
    project / 'buildWrappers' << 'org.jenkinsci.plugins.proccleaner.PreBuildCleanup' {
      cleaner(class: 'org.jenkinsci.plugins.proccleaner.PsCleaner') {
        killerType 'org.jenkinsci.plugins.proccleaner.PsAllKiller'
        killer(class: 'org.jenkinsci.plugins.proccleaner.PsAllKiller')
        username 'jenkins'
      }
    }
  }

  publishers {
    mailer('mbiarnes@redhat.com', false, false)
  }

  steps {
    environmentVariables {
        envs(MAVEN_OPTS :"-Xms2g -Xmx3g -XX:MaxPermSize=512m", MAVEN_HOME: "\$APACHE_MAVEN_3_2_3_HOME", MAVEN_REPO_LOCAL: "/home/jenkins/.m2/repository", PATH :"\$MAVEN_HOME/bin:\$PATH")
    }
    shell(updateVersions)
  }
}
