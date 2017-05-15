podTemplate(
        label: env.JOB_NAME,
        containers: [
                containerTemplate(name: 'jnlp', image: 'henryrao/jnlp-slave', args: '${computer.jnlpmac} ${computer.name}', alwaysPullImage: true)
        ],
        volumes: [
                hostPathVolume(mountPath: '/var/run/docker.sock', hostPath: '/var/run/docker.sock'),
                persistentVolumeClaim(claimName: 'jenkins-ivy2', mountPath: '/home/jenkins/.ivy2', readOnly: false),
                persistentVolumeClaim(claimName: 'helm-repository', mountPath: '/var/helm/repo', readOnly: false)
        ]) {

    node(env.JOB_NAME) {
        ansiColor('xterm') {
            try {
                def image
                stage('prepare') {
                    checkout scm
                }

                docker.image('henryrao/sbt:2.11.8').inside("") {
                    stage('build') {
                        sh 'du -sh ~/.ivy2'
                        sh 'sbt compile'
                        sh 'sbt cpJarsForDocker'
                    }
                    //stage('unit test') {
                    //    sh 'sbt test'
                    //}
                }

                stage('build image') {
                    dir('target/docker') {
                        def tag = sh(returnStdout: true, script: 'cat tag').trim()
                        def mainClass = sh(returnStdout: true, script: 'cat mainClass').trim()
                        image = docker.build("henryrao/akka-seeds:${tag}", "--pull --build-arg JAVA_MAIN_CLASS=${mainClass} .")
                    }

                }
                stage('push image') {
                    withDockerRegistry(url: 'https://index.docker.io/v1/', credentialsId: 'docker-login') {
                        image.push()
                        if( env.BRANCH_NAME == 'master' ){
                            image.push('latest')
                        }

                    }
                }

            } catch (e) {
                echo "${e}"
                currentBuild.result = FAILURE
            }
            finally {

            }
        }
    }
}