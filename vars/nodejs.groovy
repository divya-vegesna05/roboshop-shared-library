def call(Map configmap) {
pipeline{
    agent{
         node {
        label 'agent-1'
    }
    }
    parameters {
         booleanParam(name: 'Deploy', defaultValue: false, description: 'Toggle this value')
    }
    options {
        timeout(time: 1, unit: 'HOURS') 
    }
    environment { 
        package_version = ''
       // nexus_url = '172.31.5.248:8081'
    }

    stages{
         stage("get version")
         {
        steps{
             
           script{
            def file = readJSON file: 'package.json'
            package_version = file.version
            echo "${package_version}"
           }
            }
         }
        stage("install dependencies")
         {
        steps{
         sh """    
            npm install  
         """   
        }
    }
    stage("Run unit test")
    {
        steps{
            sh """
            echo "Run unit test"
            """
        }
    }
    stage("sonar scanner")
    {
        steps{
            sh """
            echo "sonr test run"
            """
        }
    }
     stage("build")
         {
        steps{
         sh """    
          ls -la
          zip -q -r ${configmap.component}.zip ./* -x '.git' -x '*.zip'
          ls -ltr
         """   
        }
    }
    stage("deploy")
         {
                steps {
                          nexusArtifactUploader(
                            nexusVersion: 'nexus3',
                                protocol: 'http',
                           nexusUrl: "${pipelineglobals.nexus_url()}",
                            groupId: 'com.roboshop',
                            version: "${package_version}",
                            repository: '${configmap.component}',
                            credentialsId: 'nexus-id',
                             artifacts: [
                               [artifactId: "${configmap.component}",
                                 classifier: '',
                                    file: "${configmap.component}.zip",
                              type: 'zip']
                                ]
                            )
                         }        
         } 
    // stage("Deploytodev")
    // {
    //    when {
    //        $params.deploy
    //     }
    //     steps{
    //          script{
    //             def params = [
    //      string(name: 'version', value: "${package_version}"),
    //      string(name: 'environment', value: "dev")
    //             ]
    //     build job: "../catalogue-deploy", parameters: params
    //     }  
    //      }
    // }
    }
 post { 
        always { 
            echo 'I will always say Hello again!'
            deleteDir()
        }
        success { 
            echo 'I will always say success!'
        }
                failure { 
            echo 'I will always say success!'
        }
    }
}
}