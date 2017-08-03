podTemplate(
        label: 'akka-seeds',
        containers: [
            containerTemplate(name: 'jnlp', image: env.JNLP_SLAVE_IMAGE, args: '${computer.jnlpmac} ${computer.name}', alwaysPullImage: true),
            containerTemplate(name: 'kube', image: "${env.PRIVATE_REGISTRY}/library/kubectl:v1.7.2", ttyEnabled: true, command: 'cat'),
            containerTemplate(name: 'sbt', image: "${env.PRIVATE_REGISTRY}/library/sbt:2.12.3-fabric8", ttyEnabled: true, command: 'cat'),
            containerTemplate(name: 'helm', image: 'henryrao/helm:2.3.1', ttyEnabled: true, command: 'cat'),
            containerTemplate(name: 'dind', image: 'docker:stable-dind', privileged: true, ttyEnabled: true, command: 'dockerd', args: '--host=unix:///var/run/docker.sock --host=tcp://0.0.0.0:2375 --storage-driver=vfs')
        ],
        volumes: [
            emptyDirVolume(mountPath: '/var/run', memory: false),
            hostPathVolume(mountPath: "/etc/docker/certs.d/${env.PRIVATE_REGISTRY}/ca.crt", hostPath: "/etc/docker/certs.d/${env.PRIVATE_REGISTRY}/ca.crt"),
            hostPathVolume(mountPath: '/home/jenkins/.kube/config', hostPath: '/etc/kubernetes/admin.conf'),
            persistentVolumeClaim(claimName: env.HELM_REPOSITORY, mountPath: '/var/helm/', readOnly: false),
            persistentVolumeClaim(claimName: env.JENKINS_IVY2, mountPath: '/home/jenkins/.ivy2', readOnly: false),
        ]) {

    node('akka-seeds') {
        ansiColor('xterm') {
            try {
                stage('prepare') {
                    checkout scm
                }

                def image
                def imgTag
                def commit_log = sh(script: 'git log --format=%B -n 1', returnStdout: true).trim()

                container('sbt') {
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
                        imgTag = sh(returnStdout: true, script: 'cat tag').trim()
                        def mainClass = sh(returnStdout: true, script: 'cat mainClass').trim()
                        image = docker.build("${env.PRIVATE_REGISTRY}/inu/akka-seeds:${imgTag}", "--pull --build-arg JAVA_MAIN_CLASS=${mainClass} .")
                    }

                }
                stage('push image') {
                    withDockerRegistry(url: env.PRIVATE_REGISTRY_URL, credentialsId: 'docker-login') {
                        image.push()
                        if( env.BRANCH_NAME == 'master' ){
                            image.push('latest')
                        }
                    }
                }

                stage('package') {
                    container('helm') {
                        sh 'helm init --client-only'
                        dir('akka-seeds') {
                            echo 'update image tag'
                            sh """
                            sed -i \'s/\${BUILD_TAG}/${imgTag}/\' ./templates/NOTES.txt ./values.yaml
                            """
                            sh 'helm lint .'
                            sh 'helm package --destination /var/helm/repo .'
                        }
                        dir('/var/helm/repo') {
                            def flags = "--url ${env.HELM_PUBLIC_REPO_URL}"
                            flags = fileExists('index.yaml') ? "${flags} --merge index.yaml" : flags
                            sh "helm repo index ${flags} ."
                        }
                    }
                    build job: 'helm-repository/master', parameters: [string(name: 'commiter', value: "${env.JOB_NAME}\ncommit: ${commit_log}")]
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