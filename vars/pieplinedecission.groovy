#!groovy
def buildpipeline(Map configmap)
{
    application = configmap.get("application")
    switch(application){
    case 'nodejsvm':
       nodejs(configmap)
       break
    case'pythonvm':
      python(configmap);
      break
     default: 
        error "application not registered"
        break
}
}