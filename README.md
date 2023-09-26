# CICD Assignment
## Aim of the project
- Mock används för att
    - att skapa svar från färdmedelsleverantörer
    - att ge respons på betalningsförsök
- Unit testning används för att testa de kritiska klasserna i applikationen.
- Smoke testning används för att avgöra att kommunikationen mellan klasserna fungerar.
- Integration testing används för att se så att information om autentiserade användare kan sparas till databasen.
- End-to-end testing används för att testa så att varje endpoint ger det svar som förväntas
- Jenkins hämtar samtlig kod från ett github repo
- Jenkins används för att skapa två pipelines:
    - Första pipeline:n kör testerna mot utvecklingsmiljön
    - Andra pipeline:n kör testerna i produktionsmiljö (ubuntu 22 eller alpine) samt kör artefakten i java-runtime.
- För ett högre betyg så måste gränsfall till större delar hanteras i programmet och i testerna.

## The contructure of project

![img1](https://github.com/niuniu268/CICDAssignment/blob/master/img/dbRelation.png?raw=true)

The Account database is association with the Route database. The booking column contains route.id.

Based on the relational database, the project can fulfill CRUD function. When users buy a ticket, the booking adds Route.id and history adds the route.price. In the end, the payment information would be sent as a result. When the ticket is finished, the booking becomes zero and the payment information would be sent as a result. In addition, if the users cancel the ordered ticket, route.id is removed from booking and the value of history is rolled back. The example is listed below. Please check Row 3 in the route database.

![img2](https://github.com/niuniu268/CICDAssignment/blob/master/img/dbBeforePayment.png?raw=true)

![img3](https://github.com/niuniu268/CICDAssignment/blob/master/img/dbAfterPayment.png?raw=true)

![img3](https://github.com/niuniu268/CICDAssignment/blob/master/img/dbCancelPayment.png?raw=true)


## Test

- Mock test
    1. Applied mockito and mockMVC to fulfill the CRUD by delivery
        - used RouteControllerTest and RouteServiceImplTest
    2. Checked the mechanism of payment
        - used AccountControllerTest and RouteServiceImplTest

- Unit test
    1. Tested a couple of important methods by means of Junit
        - used CicdAssignmentApplicationTests
        - checked the coverage of unit test
    ![img4](https://github.com/niuniu268/CICDAssignment/blob/master/img/Coverage.png?raw=true)    

- Integration test
    1. Applied failsafe

    ![img5](https://github.com/niuniu268/CICDAssignment/blob/master/img/IntegrationTest.png?raw=true)

- End-to-End test
    1. Applied http client
    ![img6](https://github.com/niuniu268/CICDAssignment/blob/master/img/EndToEnd.png?raw=true)

Jenkins

- Built up a Jenkins server at 192.168.1.72:8080
- Built up a vagrant as a test server
- Built up a java container based on OpenJDK11 image

- built up a couple of plugins: 
    1. Publish Over SSH Version1.25
    2. Blue Ocean Version1.27.7 

- finished Jenkinsfile:
    1. the Jenkinsfile includes four stages:
        - pull the code from Github.com
        - compile the code by means of Maven
        - remove the old code in the remote server
        - build up a java image and share the java package into container
    
```
pipeline {
    agent any
    tools {
        maven "maven3"

    }

    stages {


        stage('pull code') {
            steps {
                git credentialsId: 'niuniu', url: 'https://github.com/niuniu268/CICDAssignment.git'
                echo "finish pull code from github"
            }
        }
        stage('build') {
            steps {
                sh "mvn clean package"

            }

        }
        stage('remove old docker container') {
            steps {
                sshPublisher(publishers: [sshPublisherDesc(configName: 'testServer', transfers: [sshTransfer(cleanRemote: false, excludes: '', execCommand: '''sudo docker rm -f java-con
sudo docker rmi java''', execTimeout: 120000, flatten: false, makeEmptyDirs: false, noDefaultExcludes: false, patternSeparator: '[, ]+', remoteDirectory: '', remoteDirectorySDF: false, removePrefix: '', sourceFiles: '')], usePromotionTimestamp: false, useWorkspaceInPromotion: false, verbose: false)])

            }

        }
        stage('SSH send '){
            steps {

                sshPublisher(publishers: [sshPublisherDesc(configName: 'testServer', transfers: [sshTransfer(cleanRemote: false, excludes: '', execCommand: '', execTimeout: 120000, flatten: false, makeEmptyDirs: false, noDefaultExcludes: false, patternSeparator: '[, ]+', remoteDirectory: '', remoteDirectorySDF: false, removePrefix: '', sourceFiles: '**/CICD*.jar'), sshTransfer(cleanRemote: false, excludes: '', execCommand: '', execTimeout: 120000, flatten: false, makeEmptyDirs: false, noDefaultExcludes: false, patternSeparator: '[, ]+', remoteDirectory: '/target', remoteDirectorySDF: false, removePrefix: '', sourceFiles: 'Dockerfile')], usePromotionTimestamp: false, useWorkspaceInPromotion: false, verbose: false)])

            }
        }
        stage('run docker '){
            steps {

                sshPublisher(publishers: [sshPublisherDesc(configName: 'testServer', transfers: [sshTransfer(cleanRemote: false, excludes: '', execCommand: '''sudo docker build -t java .
sudo docker run -i -d -p 8082:8082 --name java-con java bash
''', execTimeout: 120000, flatten: false, makeEmptyDirs: false, noDefaultExcludes: false, patternSeparator: '[, ]+', remoteDirectory: '', remoteDirectorySDF: false, removePrefix: '', sourceFiles: '')], usePromotionTimestamp: false, useWorkspaceInPromotion: false, verbose: false)])


            }
        }
    }
}

```

2. Finish the Jenkinsfile 
![img7](https://github.com/niuniu268/CICDAssignment/blob/master/img/jenkinsMulti1.png?raw=true)
![img8](https://github.com/niuniu268/CICDAssignment/blob/master/img/jenkinsMulti2.png?raw=true)
![img9](https://github.com/niuniu268/CICDAssignment/blob/master/img/jenkinsMulti3.png?raw=true)

3. The Jenkins Log

```

Branch indexing
Connecting to https://api.github.com using louis.niuniu@gmail.com/******
Obtained jenkinsfile from c008a260960f48fa8c05140e47d074ddcac70e05
[Pipeline] Start of Pipeline
[Pipeline] node
Running on Jenkins in /var/lib/jenkins/workspace/multipleScan_master
[Pipeline] {
[Pipeline] stage
[Pipeline] { (Declarative: Checkout SCM)
[Pipeline] checkout
Selected Git installation does not exist. Using Default
The recommended git tool is: NONE
using credential niuniu
 > git rev-parse --resolve-git-dir /var/lib/jenkins/workspace/multipleScan_master/.git # timeout=10
Fetching changes from the remote Git repository
 > git config remote.origin.url https://github.com/niuniu268/CICDAssignment.git # timeout=10
Fetching without tags
Fetching upstream changes from https://github.com/niuniu268/CICDAssignment.git
 > git --version # timeout=10
 > git --version # 'git version 2.34.1'
using GIT_ASKPASS to set credentials 
 > git fetch --no-tags --force --progress -- https://github.com/niuniu268/CICDAssignment.git +refs/heads/master:refs/remotes/origin/master # timeout=10
Checking out Revision c008a260960f48fa8c05140e47d074ddcac70e05 (master)
 > git config core.sparsecheckout # timeout=10
 > git checkout -f c008a260960f48fa8c05140e47d074ddcac70e05 # timeout=10
Commit message: "add db relationship"
 > git rev-list --no-walk 8c864e93ad459c3beb0e7035ae44ff455f52a9e1 # timeout=10
[Pipeline] }
[Pipeline] // stage
[Pipeline] withEnv
[Pipeline] {
[Pipeline] stage
[Pipeline] { (Declarative: Tool Install)
[Pipeline] tool
[Pipeline] envVarsForTool
[Pipeline] }
[Pipeline] // stage
[Pipeline] withEnv
[Pipeline] {
[Pipeline] stage
[Pipeline] { (pull code)
[Pipeline] tool
[Pipeline] envVarsForTool
[Pipeline] withEnv
[Pipeline] {
[Pipeline] git
Selected Git installation does not exist. Using Default
The recommended git tool is: NONE
using credential niuniu
 > git rev-parse --resolve-git-dir /var/lib/jenkins/workspace/multipleScan_master/.git # timeout=10
Fetching changes from the remote Git repository
 > git config remote.origin.url https://github.com/niuniu268/CICDAssignment.git # timeout=10
Fetching upstream changes from https://github.com/niuniu268/CICDAssignment.git
 > git --version # timeout=10
 > git --version # 'git version 2.34.1'
using GIT_ASKPASS to set credentials 
 > git fetch --tags --force --progress -- https://github.com/niuniu268/CICDAssignment.git +refs/heads/*:refs/remotes/origin/* # timeout=10
 > git rev-parse refs/remotes/origin/master^{commit} # timeout=10
Checking out Revision c008a260960f48fa8c05140e47d074ddcac70e05 (refs/remotes/origin/master)
 > git config core.sparsecheckout # timeout=10
 > git checkout -f c008a260960f48fa8c05140e47d074ddcac70e05 # timeout=10
 > git branch -a -v --no-abbrev # timeout=10
 > git branch -D master # timeout=10
 > git checkout -b master c008a260960f48fa8c05140e47d074ddcac70e05 # timeout=10
Commit message: "add db relationship"
[Pipeline] echo
finish pull code from github
[Pipeline] }
[Pipeline] // withEnv
[Pipeline] }
[Pipeline] // stage
[Pipeline] stage
[Pipeline] { (build)
[Pipeline] tool
[Pipeline] envVarsForTool
[Pipeline] withEnv
[Pipeline] {
[Pipeline] sh
+ mvn clean package
[[1;34mINFO[m] Scanning for projects...
[[1;34mINFO[m] 
[[1;34mINFO[m] [1m---------------------< [0;36mcom.example:CICDAssignment[0;1m >---------------------[m
[[1;34mINFO[m] [1mBuilding CICDAssignment 0.0.1-SNAPSHOT[m
[[1;34mINFO[m] [1m--------------------------------[ jar ]---------------------------------[m
[[1;34mINFO[m] 
[[1;34mINFO[m] [1m--- [0;32mmaven-clean-plugin:3.2.0:clean[m [1m(default-clean)[m @ [36mCICDAssignment[0;1m ---[m
[[1;34mINFO[m] Deleting /var/lib/jenkins/workspace/multipleScan_master/target
[[1;34mINFO[m] 
[[1;34mINFO[m] [1m--- [0;32mmaven-resources-plugin:3.2.0:resources[m [1m(default-resources)[m @ [36mCICDAssignment[0;1m ---[m
[[1;34mINFO[m] Using 'UTF-8' encoding to copy filtered resources.
[[1;34mINFO[m] Using 'UTF-8' encoding to copy filtered properties files.
[[1;34mINFO[m] Copying 1 resource
[[1;34mINFO[m] Copying 1 resource
[[1;34mINFO[m] 
[[1;34mINFO[m] [1m--- [0;32mmaven-compiler-plugin:3.10.1:compile[m [1m(default-compile)[m @ [36mCICDAssignment[0;1m ---[m
[[1;34mINFO[m] Changes detected - recompiling the module!
[[1;34mINFO[m] Compiling 14 source files to /var/lib/jenkins/workspace/multipleScan_master/target/classes
[[1;34mINFO[m] 
[[1;34mINFO[m] [1m--- [0;32mmaven-resources-plugin:3.2.0:testResources[m [1m(default-testResources)[m @ [36mCICDAssignment[0;1m ---[m
[[1;34mINFO[m] Using 'UTF-8' encoding to copy filtered resources.
[[1;34mINFO[m] Using 'UTF-8' encoding to copy filtered properties files.
[[1;34mINFO[m] skip non existing resourceDirectory /var/lib/jenkins/workspace/multipleScan_master/src/test/resources
[[1;34mINFO[m] 
[[1;34mINFO[m] [1m--- [0;32mmaven-compiler-plugin:3.10.1:testCompile[m [1m(default-testCompile)[m @ [36mCICDAssignment[0;1m ---[m
[[1;34mINFO[m] Changes detected - recompiling the module!
[[1;34mINFO[m] Compiling 5 source files to /var/lib/jenkins/workspace/multipleScan_master/target/test-classes
[[1;34mINFO[m] /var/lib/jenkins/workspace/multipleScan_master/src/test/java/com/example/cicdassignment/service/RouteServiceImplTest.java: Some input files use or override a deprecated API.
[[1;34mINFO[m] /var/lib/jenkins/workspace/multipleScan_master/src/test/java/com/example/cicdassignment/service/RouteServiceImplTest.java: Recompile with -Xlint:deprecation for details.
[[1;34mINFO[m] 
[[1;34mINFO[m] [1m--- [0;32mmaven-surefire-plugin:2.22.2:test[m [1m(default-test)[m @ [36mCICDAssignment[0;1m ---[m
[[1;34mINFO[m] 
[[1;34mINFO[m] -------------------------------------------------------
[[1;34mINFO[m]  T E S T S
[[1;34mINFO[m] -------------------------------------------------------
[[1;34mINFO[m] Running com.example.cicdassignment.controller.[1mAccountControllerTest[m
13:14:50.018 [main] DEBUG org.springframework.validation.beanvalidation.OptionalValidatorFactoryBean - Failed to set up a Bean Validation provider
javax.validation.NoProviderFoundException: Unable to create a Configuration, because no Jakarta Bean Validation provider could be found. Add a provider like Hibernate Validator (RI) to your classpath.
	at javax.validation.Validation$GenericBootstrapImpl.configure(Validation.java:291)
	at org.springframework.validation.beanvalidation.LocalValidatorFactoryBean.afterPropertiesSet(LocalValidatorFactoryBean.java:275)
	at org.springframework.validation.beanvalidation.OptionalValidatorFactoryBean.afterPropertiesSet(OptionalValidatorFactoryBean.java:40)
	at org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder$StandaloneConfiguration.mvcValidator(StandaloneMockMvcBuilder.java:533)
	at org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder.registerMvcSingletons(StandaloneMockMvcBuilder.java:404)
	at org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder.initWebAppContext(StandaloneMockMvcBuilder.java:385)
	at org.springframework.test.web.servlet.setup.AbstractMockMvcBuilder.build(AbstractMockMvcBuilder.java:157)
	at com.example.cicdassignment.controller.AccountControllerTest.setUp(AccountControllerTest.java:69)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.base/java.lang.reflect.Method.invoke(Method.java:566)
	at org.junit.platform.commons.util.ReflectionUtils.invokeMethod(ReflectionUtils.java:725)
	at org.junit.jupiter.engine.execution.MethodInvocation.proceed(MethodInvocation.java:60)
	at org.junit.jupiter.engine.execution.InvocationInterceptorChain$ValidatingInvocation.proceed(InvocationInterceptorChain.java:131)
	at org.junit.jupiter.engine.extension.TimeoutExtension.intercept(TimeoutExtension.java:149)
	at org.junit.jupiter.engine.extension.TimeoutExtension.interceptLifecycleMethod(TimeoutExtension.java:126)
	at org.junit.jupiter.engine.extension.TimeoutExtension.interceptBeforeEachMethod(TimeoutExtension.java:76)
	at org.junit.jupiter.engine.execution.ExecutableInvoker$ReflectiveInterceptorCall.lambda$ofVoidMethod$0(ExecutableInvoker.java:115)
	at org.junit.jupiter.engine.execution.ExecutableInvoker.lambda$invoke$0(ExecutableInvoker.java:105)
	at org.junit.jupiter.engine.execution.InvocationInterceptorChain$InterceptedInvocation.proceed(InvocationInterceptorChain.java:106)
	at org.junit.jupiter.engine.execution.InvocationInterceptorChain.proceed(InvocationInterceptorChain.java:64)
	at org.junit.jupiter.engine.execution.InvocationInterceptorChain.chainAndInvoke(InvocationInterceptorChain.java:45)
	at org.junit.jupiter.engine.execution.InvocationInterceptorChain.invoke(InvocationInterceptorChain.java:37)
	at org.junit.jupiter.engine.execution.ExecutableInvoker.invoke(ExecutableInvoker.java:104)
	at org.junit.jupiter.engine.execution.ExecutableInvoker.invoke(ExecutableInvoker.java:98)
	at org.junit.jupiter.engine.descriptor.ClassBasedTestDescriptor.invokeMethodInExtensionContext(ClassBasedTestDescriptor.java:506)
	at org.junit.jupiter.engine.descriptor.ClassBasedTestDescriptor.lambda$synthesizeBeforeEachMethodAdapter$21(ClassBasedTestDescriptor.java:491)
	at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.lambda$invokeBeforeEachMethods$3(TestMethodTestDescriptor.java:171)
	at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.lambda$invokeBeforeMethodsOrCallbacksUntilExceptionOccurs$6(TestMethodTestDescriptor.java:199)
	at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)
	at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.invokeBeforeMethodsOrCallbacksUntilExceptionOccurs(TestMethodTestDescriptor.java:199)
	at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.invokeBeforeEachMethods(TestMethodTestDescriptor.java:168)
	at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.execute(TestMethodTestDescriptor.java:131)
	at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.execute(TestMethodTestDescriptor.java:66)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$6(NodeTestTask.java:151)
	at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$8(NodeTestTask.java:141)
	at org.junit.platform.engine.support.hierarchical.Node.around(Node.java:137)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$9(NodeTestTask.java:139)
	at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.executeRecursively(NodeTestTask.java:138)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.execute(NodeTestTask.java:95)
	at java.base/java.util.ArrayList.forEach(ArrayList.java:1541)
	at org.junit.platform.engine.support.hierarchical.SameThreadHierarchicalTestExecutorService.invokeAll(SameThreadHierarchicalTestExecutorService.java:41)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$6(NodeTestTask.java:155)
	at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$8(NodeTestTask.java:141)
	at org.junit.platform.engine.support.hierarchical.Node.around(Node.java:137)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$9(NodeTestTask.java:139)
	at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.executeRecursively(NodeTestTask.java:138)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.execute(NodeTestTask.java:95)
	at java.base/java.util.ArrayList.forEach(ArrayList.java:1541)
	at org.junit.platform.engine.support.hierarchical.SameThreadHierarchicalTestExecutorService.invokeAll(SameThreadHierarchicalTestExecutorService.java:41)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$6(NodeTestTask.java:155)
	at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$8(NodeTestTask.java:141)
	at org.junit.platform.engine.support.hierarchical.Node.around(Node.java:137)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$9(NodeTestTask.java:139)
	at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.executeRecursively(NodeTestTask.java:138)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.execute(NodeTestTask.java:95)
	at org.junit.platform.engine.support.hierarchical.SameThreadHierarchicalTestExecutorService.submit(SameThreadHierarchicalTestExecutorService.java:35)
	at org.junit.platform.engine.support.hierarchical.HierarchicalTestExecutor.execute(HierarchicalTestExecutor.java:57)
	at org.junit.platform.engine.support.hierarchical.HierarchicalTestEngine.execute(HierarchicalTestEngine.java:54)
	at org.junit.platform.launcher.core.DefaultLauncher.execute(DefaultLauncher.java:220)
	at org.junit.platform.launcher.core.DefaultLauncher.lambda$execute$6(DefaultLauncher.java:188)
	at org.junit.platform.launcher.core.DefaultLauncher.withInterceptedStreams(DefaultLauncher.java:202)
	at org.junit.platform.launcher.core.DefaultLauncher.execute(DefaultLauncher.java:181)
	at org.junit.platform.launcher.core.DefaultLauncher.execute(DefaultLauncher.java:128)
	at org.apache.maven.surefire.junitplatform.JUnitPlatformProvider.invokeAllTests(JUnitPlatformProvider.java:150)
	at org.apache.maven.surefire.junitplatform.JUnitPlatformProvider.invoke(JUnitPlatformProvider.java:124)
	at org.apache.maven.surefire.booter.ForkedBooter.invokeProviderInSameClassLoader(ForkedBooter.java:384)
	at org.apache.maven.surefire.booter.ForkedBooter.runSuitesInProcess(ForkedBooter.java:345)
	at org.apache.maven.surefire.booter.ForkedBooter.execute(ForkedBooter.java:126)
	at org.apache.maven.surefire.booter.ForkedBooter.main(ForkedBooter.java:418)
13:14:50.180 [main] DEBUG _org.springframework.web.servlet.HandlerMapping.Mappings - 
	c.e.c.c.AccountController:
	{GET [/account]}: selectAll()
	{GET [/account/join]}: selectJoinAll()
	{PUT [/account]}: addAccount(Account)
	{POST [/account/update]}: updateAccount(Account)
	{DELETE [/account/{id}]}: delAccount(Integer)
	{POST [/account/add]}: addBooking(Payment)
	{POST [/account/finish]}: completeBooking(Payment)
	{POST [/account/cancel]}: cancelBooking(Payment)
13:14:50.192 [main] DEBUG org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping - 8 mappings in org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping
13:14:50.478 [main] DEBUG org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter - ControllerAdvice beans: 0 @ModelAttribute, 0 @InitBinder, 1 RequestBodyAdvice, 1 ResponseBodyAdvice
13:14:50.538 [main] DEBUG org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver - ControllerAdvice beans: 0 @ExceptionHandler, 1 ResponseBodyAdvice
13:14:50.568 [main] INFO org.springframework.mock.web.MockServletContext - Initializing Spring TestDispatcherServlet ''
13:14:50.568 [main] INFO org.springframework.test.web.servlet.TestDispatcherServlet - Initializing Servlet ''
13:14:50.572 [main] DEBUG org.springframework.test.web.servlet.TestDispatcherServlet - Detected AcceptHeaderLocaleResolver
13:14:50.572 [main] DEBUG org.springframework.test.web.servlet.TestDispatcherServlet - Detected FixedThemeResolver
13:14:50.573 [main] DEBUG org.springframework.test.web.servlet.TestDispatcherServlet - Detected org.springframework.web.servlet.view.DefaultRequestToViewNameTranslator@11ebb1b6
13:14:50.573 [main] DEBUG org.springframework.test.web.servlet.TestDispatcherServlet - Detected org.springframework.web.servlet.support.SessionFlashMapManager@aaee2a2
13:14:50.573 [main] DEBUG org.springframework.test.web.servlet.TestDispatcherServlet - enableLoggingRequestDetails='false': request parameters and headers will be masked to prevent unsafe logging of potentially sensitive data
13:14:50.573 [main] INFO org.springframework.test.web.servlet.TestDispatcherServlet - Completed initialization in 5 ms
13:14:50.640 [main] DEBUG org.springframework.test.web.servlet.TestDispatcherServlet - GET "/account", parameters={}
13:14:50.645 [main] DEBUG org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping - Mapped to com.example.cicdassignment.controller.AccountController#selectAll()
13:14:50.682 [main] DEBUG org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor - Using 'application/json', given [*/*] and supported [application/json, application/*+json]
13:14:50.743 [main] DEBUG org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor - Writing [[Account(id=15, name=mockito, contact=6789, payment=swish, history=500, booking=0, type=user)]]
13:14:50.821 [main] DEBUG org.springframework.test.web.servlet.TestDispatcherServlet - Completed 200 OK
13:14:50.865 [main] DEBUG org.springframework.validation.beanvalidation.OptionalValidatorFactoryBean - Failed to set up a Bean Validation provider
javax.validation.NoProviderFoundException: Unable to create a Configuration, because no Jakarta Bean Validation provider could be found. Add a provider like Hibernate Validator (RI) to your classpath.
	at javax.validation.Validation$GenericBootstrapImpl.configure(Validation.java:291)
	at org.springframework.validation.beanvalidation.LocalValidatorFactoryBean.afterPropertiesSet(LocalValidatorFactoryBean.java:275)
	at org.springframework.validation.beanvalidation.OptionalValidatorFactoryBean.afterPropertiesSet(OptionalValidatorFactoryBean.java:40)
	at org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder$StandaloneConfiguration.mvcValidator(StandaloneMockMvcBuilder.java:533)
	at org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder.registerMvcSingletons(StandaloneMockMvcBuilder.java:404)
	at org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder.initWebAppContext(StandaloneMockMvcBuilder.java:385)
	at org.springframework.test.web.servlet.setup.AbstractMockMvcBuilder.build(AbstractMockMvcBuilder.java:157)
	at com.example.cicdassignment.controller.AccountControllerTest.setUp(AccountControllerTest.java:69)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.base/java.lang.reflect.Method.invoke(Method.java:566)
	at org.junit.platform.commons.util.ReflectionUtils.invokeMethod(ReflectionUtils.java:725)
	at org.junit.jupiter.engine.execution.MethodInvocation.proceed(MethodInvocation.java:60)
	at org.junit.jupiter.engine.execution.InvocationInterceptorChain$ValidatingInvocation.proceed(InvocationInterceptorChain.java:131)
	at org.junit.jupiter.engine.extension.TimeoutExtension.intercept(TimeoutExtension.java:149)
	at org.junit.jupiter.engine.extension.TimeoutExtension.interceptLifecycleMethod(TimeoutExtension.java:126)
	at org.junit.jupiter.engine.extension.TimeoutExtension.interceptBeforeEachMethod(TimeoutExtension.java:76)
	at org.junit.jupiter.engine.execution.ExecutableInvoker$ReflectiveInterceptorCall.lambda$ofVoidMethod$0(ExecutableInvoker.java:115)
	at org.junit.jupiter.engine.execution.ExecutableInvoker.lambda$invoke$0(ExecutableInvoker.java:105)
	at org.junit.jupiter.engine.execution.InvocationInterceptorChain$InterceptedInvocation.proceed(InvocationInterceptorChain.java:106)
	at org.junit.jupiter.engine.execution.InvocationInterceptorChain.proceed(InvocationInterceptorChain.java:64)
	at org.junit.jupiter.engine.execution.InvocationInterceptorChain.chainAndInvoke(InvocationInterceptorChain.java:45)
	at org.junit.jupiter.engine.execution.InvocationInterceptorChain.invoke(InvocationInterceptorChain.java:37)
	at org.junit.jupiter.engine.execution.ExecutableInvoker.invoke(ExecutableInvoker.java:104)
	at org.junit.jupiter.engine.execution.ExecutableInvoker.invoke(ExecutableInvoker.java:98)
	at org.junit.jupiter.engine.descriptor.ClassBasedTestDescriptor.invokeMethodInExtensionContext(ClassBasedTestDescriptor.java:506)
	at org.junit.jupiter.engine.descriptor.ClassBasedTestDescriptor.lambda$synthesizeBeforeEachMethodAdapter$21(ClassBasedTestDescriptor.java:491)
	at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.lambda$invokeBeforeEachMethods$3(TestMethodTestDescriptor.java:171)
	at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.lambda$invokeBeforeMethodsOrCallbacksUntilExceptionOccurs$6(TestMethodTestDescriptor.java:199)
	at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)
	at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.invokeBeforeMethodsOrCallbacksUntilExceptionOccurs(TestMethodTestDescriptor.java:199)
	at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.invokeBeforeEachMethods(TestMethodTestDescriptor.java:168)
	at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.execute(TestMethodTestDescriptor.java:131)
	at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.execute(TestMethodTestDescriptor.java:66)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$6(NodeTestTask.java:151)
	at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$8(NodeTestTask.java:141)
	at org.junit.platform.engine.support.hierarchical.Node.around(Node.java:137)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$9(NodeTestTask.java:139)
	at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.executeRecursively(NodeTestTask.java:138)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.execute(NodeTestTask.java:95)
	at java.base/java.util.ArrayList.forEach(ArrayList.java:1541)
	at org.junit.platform.engine.support.hierarchical.SameThreadHierarchicalTestExecutorService.invokeAll(SameThreadHierarchicalTestExecutorService.java:41)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$6(NodeTestTask.java:155)
	at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$8(NodeTestTask.java:141)
	at org.junit.platform.engine.support.hierarchical.Node.around(Node.java:137)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$9(NodeTestTask.java:139)
	at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.executeRecursively(NodeTestTask.java:138)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.execute(NodeTestTask.java:95)
	at java.base/java.util.ArrayList.forEach(ArrayList.java:1541)
	at org.junit.platform.engine.support.hierarchical.SameThreadHierarchicalTestExecutorService.invokeAll(SameThreadHierarchicalTestExecutorService.java:41)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$6(NodeTestTask.java:155)
	at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$8(NodeTestTask.java:141)
	at org.junit.platform.engine.support.hierarchical.Node.around(Node.java:137)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$9(NodeTestTask.java:139)
	at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.executeRecursively(NodeTestTask.java:138)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.execute(NodeTestTask.java:95)
	at org.junit.platform.engine.support.hierarchical.SameThreadHierarchicalTestExecutorService.submit(SameThreadHierarchicalTestExecutorService.java:35)
	at org.junit.platform.engine.support.hierarchical.HierarchicalTestExecutor.execute(HierarchicalTestExecutor.java:57)
	at org.junit.platform.engine.support.hierarchical.HierarchicalTestEngine.execute(HierarchicalTestEngine.java:54)
	at org.junit.platform.launcher.core.DefaultLauncher.execute(DefaultLauncher.java:220)
	at org.junit.platform.launcher.core.DefaultLauncher.lambda$execute$6(DefaultLauncher.java:188)
	at org.junit.platform.launcher.core.DefaultLauncher.withInterceptedStreams(DefaultLauncher.java:202)
	at org.junit.platform.launcher.core.DefaultLauncher.execute(DefaultLauncher.java:181)
	at org.junit.platform.launcher.core.DefaultLauncher.execute(DefaultLauncher.java:128)
	at org.apache.maven.surefire.junitplatform.JUnitPlatformProvider.invokeAllTests(JUnitPlatformProvider.java:150)
	at org.apache.maven.surefire.junitplatform.JUnitPlatformProvider.invoke(JUnitPlatformProvider.java:124)
	at org.apache.maven.surefire.booter.ForkedBooter.invokeProviderInSameClassLoader(ForkedBooter.java:384)
	at org.apache.maven.surefire.booter.ForkedBooter.runSuitesInProcess(ForkedBooter.java:345)
	at org.apache.maven.surefire.booter.ForkedBooter.execute(ForkedBooter.java:126)
	at org.apache.maven.surefire.booter.ForkedBooter.main(ForkedBooter.java:418)
13:14:50.875 [main] DEBUG _org.springframework.web.servlet.HandlerMapping.Mappings - 
	c.e.c.c.AccountController:
	{GET [/account]}: selectAll()
	{GET [/account/join]}: selectJoinAll()
	{PUT [/account]}: addAccount(Account)
	{POST [/account/update]}: updateAccount(Account)
	{DELETE [/account/{id}]}: delAccount(Integer)
	{POST [/account/add]}: addBooking(Payment)
	{POST [/account/finish]}: completeBooking(Payment)
	{POST [/account/cancel]}: cancelBooking(Payment)
13:14:50.878 [main] DEBUG org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping - 8 mappings in org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping
13:14:50.886 [main] DEBUG org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter - ControllerAdvice beans: 0 @ModelAttribute, 0 @InitBinder, 1 RequestBodyAdvice, 1 ResponseBodyAdvice
13:14:50.892 [main] DEBUG org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver - ControllerAdvice beans: 0 @ExceptionHandler, 1 ResponseBodyAdvice
13:14:50.893 [main] INFO org.springframework.mock.web.MockServletContext - Initializing Spring TestDispatcherServlet ''
13:14:50.893 [main] INFO org.springframework.test.web.servlet.TestDispatcherServlet - Initializing Servlet ''
13:14:50.893 [main] DEBUG org.springframework.test.web.servlet.TestDispatcherServlet - Detected AcceptHeaderLocaleResolver
13:14:50.893 [main] DEBUG org.springframework.test.web.servlet.TestDispatcherServlet - Detected FixedThemeResolver
13:14:50.893 [main] DEBUG org.springframework.test.web.servlet.TestDispatcherServlet - Detected org.springframework.web.servlet.view.DefaultRequestToViewNameTranslator@68880c21
13:14:50.893 [main] DEBUG org.springframework.test.web.servlet.TestDispatcherServlet - Detected org.springframework.web.servlet.support.SessionFlashMapManager@2dd2e270
13:14:50.894 [main] DEBUG org.springframework.test.web.servlet.TestDispatcherServlet - enableLoggingRequestDetails='false': request parameters and headers will be masked to prevent unsafe logging of potentially sensitive data
13:14:50.894 [main] INFO org.springframework.test.web.servlet.TestDispatcherServlet - Completed initialization in 1 ms
13:14:50.896 [main] DEBUG org.springframework.test.web.servlet.TestDispatcherServlet - GET "/account/join", parameters={}
13:14:50.899 [main] DEBUG org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping - Mapped to com.example.cicdassignment.controller.AccountController#selectJoinAll()
13:14:50.903 [main] DEBUG org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor - Using 'application/json', given [*/*] and supported [application/json, application/*+json]
13:14:50.903 [main] DEBUG org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor - Writing [[]]
13:14:50.904 [main] DEBUG org.springframework.test.web.servlet.TestDispatcherServlet - Completed 200 OK
13:14:50.925 [main] DEBUG org.springframework.validation.beanvalidation.OptionalValidatorFactoryBean - Failed to set up a Bean Validation provider
javax.validation.NoProviderFoundException: Unable to create a Configuration, because no Jakarta Bean Validation provider could be found. Add a provider like Hibernate Validator (RI) to your classpath.
	at javax.validation.Validation$GenericBootstrapImpl.configure(Validation.java:291)
	at org.springframework.validation.beanvalidation.LocalValidatorFactoryBean.afterPropertiesSet(LocalValidatorFactoryBean.java:275)
	at org.springframework.validation.beanvalidation.OptionalValidatorFactoryBean.afterPropertiesSet(OptionalValidatorFactoryBean.java:40)
	at org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder$StandaloneConfiguration.mvcValidator(StandaloneMockMvcBuilder.java:533)
	at org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder.registerMvcSingletons(StandaloneMockMvcBuilder.java:404)
	at org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder.initWebAppContext(StandaloneMockMvcBuilder.java:385)
	at org.springframework.test.web.servlet.setup.AbstractMockMvcBuilder.build(AbstractMockMvcBuilder.java:157)
	at com.example.cicdassignment.controller.AccountControllerTest.setUp(AccountControllerTest.java:69)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.base/java.lang.reflect.Method.invoke(Method.java:566)
	at org.junit.platform.commons.util.ReflectionUtils.invokeMethod(ReflectionUtils.java:725)
	at org.junit.jupiter.engine.execution.MethodInvocation.proceed(MethodInvocation.java:60)
	at org.junit.jupiter.engine.execution.InvocationInterceptorChain$ValidatingInvocation.proceed(InvocationInterceptorChain.java:131)
	at org.junit.jupiter.engine.extension.TimeoutExtension.intercept(TimeoutExtension.java:149)
	at org.junit.jupiter.engine.extension.TimeoutExtension.interceptLifecycleMethod(TimeoutExtension.java:126)
	at org.junit.jupiter.engine.extension.TimeoutExtension.interceptBeforeEachMethod(TimeoutExtension.java:76)
	at org.junit.jupiter.engine.execution.ExecutableInvoker$ReflectiveInterceptorCall.lambda$ofVoidMethod$0(ExecutableInvoker.java:115)
	at org.junit.jupiter.engine.execution.ExecutableInvoker.lambda$invoke$0(ExecutableInvoker.java:105)
	at org.junit.jupiter.engine.execution.InvocationInterceptorChain$InterceptedInvocation.proceed(InvocationInterceptorChain.java:106)
	at org.junit.jupiter.engine.execution.InvocationInterceptorChain.proceed(InvocationInterceptorChain.java:64)
	at org.junit.jupiter.engine.execution.InvocationInterceptorChain.chainAndInvoke(InvocationInterceptorChain.java:45)
	at org.junit.jupiter.engine.execution.InvocationInterceptorChain.invoke(InvocationInterceptorChain.java:37)
	at org.junit.jupiter.engine.execution.ExecutableInvoker.invoke(ExecutableInvoker.java:104)
	at org.junit.jupiter.engine.execution.ExecutableInvoker.invoke(ExecutableInvoker.java:98)
	at org.junit.jupiter.engine.descriptor.ClassBasedTestDescriptor.invokeMethodInExtensionContext(ClassBasedTestDescriptor.java:506)
	at org.junit.jupiter.engine.descriptor.ClassBasedTestDescriptor.lambda$synthesizeBeforeEachMethodAdapter$21(ClassBasedTestDescriptor.java:491)
	at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.lambda$invokeBeforeEachMethods$3(TestMethodTestDescriptor.java:171)
	at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.lambda$invokeBeforeMethodsOrCallbacksUntilExceptionOccurs$6(TestMethodTestDescriptor.java:199)
	at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)
	at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.invokeBeforeMethodsOrCallbacksUntilExceptionOccurs(TestMethodTestDescriptor.java:199)
	at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.invokeBeforeEachMethods(TestMethodTestDescriptor.java:168)
	at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.execute(TestMethodTestDescriptor.java:131)
	at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.execute(TestMethodTestDescriptor.java:66)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$6(NodeTestTask.java:151)
	at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$8(NodeTestTask.java:141)
	at org.junit.platform.engine.support.hierarchical.Node.around(Node.java:137)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$9(NodeTestTask.java:139)
	at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.executeRecursively(NodeTestTask.java:138)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.execute(NodeTestTask.java:95)
	at java.base/java.util.ArrayList.forEach(ArrayList.java:1541)
	at org.junit.platform.engine.support.hierarchical.SameThreadHierarchicalTestExecutorService.invokeAll(SameThreadHierarchicalTestExecutorService.java:41)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$6(NodeTestTask.java:155)
	at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$8(NodeTestTask.java:141)
	at org.junit.platform.engine.support.hierarchical.Node.around(Node.java:137)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$9(NodeTestTask.java:139)
	at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.executeRecursively(NodeTestTask.java:138)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.execute(NodeTestTask.java:95)
	at java.base/java.util.ArrayList.forEach(ArrayList.java:1541)
	at org.junit.platform.engine.support.hierarchical.SameThreadHierarchicalTestExecutorService.invokeAll(SameThreadHierarchicalTestExecutorService.java:41)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$6(NodeTestTask.java:155)
	at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$8(NodeTestTask.java:141)
	at org.junit.platform.engine.support.hierarchical.Node.around(Node.java:137)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$9(NodeTestTask.java:139)
	at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.executeRecursively(NodeTestTask.java:138)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.execute(NodeTestTask.java:95)
	at org.junit.platform.engine.support.hierarchical.SameThreadHierarchicalTestExecutorService.submit(SameThreadHierarchicalTestExecutorService.java:35)
	at org.junit.platform.engine.support.hierarchical.HierarchicalTestExecutor.execute(HierarchicalTestExecutor.java:57)
	at org.junit.platform.engine.support.hierarchical.HierarchicalTestEngine.execute(HierarchicalTestEngine.java:54)
	at org.junit.platform.launcher.core.DefaultLauncher.execute(DefaultLauncher.java:220)
	at org.junit.platform.launcher.core.DefaultLauncher.lambda$execute$6(DefaultLauncher.java:188)
	at org.junit.platform.launcher.core.DefaultLauncher.withInterceptedStreams(DefaultLauncher.java:202)
	at org.junit.platform.launcher.core.DefaultLauncher.execute(DefaultLauncher.java:181)
	at org.junit.platform.launcher.core.DefaultLauncher.execute(DefaultLauncher.java:128)
	at org.apache.maven.surefire.junitplatform.JUnitPlatformProvider.invokeAllTests(JUnitPlatformProvider.java:150)
	at org.apache.maven.surefire.junitplatform.JUnitPlatformProvider.invoke(JUnitPlatformProvider.java:124)
	at org.apache.maven.surefire.booter.ForkedBooter.invokeProviderInSameClassLoader(ForkedBooter.java:384)
	at org.apache.maven.surefire.booter.ForkedBooter.runSuitesInProcess(ForkedBooter.java:345)
	at org.apache.maven.surefire.booter.ForkedBooter.execute(ForkedBooter.java:126)
	at org.apache.maven.surefire.booter.ForkedBooter.main(ForkedBooter.java:418)
13:14:50.947 [main] DEBUG _org.springframework.web.servlet.HandlerMapping.Mappings - 
	c.e.c.c.AccountController:
	{GET [/account]}: selectAll()
	{GET [/account/join]}: selectJoinAll()
	{PUT [/account]}: addAccount(Account)
	{POST [/account/update]}: updateAccount(Account)
	{DELETE [/account/{id}]}: delAccount(Integer)
	{POST [/account/add]}: addBooking(Payment)
	{POST [/account/finish]}: completeBooking(Payment)
	{POST [/account/cancel]}: cancelBooking(Payment)
13:14:50.964 [main] DEBUG org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping - 8 mappings in org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping
13:14:50.973 [main] DEBUG org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter - ControllerAdvice beans: 0 @ModelAttribute, 0 @InitBinder, 1 RequestBodyAdvice, 1 ResponseBodyAdvice
13:14:50.979 [main] DEBUG org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver - ControllerAdvice beans: 0 @ExceptionHandler, 1 ResponseBodyAdvice
13:14:50.980 [main] INFO org.springframework.mock.web.MockServletContext - Initializing Spring TestDispatcherServlet ''
13:14:50.980 [main] INFO org.springframework.test.web.servlet.TestDispatcherServlet - Initializing Servlet ''
13:14:50.980 [main] DEBUG org.springframework.test.web.servlet.TestDispatcherServlet - Detected AcceptHeaderLocaleResolver
13:14:50.980 [main] DEBUG org.springframework.test.web.servlet.TestDispatcherServlet - Detected FixedThemeResolver
13:14:50.981 [main] DEBUG org.springframework.test.web.servlet.TestDispatcherServlet - Detected org.springframework.web.servlet.view.DefaultRequestToViewNameTranslator@2c7a8af2
13:14:50.981 [main] DEBUG org.springframework.test.web.servlet.TestDispatcherServlet - Detected org.springframework.web.servlet.support.SessionFlashMapManager@10c07b8d
13:14:50.981 [main] DEBUG org.springframework.test.web.servlet.TestDispatcherServlet - enableLoggingRequestDetails='false': request parameters and headers will be masked to prevent unsafe logging of potentially sensitive data
13:14:50.981 [main] INFO org.springframework.test.web.servlet.TestDispatcherServlet - Completed initialization in 1 ms
13:14:50.999 [main] DEBUG org.springframework.test.web.servlet.TestDispatcherServlet - DELETE "/account/15", parameters={}
13:14:51.000 [main] DEBUG org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping - Mapped to com.example.cicdassignment.controller.AccountController#delAccount(Integer)
13:14:51.031 [main] DEBUG org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor - Using 'application/json', given [*/*] and supported [application/json, application/*+json]
13:14:51.033 [main] DEBUG org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor - Writing [true]
13:14:51.033 [main] DEBUG org.springframework.test.web.servlet.TestDispatcherServlet - Completed 200 OK
13:14:51.140 [main] DEBUG org.springframework.validation.beanvalidation.OptionalValidatorFactoryBean - Failed to set up a Bean Validation provider
javax.validation.NoProviderFoundException: Unable to create a Configuration, because no Jakarta Bean Validation provider could be found. Add a provider like Hibernate Validator (RI) to your classpath.
	at javax.validation.Validation$GenericBootstrapImpl.configure(Validation.java:291)
	at org.springframework.validation.beanvalidation.LocalValidatorFactoryBean.afterPropertiesSet(LocalValidatorFactoryBean.java:275)
	at org.springframework.validation.beanvalidation.OptionalValidatorFactoryBean.afterPropertiesSet(OptionalValidatorFactoryBean.java:40)
	at org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder$StandaloneConfiguration.mvcValidator(StandaloneMockMvcBuilder.java:533)
	at org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder.registerMvcSingletons(StandaloneMockMvcBuilder.java:404)
	at org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder.initWebAppContext(StandaloneMockMvcBuilder.java:385)
	at org.springframework.test.web.servlet.setup.AbstractMockMvcBuilder.build(AbstractMockMvcBuilder.java:157)
	at com.example.cicdassignment.controller.AccountControllerTest.setUp(AccountControllerTest.java:69)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.base/java.lang.reflect.Method.invoke(Method.java:566)
	at org.junit.platform.commons.util.ReflectionUtils.invokeMethod(ReflectionUtils.java:725)
	at org.junit.jupiter.engine.execution.MethodInvocation.proceed(MethodInvocation.java:60)
	at org.junit.jupiter.engine.execution.InvocationInterceptorChain$ValidatingInvocation.proceed(InvocationInterceptorChain.java:131)
	at org.junit.jupiter.engine.extension.TimeoutExtension.intercept(TimeoutExtension.java:149)
	at org.junit.jupiter.engine.extension.TimeoutExtension.interceptLifecycleMethod(TimeoutExtension.java:126)
	at org.junit.jupiter.engine.extension.TimeoutExtension.interceptBeforeEachMethod(TimeoutExtension.java:76)
	at org.junit.jupiter.engine.execution.ExecutableInvoker$ReflectiveInterceptorCall.lambda$ofVoidMethod$0(ExecutableInvoker.java:115)
	at org.junit.jupiter.engine.execution.ExecutableInvoker.lambda$invoke$0(ExecutableInvoker.java:105)
	at org.junit.jupiter.engine.execution.InvocationInterceptorChain$InterceptedInvocation.proceed(InvocationInterceptorChain.java:106)
	at org.junit.jupiter.engine.execution.InvocationInterceptorChain.proceed(InvocationInterceptorChain.java:64)
	at org.junit.jupiter.engine.execution.InvocationInterceptorChain.chainAndInvoke(InvocationInterceptorChain.java:45)
	at org.junit.jupiter.engine.execution.InvocationInterceptorChain.invoke(InvocationInterceptorChain.java:37)
	at org.junit.jupiter.engine.execution.ExecutableInvoker.invoke(ExecutableInvoker.java:104)
	at org.junit.jupiter.engine.execution.ExecutableInvoker.invoke(ExecutableInvoker.java:98)
	at org.junit.jupiter.engine.descriptor.ClassBasedTestDescriptor.invokeMethodInExtensionContext(ClassBasedTestDescriptor.java:506)
	at org.junit.jupiter.engine.descriptor.ClassBasedTestDescriptor.lambda$synthesizeBeforeEachMethodAdapter$21(ClassBasedTestDescriptor.java:491)
	at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.lambda$invokeBeforeEachMethods$3(TestMethodTestDescriptor.java:171)
	at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.lambda$invokeBeforeMethodsOrCallbacksUntilExceptionOccurs$6(TestMethodTestDescriptor.java:199)
	at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)
	at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.invokeBeforeMethodsOrCallbacksUntilExceptionOccurs(TestMethodTestDescriptor.java:199)
	at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.invokeBeforeEachMethods(TestMethodTestDescriptor.java:168)
	at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.execute(TestMethodTestDescriptor.java:131)
	at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.execute(TestMethodTestDescriptor.java:66)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$6(NodeTestTask.java:151)
	at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$8(NodeTestTask.java:141)
	at org.junit.platform.engine.support.hierarchical.Node.around(Node.java:137)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$9(NodeTestTask.java:139)
	at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.executeRecursively(NodeTestTask.java:138)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.execute(NodeTestTask.java:95)
	at java.base/java.util.ArrayList.forEach(ArrayList.java:1541)
	at org.junit.platform.engine.support.hierarchical.SameThreadHierarchicalTestExecutorService.invokeAll(SameThreadHierarchicalTestExecutorService.java:41)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$6(NodeTestTask.java:155)
	at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$8(NodeTestTask.java:141)
	at org.junit.platform.engine.support.hierarchical.Node.around(Node.java:137)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$9(NodeTestTask.java:139)
	at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.executeRecursively(NodeTestTask.java:138)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.execute(NodeTestTask.java:95)
	at java.base/java.util.ArrayList.forEach(ArrayList.java:1541)
	at org.junit.platform.engine.support.hierarchical.SameThreadHierarchicalTestExecutorService.invokeAll(SameThreadHierarchicalTestExecutorService.java:41)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$6(NodeTestTask.java:155)
	at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$8(NodeTestTask.java:141)
	at org.junit.platform.engine.support.hierarchical.Node.around(Node.java:137)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$9(NodeTestTask.java:139)
	at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.executeRecursively(NodeTestTask.java:138)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.execute(NodeTestTask.java:95)
	at org.junit.platform.engine.support.hierarchical.SameThreadHierarchicalTestExecutorService.submit(SameThreadHierarchicalTestExecutorService.java:35)
	at org.junit.platform.engine.support.hierarchical.HierarchicalTestExecutor.execute(HierarchicalTestExecutor.java:57)
	at org.junit.platform.engine.support.hierarchical.HierarchicalTestEngine.execute(HierarchicalTestEngine.java:54)
	at org.junit.platform.launcher.core.DefaultLauncher.execute(DefaultLauncher.java:220)
	at org.junit.platform.launcher.core.DefaultLauncher.lambda$execute$6(DefaultLauncher.java:188)
	at org.junit.platform.launcher.core.DefaultLauncher.withInterceptedStreams(DefaultLauncher.java:202)
	at org.junit.platform.launcher.core.DefaultLauncher.execute(DefaultLauncher.java:181)
	at org.junit.platform.launcher.core.DefaultLauncher.execute(DefaultLauncher.java:128)
	at org.apache.maven.surefire.junitplatform.JUnitPlatformProvider.invokeAllTests(JUnitPlatformProvider.java:150)
	at org.apache.maven.surefire.junitplatform.JUnitPlatformProvider.invoke(JUnitPlatformProvider.java:124)
	at org.apache.maven.surefire.booter.ForkedBooter.invokeProviderInSameClassLoader(ForkedBooter.java:384)
	at org.apache.maven.surefire.booter.ForkedBooter.runSuitesInProcess(ForkedBooter.java:345)
	at org.apache.maven.surefire.booter.ForkedBooter.execute(ForkedBooter.java:126)
	at org.apache.maven.surefire.booter.ForkedBooter.main(ForkedBooter.java:418)
13:14:51.148 [main] DEBUG _org.springframework.web.servlet.HandlerMapping.Mappings - 
	c.e.c.c.AccountController:
	{GET [/account]}: selectAll()
	{GET [/account/join]}: selectJoinAll()
	{PUT [/account]}: addAccount(Account)
	{POST [/account/update]}: updateAccount(Account)
	{DELETE [/account/{id}]}: delAccount(Integer)
	{POST [/account/add]}: addBooking(Payment)
	{POST [/account/finish]}: completeBooking(Payment)
	{POST [/account/cancel]}: cancelBooking(Payment)
13:14:51.151 [main] DEBUG org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping - 8 mappings in org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping
13:14:51.159 [main] DEBUG org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter - ControllerAdvice beans: 0 @ModelAttribute, 0 @InitBinder, 1 RequestBodyAdvice, 1 ResponseBodyAdvice
13:14:51.163 [main] DEBUG org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver - ControllerAdvice beans: 0 @ExceptionHandler, 1 ResponseBodyAdvice
13:14:51.164 [main] INFO org.springframework.mock.web.MockServletContext - Initializing Spring TestDispatcherServlet ''
13:14:51.164 [main] INFO org.springframework.test.web.servlet.TestDispatcherServlet - Initializing Servlet ''
13:14:51.164 [main] DEBUG org.springframework.test.web.servlet.TestDispatcherServlet - Detected AcceptHeaderLocaleResolver
13:14:51.164 [main] DEBUG org.springframework.test.web.servlet.TestDispatcherServlet - Detected FixedThemeResolver
13:14:51.164 [main] DEBUG org.springframework.test.web.servlet.TestDispatcherServlet - Detected org.springframework.web.servlet.view.DefaultRequestToViewNameTranslator@15639440
13:14:51.177 [main] DEBUG org.springframework.test.web.servlet.TestDispatcherServlet - Detected org.springframework.web.servlet.support.SessionFlashMapManager@121bb45b
13:14:51.177 [main] DEBUG org.springframework.test.web.servlet.TestDispatcherServlet - enableLoggingRequestDetails='false': request parameters and headers will be masked to prevent unsafe logging of potentially sensitive data
13:14:51.177 [main] INFO org.springframework.test.web.servlet.TestDispatcherServlet - Completed initialization in 13 ms
13:14:51.350 [main] DEBUG org.springframework.test.web.servlet.TestDispatcherServlet - PUT "/account", parameters={}
13:14:51.350 [main] DEBUG org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping - Mapped to com.example.cicdassignment.controller.AccountController#addAccount(Account)
13:14:51.417 [main] DEBUG org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor - Read "application/json" to [Account(id=15, name=mockito, contact=6789, payment=swish, history=500, booking=0, type=user)]
13:14:51.434 [main] DEBUG org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor - Using 'application/json', given [*/*] and supported [application/json, application/*+json]
13:14:51.434 [main] DEBUG org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor - Writing [true]
13:14:51.435 [main] DEBUG org.springframework.test.web.servlet.TestDispatcherServlet - Completed 200 OK
13:14:51.444 [main] DEBUG org.springframework.validation.beanvalidation.OptionalValidatorFactoryBean - Failed to set up a Bean Validation provider
javax.validation.NoProviderFoundException: Unable to create a Configuration, because no Jakarta Bean Validation provider could be found. Add a provider like Hibernate Validator (RI) to your classpath.
	at javax.validation.Validation$GenericBootstrapImpl.configure(Validation.java:291)
	at org.springframework.validation.beanvalidation.LocalValidatorFactoryBean.afterPropertiesSet(LocalValidatorFactoryBean.java:275)
	at org.springframework.validation.beanvalidation.OptionalValidatorFactoryBean.afterPropertiesSet(OptionalValidatorFactoryBean.java:40)
	at org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder$StandaloneConfiguration.mvcValidator(StandaloneMockMvcBuilder.java:533)
	at org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder.registerMvcSingletons(StandaloneMockMvcBuilder.java:404)
	at org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder.initWebAppContext(StandaloneMockMvcBuilder.java:385)
	at org.springframework.test.web.servlet.setup.AbstractMockMvcBuilder.build(AbstractMockMvcBuilder.java:157)
	at com.example.cicdassignment.controller.AccountControllerTest.setUp(AccountControllerTest.java:69)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.base/java.lang.reflect.Method.invoke(Method.java:566)
	at org.junit.platform.commons.util.ReflectionUtils.invokeMethod(ReflectionUtils.java:725)
	at org.junit.jupiter.engine.execution.MethodInvocation.proceed(MethodInvocation.java:60)
	at org.junit.jupiter.engine.execution.InvocationInterceptorChain$ValidatingInvocation.proceed(InvocationInterceptorChain.java:131)
	at org.junit.jupiter.engine.extension.TimeoutExtension.intercept(TimeoutExtension.java:149)
	at org.junit.jupiter.engine.extension.TimeoutExtension.interceptLifecycleMethod(TimeoutExtension.java:126)
	at org.junit.jupiter.engine.extension.TimeoutExtension.interceptBeforeEachMethod(TimeoutExtension.java:76)
	at org.junit.jupiter.engine.execution.ExecutableInvoker$ReflectiveInterceptorCall.lambda$ofVoidMethod$0(ExecutableInvoker.java:115)
	at org.junit.jupiter.engine.execution.ExecutableInvoker.lambda$invoke$0(ExecutableInvoker.java:105)
	at org.junit.jupiter.engine.execution.InvocationInterceptorChain$InterceptedInvocation.proceed(InvocationInterceptorChain.java:106)
	at org.junit.jupiter.engine.execution.InvocationInterceptorChain.proceed(InvocationInterceptorChain.java:64)
	at org.junit.jupiter.engine.execution.InvocationInterceptorChain.chainAndInvoke(InvocationInterceptorChain.java:45)
	at org.junit.jupiter.engine.execution.InvocationInterceptorChain.invoke(InvocationInterceptorChain.java:37)
	at org.junit.jupiter.engine.execution.ExecutableInvoker.invoke(ExecutableInvoker.java:104)
	at org.junit.jupiter.engine.execution.ExecutableInvoker.invoke(ExecutableInvoker.java:98)
	at org.junit.jupiter.engine.descriptor.ClassBasedTestDescriptor.invokeMethodInExtensionContext(ClassBasedTestDescriptor.java:506)
	at org.junit.jupiter.engine.descriptor.ClassBasedTestDescriptor.lambda$synthesizeBeforeEachMethodAdapter$21(ClassBasedTestDescriptor.java:491)
	at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.lambda$invokeBeforeEachMethods$3(TestMethodTestDescriptor.java:171)
	at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.lambda$invokeBeforeMethodsOrCallbacksUntilExceptionOccurs$6(TestMethodTestDescriptor.java:199)
	at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)
	at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.invokeBeforeMethodsOrCallbacksUntilExceptionOccurs(TestMethodTestDescriptor.java:199)
	at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.invokeBeforeEachMethods(TestMethodTestDescriptor.java:168)
	at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.execute(TestMethodTestDescriptor.java:131)
	at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.execute(TestMethodTestDescriptor.java:66)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$6(NodeTestTask.java:151)
	at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$8(NodeTestTask.java:141)
	at org.junit.platform.engine.support.hierarchical.Node.around(Node.java:137)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$9(NodeTestTask.java:139)
	at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.executeRecursively(NodeTestTask.java:138)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.execute(NodeTestTask.java:95)
	at java.base/java.util.ArrayList.forEach(ArrayList.java:1541)
	at org.junit.platform.engine.support.hierarchical.SameThreadHierarchicalTestExecutorService.invokeAll(SameThreadHierarchicalTestExecutorService.java:41)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$6(NodeTestTask.java:155)
	at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$8(NodeTestTask.java:141)
	at org.junit.platform.engine.support.hierarchical.Node.around(Node.java:137)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$9(NodeTestTask.java:139)
	at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.executeRecursively(NodeTestTask.java:138)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.execute(NodeTestTask.java:95)
	at java.base/java.util.ArrayList.forEach(ArrayList.java:1541)
	at org.junit.platform.engine.support.hierarchical.SameThreadHierarchicalTestExecutorService.invokeAll(SameThreadHierarchicalTestExecutorService.java:41)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$6(NodeTestTask.java:155)
	at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$8(NodeTestTask.java:141)
	at org.junit.platform.engine.support.hierarchical.Node.around(Node.java:137)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$9(NodeTestTask.java:139)
	at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.executeRecursively(NodeTestTask.java:138)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.execute(NodeTestTask.java:95)
	at org.junit.platform.engine.support.hierarchical.SameThreadHierarchicalTestExecutorService.submit(SameThreadHierarchicalTestExecutorService.java:35)
	at org.junit.platform.engine.support.hierarchical.HierarchicalTestExecutor.execute(HierarchicalTestExecutor.java:57)
	at org.junit.platform.engine.support.hierarchical.HierarchicalTestEngine.execute(HierarchicalTestEngine.java:54)
	at org.junit.platform.launcher.core.DefaultLauncher.execute(DefaultLauncher.java:220)
	at org.junit.platform.launcher.core.DefaultLauncher.lambda$execute$6(DefaultLauncher.java:188)
	at org.junit.platform.launcher.core.DefaultLauncher.withInterceptedStreams(DefaultLauncher.java:202)
	at org.junit.platform.launcher.core.DefaultLauncher.execute(DefaultLauncher.java:181)
	at org.junit.platform.launcher.core.DefaultLauncher.execute(DefaultLauncher.java:128)
	at org.apache.maven.surefire.junitplatform.JUnitPlatformProvider.invokeAllTests(JUnitPlatformProvider.java:150)
	at org.apache.maven.surefire.junitplatform.JUnitPlatformProvider.invoke(JUnitPlatformProvider.java:124)
	at org.apache.maven.surefire.booter.ForkedBooter.invokeProviderInSameClassLoader(ForkedBooter.java:384)
	at org.apache.maven.surefire.booter.ForkedBooter.runSuitesInProcess(ForkedBooter.java:345)
	at org.apache.maven.surefire.booter.ForkedBooter.execute(ForkedBooter.java:126)
	at org.apache.maven.surefire.booter.ForkedBooter.main(ForkedBooter.java:418)
13:14:51.451 [main] DEBUG _org.springframework.web.servlet.HandlerMapping.Mappings - 
	c.e.c.c.AccountController:
	{GET [/account]}: selectAll()
	{GET [/account/join]}: selectJoinAll()
	{PUT [/account]}: addAccount(Account)
	{POST [/account/update]}: updateAccount(Account)
	{DELETE [/account/{id}]}: delAccount(Integer)
	{POST [/account/add]}: addBooking(Payment)
	{POST [/account/finish]}: completeBooking(Payment)
	{POST [/account/cancel]}: cancelBooking(Payment)
13:14:51.465 [main] DEBUG org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping - 8 mappings in org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping
13:14:51.472 [main] DEBUG org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter - ControllerAdvice beans: 0 @ModelAttribute, 0 @InitBinder, 1 RequestBodyAdvice, 1 ResponseBodyAdvice
13:14:51.476 [main] DEBUG org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver - ControllerAdvice beans: 0 @ExceptionHandler, 1 ResponseBodyAdvice
13:14:51.476 [main] INFO org.springframework.mock.web.MockServletContext - Initializing Spring TestDispatcherServlet ''
13:14:51.476 [main] INFO org.springframework.test.web.servlet.TestDispatcherServlet - Initializing Servlet ''
13:14:51.476 [main] DEBUG org.springframework.test.web.servlet.TestDispatcherServlet - Detected AcceptHeaderLocaleResolver
13:14:51.476 [main] DEBUG org.springframework.test.web.servlet.TestDispatcherServlet - Detected FixedThemeResolver
13:14:51.476 [main] DEBUG org.springframework.test.web.servlet.TestDispatcherServlet - Detected org.springframework.web.servlet.view.DefaultRequestToViewNameTranslator@32b0876c
13:14:51.477 [main] DEBUG org.springframework.test.web.servlet.TestDispatcherServlet - Detected org.springframework.web.servlet.support.SessionFlashMapManager@2aaf152b
13:14:51.477 [main] DEBUG org.springframework.test.web.servlet.TestDispatcherServlet - enableLoggingRequestDetails='false': request parameters and headers will be masked to prevent unsafe logging of potentially sensitive data
13:14:51.477 [main] INFO org.springframework.test.web.servlet.TestDispatcherServlet - Completed initialization in 1 ms
13:14:51.485 [main] DEBUG org.springframework.test.web.servlet.TestDispatcherServlet - POST "/account/cancel", parameters={}
13:14:51.485 [main] DEBUG org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping - Mapped to com.example.cicdassignment.controller.AccountController#cancelBooking(Payment)
13:14:51.514 [main] DEBUG org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor - Read "application/json" to [Payment(userid=15, routeid=2)]
13:14:51.518 [main] DEBUG org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor - Using 'text/plain', given [*/*] and supported [text/plain, */*, application/json, application/*+json]
13:14:51.518 [main] DEBUG org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor - Writing ["credit card"]
13:14:51.520 [main] DEBUG org.springframework.test.web.servlet.TestDispatcherServlet - Completed 200 OK
13:14:51.536 [main] DEBUG org.springframework.validation.beanvalidation.OptionalValidatorFactoryBean - Failed to set up a Bean Validation provider
javax.validation.NoProviderFoundException: Unable to create a Configuration, because no Jakarta Bean Validation provider could be found. Add a provider like Hibernate Validator (RI) to your classpath.
	at javax.validation.Validation$GenericBootstrapImpl.configure(Validation.java:291)
	at org.springframework.validation.beanvalidation.LocalValidatorFactoryBean.afterPropertiesSet(LocalValidatorFactoryBean.java:275)
	at org.springframework.validation.beanvalidation.OptionalValidatorFactoryBean.afterPropertiesSet(OptionalValidatorFactoryBean.java:40)
	at org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder$StandaloneConfiguration.mvcValidator(StandaloneMockMvcBuilder.java:533)
	at org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder.registerMvcSingletons(StandaloneMockMvcBuilder.java:404)
	at org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder.initWebAppContext(StandaloneMockMvcBuilder.java:385)
	at org.springframework.test.web.servlet.setup.AbstractMockMvcBuilder.build(AbstractMockMvcBuilder.java:157)
	at com.example.cicdassignment.controller.AccountControllerTest.setUp(AccountControllerTest.java:69)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.base/java.lang.reflect.Method.invoke(Method.java:566)
	at org.junit.platform.commons.util.ReflectionUtils.invokeMethod(ReflectionUtils.java:725)
	at org.junit.jupiter.engine.execution.MethodInvocation.proceed(MethodInvocation.java:60)
	at org.junit.jupiter.engine.execution.InvocationInterceptorChain$ValidatingInvocation.proceed(InvocationInterceptorChain.java:131)
	at org.junit.jupiter.engine.extension.TimeoutExtension.intercept(TimeoutExtension.java:149)
	at org.junit.jupiter.engine.extension.TimeoutExtension.interceptLifecycleMethod(TimeoutExtension.java:126)
	at org.junit.jupiter.engine.extension.TimeoutExtension.interceptBeforeEachMethod(TimeoutExtension.java:76)
	at org.junit.jupiter.engine.execution.ExecutableInvoker$ReflectiveInterceptorCall.lambda$ofVoidMethod$0(ExecutableInvoker.java:115)
	at org.junit.jupiter.engine.execution.ExecutableInvoker.lambda$invoke$0(ExecutableInvoker.java:105)
	at org.junit.jupiter.engine.execution.InvocationInterceptorChain$InterceptedInvocation.proceed(InvocationInterceptorChain.java:106)
	at org.junit.jupiter.engine.execution.InvocationInterceptorChain.proceed(InvocationInterceptorChain.java:64)
	at org.junit.jupiter.engine.execution.InvocationInterceptorChain.chainAndInvoke(InvocationInterceptorChain.java:45)
	at org.junit.jupiter.engine.execution.InvocationInterceptorChain.invoke(InvocationInterceptorChain.java:37)
	at org.junit.jupiter.engine.execution.ExecutableInvoker.invoke(ExecutableInvoker.java:104)
	at org.junit.jupiter.engine.execution.ExecutableInvoker.invoke(ExecutableInvoker.java:98)
	at org.junit.jupiter.engine.descriptor.ClassBasedTestDescriptor.invokeMethodInExtensionContext(ClassBasedTestDescriptor.java:506)
	at org.junit.jupiter.engine.descriptor.ClassBasedTestDescriptor.lambda$synthesizeBeforeEachMethodAdapter$21(ClassBasedTestDescriptor.java:491)
	at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.lambda$invokeBeforeEachMethods$3(TestMethodTestDescriptor.java:171)
	at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.lambda$invokeBeforeMethodsOrCallbacksUntilExceptionOccurs$6(TestMethodTestDescriptor.java:199)
	at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)
	at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.invokeBeforeMethodsOrCallbacksUntilExceptionOccurs(TestMethodTestDescriptor.java:199)
	at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.invokeBeforeEachMethods(TestMethodTestDescriptor.java:168)
	at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.execute(TestMethodTestDescriptor.java:131)
	at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.execute(TestMethodTestDescriptor.java:66)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$6(NodeTestTask.java:151)
	at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$8(NodeTestTask.java:141)
	at org.junit.platform.engine.support.hierarchical.Node.around(Node.java:137)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$9(NodeTestTask.java:139)
	at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.executeRecursively(NodeTestTask.java:138)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.execute(NodeTestTask.java:95)
	at java.base/java.util.ArrayList.forEach(ArrayList.java:1541)
	at org.junit.platform.engine.support.hierarchical.SameThreadHierarchicalTestExecutorService.invokeAll(SameThreadHierarchicalTestExecutorService.java:41)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$6(NodeTestTask.java:155)
	at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$8(NodeTestTask.java:141)
	at org.junit.platform.engine.support.hierarchical.Node.around(Node.java:137)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$9(NodeTestTask.java:139)
	at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.executeRecursively(NodeTestTask.java:138)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.execute(NodeTestTask.java:95)
	at java.base/java.util.ArrayList.forEach(ArrayList.java:1541)
	at org.junit.platform.engine.support.hierarchical.SameThreadHierarchicalTestExecutorService.invokeAll(SameThreadHierarchicalTestExecutorService.java:41)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$6(NodeTestTask.java:155)
	at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$8(NodeTestTask.java:141)
	at org.junit.platform.engine.support.hierarchical.Node.around(Node.java:137)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$9(NodeTestTask.java:139)
	at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.executeRecursively(NodeTestTask.java:138)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.execute(NodeTestTask.java:95)
	at org.junit.platform.engine.support.hierarchical.SameThreadHierarchicalTestExecutorService.submit(SameThreadHierarchicalTestExecutorService.java:35)
	at org.junit.platform.engine.support.hierarchical.HierarchicalTestExecutor.execute(HierarchicalTestExecutor.java:57)
	at org.junit.platform.engine.support.hierarchical.HierarchicalTestEngine.execute(HierarchicalTestEngine.java:54)
	at org.junit.platform.launcher.core.DefaultLauncher.execute(DefaultLauncher.java:220)
	at org.junit.platform.launcher.core.DefaultLauncher.lambda$execute$6(DefaultLauncher.java:188)
	at org.junit.platform.launcher.core.DefaultLauncher.withInterceptedStreams(DefaultLauncher.java:202)
	at org.junit.platform.launcher.core.DefaultLauncher.execute(DefaultLauncher.java:181)
	at org.junit.platform.launcher.core.DefaultLauncher.execute(DefaultLauncher.java:128)
	at org.apache.maven.surefire.junitplatform.JUnitPlatformProvider.invokeAllTests(JUnitPlatformProvider.java:150)
	at org.apache.maven.surefire.junitplatform.JUnitPlatformProvider.invoke(JUnitPlatformProvider.java:124)
	at org.apache.maven.surefire.booter.ForkedBooter.invokeProviderInSameClassLoader(ForkedBooter.java:384)
	at org.apache.maven.surefire.booter.ForkedBooter.runSuitesInProcess(ForkedBooter.java:345)
	at org.apache.maven.surefire.booter.ForkedBooter.execute(ForkedBooter.java:126)
	at org.apache.maven.surefire.booter.ForkedBooter.main(ForkedBooter.java:418)
13:14:51.547 [main] DEBUG _org.springframework.web.servlet.HandlerMapping.Mappings - 
	c.e.c.c.AccountController:
	{GET [/account]}: selectAll()
	{GET [/account/join]}: selectJoinAll()
	{PUT [/account]}: addAccount(Account)
	{POST [/account/update]}: updateAccount(Account)
	{DELETE [/account/{id}]}: delAccount(Integer)
	{POST [/account/add]}: addBooking(Payment)
	{POST [/account/finish]}: completeBooking(Payment)
	{POST [/account/cancel]}: cancelBooking(Payment)
13:14:51.551 [main] DEBUG org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping - 8 mappings in org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping
13:14:51.567 [main] DEBUG org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter - ControllerAdvice beans: 0 @ModelAttribute, 0 @InitBinder, 1 RequestBodyAdvice, 1 ResponseBodyAdvice
13:14:51.574 [main] DEBUG org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver - ControllerAdvice beans: 0 @ExceptionHandler, 1 ResponseBodyAdvice
13:14:51.574 [main] INFO org.springframework.mock.web.MockServletContext - Initializing Spring TestDispatcherServlet ''
13:14:51.574 [main] INFO org.springframework.test.web.servlet.TestDispatcherServlet - Initializing Servlet ''
13:14:51.574 [main] DEBUG org.springframework.test.web.servlet.TestDispatcherServlet - Detected AcceptHeaderLocaleResolver
13:14:51.574 [main] DEBUG org.springframework.test.web.servlet.TestDispatcherServlet - Detected FixedThemeResolver
13:14:51.575 [main] DEBUG org.springframework.test.web.servlet.TestDispatcherServlet - Detected org.springframework.web.servlet.view.DefaultRequestToViewNameTranslator@7ad1caa2
13:14:51.575 [main] DEBUG org.springframework.test.web.servlet.TestDispatcherServlet - Detected org.springframework.web.servlet.support.SessionFlashMapManager@6b6b3572
13:14:51.575 [main] DEBUG org.springframework.test.web.servlet.TestDispatcherServlet - enableLoggingRequestDetails='false': request parameters and headers will be masked to prevent unsafe logging of potentially sensitive data
13:14:51.575 [main] INFO org.springframework.test.web.servlet.TestDispatcherServlet - Completed initialization in 1 ms
13:14:51.577 [main] DEBUG org.springframework.test.web.servlet.TestDispatcherServlet - POST "/account/finish", parameters={}
13:14:51.577 [main] DEBUG org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping - Mapped to com.example.cicdassignment.controller.AccountController#completeBooking(Payment)
13:14:51.583 [main] DEBUG org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor - Read "application/json" to [Payment(userid=15, routeid=2)]
13:14:51.585 [main] DEBUG org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor - Using 'text/plain', given [*/*] and supported [text/plain, */*, application/json, application/*+json]
13:14:51.585 [main] DEBUG org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor - Writing ["credit card"]
13:14:51.585 [main] DEBUG org.springframework.test.web.servlet.TestDispatcherServlet - Completed 200 OK
13:14:51.594 [main] DEBUG org.springframework.validation.beanvalidation.OptionalValidatorFactoryBean - Failed to set up a Bean Validation provider
javax.validation.NoProviderFoundException: Unable to create a Configuration, because no Jakarta Bean Validation provider could be found. Add a provider like Hibernate Validator (RI) to your classpath.
	at javax.validation.Validation$GenericBootstrapImpl.configure(Validation.java:291)
	at org.springframework.validation.beanvalidation.LocalValidatorFactoryBean.afterPropertiesSet(LocalValidatorFactoryBean.java:275)
	at org.springframework.validation.beanvalidation.OptionalValidatorFactoryBean.afterPropertiesSet(OptionalValidatorFactoryBean.java:40)
	at org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder$StandaloneConfiguration.mvcValidator(StandaloneMockMvcBuilder.java:533)
	at org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder.registerMvcSingletons(StandaloneMockMvcBuilder.java:404)
	at org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder.initWebAppContext(StandaloneMockMvcBuilder.java:385)
	at org.springframework.test.web.servlet.setup.AbstractMockMvcBuilder.build(AbstractMockMvcBuilder.java:157)
	at com.example.cicdassignment.controller.AccountControllerTest.setUp(AccountControllerTest.java:69)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.base/java.lang.reflect.Method.invoke(Method.java:566)
	at org.junit.platform.commons.util.ReflectionUtils.invokeMethod(ReflectionUtils.java:725)
	at org.junit.jupiter.engine.execution.MethodInvocation.proceed(MethodInvocation.java:60)
	at org.junit.jupiter.engine.execution.InvocationInterceptorChain$ValidatingInvocation.proceed(InvocationInterceptorChain.java:131)
	at org.junit.jupiter.engine.extension.TimeoutExtension.intercept(TimeoutExtension.java:149)
	at org.junit.jupiter.engine.extension.TimeoutExtension.interceptLifecycleMethod(TimeoutExtension.java:126)
	at org.junit.jupiter.engine.extension.TimeoutExtension.interceptBeforeEachMethod(TimeoutExtension.java:76)
	at org.junit.jupiter.engine.execution.ExecutableInvoker$ReflectiveInterceptorCall.lambda$ofVoidMethod$0(ExecutableInvoker.java:115)
	at org.junit.jupiter.engine.execution.ExecutableInvoker.lambda$invoke$0(ExecutableInvoker.java:105)
	at org.junit.jupiter.engine.execution.InvocationInterceptorChain$InterceptedInvocation.proceed(InvocationInterceptorChain.java:106)
	at org.junit.jupiter.engine.execution.InvocationInterceptorChain.proceed(InvocationInterceptorChain.java:64)
	at org.junit.jupiter.engine.execution.InvocationInterceptorChain.chainAndInvoke(InvocationInterceptorChain.java:45)
	at org.junit.jupiter.engine.execution.InvocationInterceptorChain.invoke(InvocationInterceptorChain.java:37)
	at org.junit.jupiter.engine.execution.ExecutableInvoker.invoke(ExecutableInvoker.java:104)
	at org.junit.jupiter.engine.execution.ExecutableInvoker.invoke(ExecutableInvoker.java:98)
	at org.junit.jupiter.engine.descriptor.ClassBasedTestDescriptor.invokeMethodInExtensionContext(ClassBasedTestDescriptor.java:506)
	at org.junit.jupiter.engine.descriptor.ClassBasedTestDescriptor.lambda$synthesizeBeforeEachMethodAdapter$21(ClassBasedTestDescriptor.java:491)
	at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.lambda$invokeBeforeEachMethods$3(TestMethodTestDescriptor.java:171)
	at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.lambda$invokeBeforeMethodsOrCallbacksUntilExceptionOccurs$6(TestMethodTestDescriptor.java:199)
	at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)
	at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.invokeBeforeMethodsOrCallbacksUntilExceptionOccurs(TestMethodTestDescriptor.java:199)
	at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.invokeBeforeEachMethods(TestMethodTestDescriptor.java:168)
	at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.execute(TestMethodTestDescriptor.java:131)
	at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.execute(TestMethodTestDescriptor.java:66)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$6(NodeTestTask.java:151)
	at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$8(NodeTestTask.java:141)
	at org.junit.platform.engine.support.hierarchical.Node.around(Node.java:137)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$9(NodeTestTask.java:139)
	at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.executeRecursively(NodeTestTask.java:138)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.execute(NodeTestTask.java:95)
	at java.base/java.util.ArrayList.forEach(ArrayList.java:1541)
	at org.junit.platform.engine.support.hierarchical.SameThreadHierarchicalTestExecutorService.invokeAll(SameThreadHierarchicalTestExecutorService.java:41)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$6(NodeTestTask.java:155)
	at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$8(NodeTestTask.java:141)
	at org.junit.platform.engine.support.hierarchical.Node.around(Node.java:137)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$9(NodeTestTask.java:139)
	at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.executeRecursively(NodeTestTask.java:138)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.execute(NodeTestTask.java:95)
	at java.base/java.util.ArrayList.forEach(ArrayList.java:1541)
	at org.junit.platform.engine.support.hierarchical.SameThreadHierarchicalTestExecutorService.invokeAll(SameThreadHierarchicalTestExecutorService.java:41)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$6(NodeTestTask.java:155)
	at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$8(NodeTestTask.java:141)
	at org.junit.platform.engine.support.hierarchical.Node.around(Node.java:137)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$9(NodeTestTask.java:139)
	at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.executeRecursively(NodeTestTask.java:138)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.execute(NodeTestTask.java:95)
	at org.junit.platform.engine.support.hierarchical.SameThreadHierarchicalTestExecutorService.submit(SameThreadHierarchicalTestExecutorService.java:35)
	at org.junit.platform.engine.support.hierarchical.HierarchicalTestExecutor.execute(HierarchicalTestExecutor.java:57)
	at org.junit.platform.engine.support.hierarchical.HierarchicalTestEngine.execute(HierarchicalTestEngine.java:54)
	at org.junit.platform.launcher.core.DefaultLauncher.execute(DefaultLauncher.java:220)
	at org.junit.platform.launcher.core.DefaultLauncher.lambda$execute$6(DefaultLauncher.java:188)
	at org.junit.platform.launcher.core.DefaultLauncher.withInterceptedStreams(DefaultLauncher.java:202)
	at org.junit.platform.launcher.core.DefaultLauncher.execute(DefaultLauncher.java:181)
	at org.junit.platform.launcher.core.DefaultLauncher.execute(DefaultLauncher.java:128)
	at org.apache.maven.surefire.junitplatform.JUnitPlatformProvider.invokeAllTests(JUnitPlatformProvider.java:150)
	at org.apache.maven.surefire.junitplatform.JUnitPlatformProvider.invoke(JUnitPlatformProvider.java:124)
	at org.apache.maven.surefire.booter.ForkedBooter.invokeProviderInSameClassLoader(ForkedBooter.java:384)
	at org.apache.maven.surefire.booter.ForkedBooter.runSuitesInProcess(ForkedBooter.java:345)
	at org.apache.maven.surefire.booter.ForkedBooter.execute(ForkedBooter.java:126)
	at org.apache.maven.surefire.booter.ForkedBooter.main(ForkedBooter.java:418)
13:14:51.601 [main] DEBUG _org.springframework.web.servlet.HandlerMapping.Mappings - 
	c.e.c.c.AccountController:
	{GET [/account]}: selectAll()
	{GET [/account/join]}: selectJoinAll()
	{PUT [/account]}: addAccount(Account)
	{POST [/account/update]}: updateAccount(Account)
	{DELETE [/account/{id}]}: delAccount(Integer)
	{POST [/account/add]}: addBooking(Payment)
	{POST [/account/finish]}: completeBooking(Payment)
	{POST [/account/cancel]}: cancelBooking(Payment)
13:14:51.604 [main] DEBUG org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping - 8 mappings in org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping
13:14:51.611 [main] DEBUG org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter - ControllerAdvice beans: 0 @ModelAttribute, 0 @InitBinder, 1 RequestBodyAdvice, 1 ResponseBodyAdvice
13:14:51.614 [main] DEBUG org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver - ControllerAdvice beans: 0 @ExceptionHandler, 1 ResponseBodyAdvice
13:14:51.614 [main] INFO org.springframework.mock.web.MockServletContext - Initializing Spring TestDispatcherServlet ''
13:14:51.615 [main] INFO org.springframework.test.web.servlet.TestDispatcherServlet - Initializing Servlet ''
13:14:51.615 [main] DEBUG org.springframework.test.web.servlet.TestDispatcherServlet - Detected AcceptHeaderLocaleResolver
13:14:51.615 [main] DEBUG org.springframework.test.web.servlet.TestDispatcherServlet - Detected FixedThemeResolver
13:14:51.625 [main] DEBUG org.springframework.test.web.servlet.TestDispatcherServlet - Detected org.springframework.web.servlet.view.DefaultRequestToViewNameTranslator@553bc36c
13:14:51.625 [main] DEBUG org.springframework.test.web.servlet.TestDispatcherServlet - Detected org.springframework.web.servlet.support.SessionFlashMapManager@380e1909
13:14:51.625 [main] DEBUG org.springframework.test.web.servlet.TestDispatcherServlet - enableLoggingRequestDetails='false': request parameters and headers will be masked to prevent unsafe logging of potentially sensitive data
13:14:51.625 [main] INFO org.springframework.test.web.servlet.TestDispatcherServlet - Completed initialization in 10 ms
13:14:51.627 [main] DEBUG org.springframework.test.web.servlet.TestDispatcherServlet - POST "/account/add", parameters={}
13:14:51.628 [main] DEBUG org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping - Mapped to com.example.cicdassignment.controller.AccountController#addBooking(Payment)
13:14:51.636 [main] DEBUG org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor - Read "application/json" to [Payment(userid=null, routeid=null)]
13:14:51.637 [main] DEBUG org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor - Using 'text/plain', given [*/*] and supported [text/plain, */*, application/json, application/*+json]
13:14:51.637 [main] DEBUG org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor - Nothing to write: null body
13:14:51.638 [main] DEBUG org.springframework.test.web.servlet.TestDispatcherServlet - Completed 200 OK
13:14:51.644 [main] DEBUG org.springframework.validation.beanvalidation.OptionalValidatorFactoryBean - Failed to set up a Bean Validation provider
javax.validation.NoProviderFoundException: Unable to create a Configuration, because no Jakarta Bean Validation provider could be found. Add a provider like Hibernate Validator (RI) to your classpath.
	at javax.validation.Validation$GenericBootstrapImpl.configure(Validation.java:291)
	at org.springframework.validation.beanvalidation.LocalValidatorFactoryBean.afterPropertiesSet(LocalValidatorFactoryBean.java:275)
	at org.springframework.validation.beanvalidation.OptionalValidatorFactoryBean.afterPropertiesSet(OptionalValidatorFactoryBean.java:40)
	at org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder$StandaloneConfiguration.mvcValidator(StandaloneMockMvcBuilder.java:533)
	at org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder.registerMvcSingletons(StandaloneMockMvcBuilder.java:404)
	at org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder.initWebAppContext(StandaloneMockMvcBuilder.java:385)
	at org.springframework.test.web.servlet.setup.AbstractMockMvcBuilder.build(AbstractMockMvcBuilder.java:157)
	at com.example.cicdassignment.controller.AccountControllerTest.setUp(AccountControllerTest.java:69)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.base/java.lang.reflect.Method.invoke(Method.java:566)
	at org.junit.platform.commons.util.ReflectionUtils.invokeMethod(ReflectionUtils.java:725)
	at org.junit.jupiter.engine.execution.MethodInvocation.proceed(MethodInvocation.java:60)
	at org.junit.jupiter.engine.execution.InvocationInterceptorChain$ValidatingInvocation.proceed(InvocationInterceptorChain.java:131)
	at org.junit.jupiter.engine.extension.TimeoutExtension.intercept(TimeoutExtension.java:149)
	at org.junit.jupiter.engine.extension.TimeoutExtension.interceptLifecycleMethod(TimeoutExtension.java:126)
	at org.junit.jupiter.engine.extension.TimeoutExtension.interceptBeforeEachMethod(TimeoutExtension.java:76)
	at org.junit.jupiter.engine.execution.ExecutableInvoker$ReflectiveInterceptorCall.lambda$ofVoidMethod$0(ExecutableInvoker.java:115)
	at org.junit.jupiter.engine.execution.ExecutableInvoker.lambda$invoke$0(ExecutableInvoker.java:105)
	at org.junit.jupiter.engine.execution.InvocationInterceptorChain$InterceptedInvocation.proceed(InvocationInterceptorChain.java:106)
	at org.junit.jupiter.engine.execution.InvocationInterceptorChain.proceed(InvocationInterceptorChain.java:64)
	at org.junit.jupiter.engine.execution.InvocationInterceptorChain.chainAndInvoke(InvocationInterceptorChain.java:45)
	at org.junit.jupiter.engine.execution.InvocationInterceptorChain.invoke(InvocationInterceptorChain.java:37)
	at org.junit.jupiter.engine.execution.ExecutableInvoker.invoke(ExecutableInvoker.java:104)
	at org.junit.jupiter.engine.execution.ExecutableInvoker.invoke(ExecutableInvoker.java:98)
	at org.junit.jupiter.engine.descriptor.ClassBasedTestDescriptor.invokeMethodInExtensionContext(ClassBasedTestDescriptor.java:506)
	at org.junit.jupiter.engine.descriptor.ClassBasedTestDescriptor.lambda$synthesizeBeforeEachMethodAdapter$21(ClassBasedTestDescriptor.java:491)
	at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.lambda$invokeBeforeEachMethods$3(TestMethodTestDescriptor.java:171)
	at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.lambda$invokeBeforeMethodsOrCallbacksUntilExceptionOccurs$6(TestMethodTestDescriptor.java:199)
	at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)
	at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.invokeBeforeMethodsOrCallbacksUntilExceptionOccurs(TestMethodTestDescriptor.java:199)
	at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.invokeBeforeEachMethods(TestMethodTestDescriptor.java:168)
	at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.execute(TestMethodTestDescriptor.java:131)
	at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.execute(TestMethodTestDescriptor.java:66)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$6(NodeTestTask.java:151)
	at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$8(NodeTestTask.java:141)
	at org.junit.platform.engine.support.hierarchical.Node.around(Node.java:137)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$9(NodeTestTask.java:139)
	at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.executeRecursively(NodeTestTask.java:138)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.execute(NodeTestTask.java:95)
	at java.base/java.util.ArrayList.forEach(ArrayList.java:1541)
	at org.junit.platform.engine.support.hierarchical.SameThreadHierarchicalTestExecutorService.invokeAll(SameThreadHierarchicalTestExecutorService.java:41)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$6(NodeTestTask.java:155)
	at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$8(NodeTestTask.java:141)
	at org.junit.platform.engine.support.hierarchical.Node.around(Node.java:137)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$9(NodeTestTask.java:139)
	at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.executeRecursively(NodeTestTask.java:138)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.execute(NodeTestTask.java:95)
	at java.base/java.util.ArrayList.forEach(ArrayList.java:1541)
	at org.junit.platform.engine.support.hierarchical.SameThreadHierarchicalTestExecutorService.invokeAll(SameThreadHierarchicalTestExecutorService.java:41)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$6(NodeTestTask.java:155)
	at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$8(NodeTestTask.java:141)
	at org.junit.platform.engine.support.hierarchical.Node.around(Node.java:137)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$9(NodeTestTask.java:139)
	at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.executeRecursively(NodeTestTask.java:138)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.execute(NodeTestTask.java:95)
	at org.junit.platform.engine.support.hierarchical.SameThreadHierarchicalTestExecutorService.submit(SameThreadHierarchicalTestExecutorService.java:35)
	at org.junit.platform.engine.support.hierarchical.HierarchicalTestExecutor.execute(HierarchicalTestExecutor.java:57)
	at org.junit.platform.engine.support.hierarchical.HierarchicalTestEngine.execute(HierarchicalTestEngine.java:54)
	at org.junit.platform.launcher.core.DefaultLauncher.execute(DefaultLauncher.java:220)
	at org.junit.platform.launcher.core.DefaultLauncher.lambda$execute$6(DefaultLauncher.java:188)
	at org.junit.platform.launcher.core.DefaultLauncher.withInterceptedStreams(DefaultLauncher.java:202)
	at org.junit.platform.launcher.core.DefaultLauncher.execute(DefaultLauncher.java:181)
	at org.junit.platform.launcher.core.DefaultLauncher.execute(DefaultLauncher.java:128)
	at org.apache.maven.surefire.junitplatform.JUnitPlatformProvider.invokeAllTests(JUnitPlatformProvider.java:150)
	at org.apache.maven.surefire.junitplatform.JUnitPlatformProvider.invoke(JUnitPlatformProvider.java:124)
	at org.apache.maven.surefire.booter.ForkedBooter.invokeProviderInSameClassLoader(ForkedBooter.java:384)
	at org.apache.maven.surefire.booter.ForkedBooter.runSuitesInProcess(ForkedBooter.java:345)
	at org.apache.maven.surefire.booter.ForkedBooter.execute(ForkedBooter.java:126)
	at org.apache.maven.surefire.booter.ForkedBooter.main(ForkedBooter.java:418)
13:14:51.650 [main] DEBUG _org.springframework.web.servlet.HandlerMapping.Mappings - 
	c.e.c.c.AccountController:
	{GET [/account]}: selectAll()
	{GET [/account/join]}: selectJoinAll()
	{PUT [/account]}: addAccount(Account)
	{POST [/account/update]}: updateAccount(Account)
	{DELETE [/account/{id}]}: delAccount(Integer)
	{POST [/account/add]}: addBooking(Payment)
	{POST [/account/finish]}: completeBooking(Payment)
	{POST [/account/cancel]}: cancelBooking(Payment)
13:14:51.652 [main] DEBUG org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping - 8 mappings in org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping
13:14:51.659 [main] DEBUG org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter - ControllerAdvice beans: 0 @ModelAttribute, 0 @InitBinder, 1 RequestBodyAdvice, 1 ResponseBodyAdvice
13:14:51.662 [main] DEBUG org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver - ControllerAdvice beans: 0 @ExceptionHandler, 1 ResponseBodyAdvice
13:14:51.662 [main] INFO org.springframework.mock.web.MockServletContext - Initializing Spring TestDispatcherServlet ''
13:14:51.662 [main] INFO org.springframework.test.web.servlet.TestDispatcherServlet - Initializing Servlet ''
13:14:51.663 [main] DEBUG org.springframework.test.web.servlet.TestDispatcherServlet - Detected AcceptHeaderLocaleResolver
13:14:51.663 [main] DEBUG org.springframework.test.web.servlet.TestDispatcherServlet - Detected FixedThemeResolver
13:14:51.663 [main] DEBUG org.springframework.test.web.servlet.TestDispatcherServlet - Detected org.springframework.web.servlet.view.DefaultRequestToViewNameTranslator@4ba6ec50
13:14:51.663 [main] DEBUG org.springframework.test.web.servlet.TestDispatcherServlet - Detected org.springframework.web.servlet.support.SessionFlashMapManager@642413d4
13:14:51.663 [main] DEBUG org.springframework.test.web.servlet.TestDispatcherServlet - enableLoggingRequestDetails='false': request parameters and headers will be masked to prevent unsafe logging of potentially sensitive data
13:14:51.663 [main] INFO org.springframework.test.web.servlet.TestDispatcherServlet - Completed initialization in 1 ms
13:14:51.665 [main] DEBUG org.springframework.test.web.servlet.TestDispatcherServlet - POST "/account/add", parameters={}
13:14:51.666 [main] DEBUG org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping - Mapped to com.example.cicdassignment.controller.AccountController#addBooking(Payment)
13:14:51.668 [main] DEBUG org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor - Read "application/json" to [Payment(userid=15, routeid=2)]
13:14:51.673 [main] DEBUG org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor - Using 'text/plain', given [*/*] and supported [text/plain, */*, application/json, application/*+json]
13:14:51.673 [main] DEBUG org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor - Writing ["swish"]
13:14:51.674 [main] DEBUG org.springframework.test.web.servlet.TestDispatcherServlet - Completed 200 OK
[[1;34mINFO[m] [1;32mTests run: [0;1;32m8[m, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 2.99 s - in com.example.cicdassignment.controller.[1mAccountControllerTest[m
[[1;34mINFO[m] Running com.example.cicdassignment.controller.[1mRouteControllerTest[m
13:14:51.692 [main] DEBUG org.springframework.validation.beanvalidation.OptionalValidatorFactoryBean - Failed to set up a Bean Validation provider
javax.validation.NoProviderFoundException: Unable to create a Configuration, because no Jakarta Bean Validation provider could be found. Add a provider like Hibernate Validator (RI) to your classpath.
	at javax.validation.Validation$GenericBootstrapImpl.configure(Validation.java:291)
	at org.springframework.validation.beanvalidation.LocalValidatorFactoryBean.afterPropertiesSet(LocalValidatorFactoryBean.java:275)
	at org.springframework.validation.beanvalidation.OptionalValidatorFactoryBean.afterPropertiesSet(OptionalValidatorFactoryBean.java:40)
	at org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder$StandaloneConfiguration.mvcValidator(StandaloneMockMvcBuilder.java:533)
	at org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder.registerMvcSingletons(StandaloneMockMvcBuilder.java:404)
	at org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder.initWebAppContext(StandaloneMockMvcBuilder.java:385)
	at org.springframework.test.web.servlet.setup.AbstractMockMvcBuilder.build(AbstractMockMvcBuilder.java:157)
	at com.example.cicdassignment.controller.RouteControllerTest.setUp(RouteControllerTest.java:43)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.base/java.lang.reflect.Method.invoke(Method.java:566)
	at org.junit.platform.commons.util.ReflectionUtils.invokeMethod(ReflectionUtils.java:725)
	at org.junit.jupiter.engine.execution.MethodInvocation.proceed(MethodInvocation.java:60)
	at org.junit.jupiter.engine.execution.InvocationInterceptorChain$ValidatingInvocation.proceed(InvocationInterceptorChain.java:131)
	at org.junit.jupiter.engine.extension.TimeoutExtension.intercept(TimeoutExtension.java:149)
	at org.junit.jupiter.engine.extension.TimeoutExtension.interceptLifecycleMethod(TimeoutExtension.java:126)
	at org.junit.jupiter.engine.extension.TimeoutExtension.interceptBeforeEachMethod(TimeoutExtension.java:76)
	at org.junit.jupiter.engine.execution.ExecutableInvoker$ReflectiveInterceptorCall.lambda$ofVoidMethod$0(ExecutableInvoker.java:115)
	at org.junit.jupiter.engine.execution.ExecutableInvoker.lambda$invoke$0(ExecutableInvoker.java:105)
	at org.junit.jupiter.engine.execution.InvocationInterceptorChain$InterceptedInvocation.proceed(InvocationInterceptorChain.java:106)
	at org.junit.jupiter.engine.execution.InvocationInterceptorChain.proceed(InvocationInterceptorChain.java:64)
	at org.junit.jupiter.engine.execution.InvocationInterceptorChain.chainAndInvoke(InvocationInterceptorChain.java:45)
	at org.junit.jupiter.engine.execution.InvocationInterceptorChain.invoke(InvocationInterceptorChain.java:37)
	at org.junit.jupiter.engine.execution.ExecutableInvoker.invoke(ExecutableInvoker.java:104)
	at org.junit.jupiter.engine.execution.ExecutableInvoker.invoke(ExecutableInvoker.java:98)
	at org.junit.jupiter.engine.descriptor.ClassBasedTestDescriptor.invokeMethodInExtensionContext(ClassBasedTestDescriptor.java:506)
	at org.junit.jupiter.engine.descriptor.ClassBasedTestDescriptor.lambda$synthesizeBeforeEachMethodAdapter$21(ClassBasedTestDescriptor.java:491)
	at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.lambda$invokeBeforeEachMethods$3(TestMethodTestDescriptor.java:171)
	at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.lambda$invokeBeforeMethodsOrCallbacksUntilExceptionOccurs$6(TestMethodTestDescriptor.java:199)
	at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)
	at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.invokeBeforeMethodsOrCallbacksUntilExceptionOccurs(TestMethodTestDescriptor.java:199)
	at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.invokeBeforeEachMethods(TestMethodTestDescriptor.java:168)
	at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.execute(TestMethodTestDescriptor.java:131)
	at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.execute(TestMethodTestDescriptor.java:66)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$6(NodeTestTask.java:151)
	at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$8(NodeTestTask.java:141)
	at org.junit.platform.engine.support.hierarchical.Node.around(Node.java:137)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$9(NodeTestTask.java:139)
	at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.executeRecursively(NodeTestTask.java:138)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.execute(NodeTestTask.java:95)
	at java.base/java.util.ArrayList.forEach(ArrayList.java:1541)
	at org.junit.platform.engine.support.hierarchical.SameThreadHierarchicalTestExecutorService.invokeAll(SameThreadHierarchicalTestExecutorService.java:41)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$6(NodeTestTask.java:155)
	at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$8(NodeTestTask.java:141)
	at org.junit.platform.engine.support.hierarchical.Node.around(Node.java:137)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$9(NodeTestTask.java:139)
	at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.executeRecursively(NodeTestTask.java:138)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.execute(NodeTestTask.java:95)
	at java.base/java.util.ArrayList.forEach(ArrayList.java:1541)
	at org.junit.platform.engine.support.hierarchical.SameThreadHierarchicalTestExecutorService.invokeAll(SameThreadHierarchicalTestExecutorService.java:41)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$6(NodeTestTask.java:155)
	at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$8(NodeTestTask.java:141)
	at org.junit.platform.engine.support.hierarchical.Node.around(Node.java:137)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$9(NodeTestTask.java:139)
	at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.executeRecursively(NodeTestTask.java:138)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.execute(NodeTestTask.java:95)
	at org.junit.platform.engine.support.hierarchical.SameThreadHierarchicalTestExecutorService.submit(SameThreadHierarchicalTestExecutorService.java:35)
	at org.junit.platform.engine.support.hierarchical.HierarchicalTestExecutor.execute(HierarchicalTestExecutor.java:57)
	at org.junit.platform.engine.support.hierarchical.HierarchicalTestEngine.execute(HierarchicalTestEngine.java:54)
	at org.junit.platform.launcher.core.DefaultLauncher.execute(DefaultLauncher.java:220)
	at org.junit.platform.launcher.core.DefaultLauncher.lambda$execute$6(DefaultLauncher.java:188)
	at org.junit.platform.launcher.core.DefaultLauncher.withInterceptedStreams(DefaultLauncher.java:202)
	at org.junit.platform.launcher.core.DefaultLauncher.execute(DefaultLauncher.java:181)
	at org.junit.platform.launcher.core.DefaultLauncher.execute(DefaultLauncher.java:128)
	at org.apache.maven.surefire.junitplatform.JUnitPlatformProvider.invokeAllTests(JUnitPlatformProvider.java:150)
	at org.apache.maven.surefire.junitplatform.JUnitPlatformProvider.invoke(JUnitPlatformProvider.java:124)
	at org.apache.maven.surefire.booter.ForkedBooter.invokeProviderInSameClassLoader(ForkedBooter.java:384)
	at org.apache.maven.surefire.booter.ForkedBooter.runSuitesInProcess(ForkedBooter.java:345)
	at org.apache.maven.surefire.booter.ForkedBooter.execute(ForkedBooter.java:126)
	at org.apache.maven.surefire.booter.ForkedBooter.main(ForkedBooter.java:418)
13:14:51.699 [main] DEBUG _org.springframework.web.servlet.HandlerMapping.Mappings - 
	c.e.c.c.RouteController:
	{GET [/route]}: selectAll()
	{PUT [/route]}: addRoute(Route)
	{POST [/route/update]}: updateRoute(Route)
13:14:51.701 [main] DEBUG org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping - 3 mappings in org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping
13:14:51.711 [main] DEBUG org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter - ControllerAdvice beans: 0 @ModelAttribute, 0 @InitBinder, 1 RequestBodyAdvice, 1 ResponseBodyAdvice
13:14:51.715 [main] DEBUG org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver - ControllerAdvice beans: 0 @ExceptionHandler, 1 ResponseBodyAdvice
13:14:51.715 [main] INFO org.springframework.mock.web.MockServletContext - Initializing Spring TestDispatcherServlet ''
13:14:51.715 [main] INFO org.springframework.test.web.servlet.TestDispatcherServlet - Initializing Servlet ''
13:14:51.715 [main] DEBUG org.springframework.test.web.servlet.TestDispatcherServlet - Detected AcceptHeaderLocaleResolver
13:14:51.716 [main] DEBUG org.springframework.test.web.servlet.TestDispatcherServlet - Detected FixedThemeResolver
13:14:51.716 [main] DEBUG org.springframework.test.web.servlet.TestDispatcherServlet - Detected org.springframework.web.servlet.view.DefaultRequestToViewNameTranslator@f324455
13:14:51.716 [main] DEBUG org.springframework.test.web.servlet.TestDispatcherServlet - Detected org.springframework.web.servlet.support.SessionFlashMapManager@3a894088
13:14:51.716 [main] DEBUG org.springframework.test.web.servlet.TestDispatcherServlet - enableLoggingRequestDetails='false': request parameters and headers will be masked to prevent unsafe logging of potentially sensitive data
13:14:51.716 [main] INFO org.springframework.test.web.servlet.TestDispatcherServlet - Completed initialization in 1 ms
13:14:51.718 [main] DEBUG org.springframework.test.web.servlet.TestDispatcherServlet - GET "/route", parameters={}
13:14:51.718 [main] DEBUG org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping - Mapped to com.example.cicdassignment.controller.RouteController#selectAll()
13:14:51.720 [main] DEBUG org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor - Using 'application/json', given [*/*] and supported [application/json, application/*+json]
13:14:51.812 [main] DEBUG org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor - Writing [[Route(id=23, departure=ytad, arrival=malmo, vehicle=train, expectDeparture=ystad c, expectArrival=m (truncated)...]
13:14:51.823 [main] DEBUG org.springframework.test.web.servlet.TestDispatcherServlet - Completed 200 OK
13:14:51.833 [main] DEBUG org.springframework.validation.beanvalidation.OptionalValidatorFactoryBean - Failed to set up a Bean Validation provider
javax.validation.NoProviderFoundException: Unable to create a Configuration, because no Jakarta Bean Validation provider could be found. Add a provider like Hibernate Validator (RI) to your classpath.
	at javax.validation.Validation$GenericBootstrapImpl.configure(Validation.java:291)
	at org.springframework.validation.beanvalidation.LocalValidatorFactoryBean.afterPropertiesSet(LocalValidatorFactoryBean.java:275)
	at org.springframework.validation.beanvalidation.OptionalValidatorFactoryBean.afterPropertiesSet(OptionalValidatorFactoryBean.java:40)
	at org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder$StandaloneConfiguration.mvcValidator(StandaloneMockMvcBuilder.java:533)
	at org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder.registerMvcSingletons(StandaloneMockMvcBuilder.java:404)
	at org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder.initWebAppContext(StandaloneMockMvcBuilder.java:385)
	at org.springframework.test.web.servlet.setup.AbstractMockMvcBuilder.build(AbstractMockMvcBuilder.java:157)
	at com.example.cicdassignment.controller.RouteControllerTest.setUp(RouteControllerTest.java:43)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.base/java.lang.reflect.Method.invoke(Method.java:566)
	at org.junit.platform.commons.util.ReflectionUtils.invokeMethod(ReflectionUtils.java:725)
	at org.junit.jupiter.engine.execution.MethodInvocation.proceed(MethodInvocation.java:60)
	at org.junit.jupiter.engine.execution.InvocationInterceptorChain$ValidatingInvocation.proceed(InvocationInterceptorChain.java:131)
	at org.junit.jupiter.engine.extension.TimeoutExtension.intercept(TimeoutExtension.java:149)
	at org.junit.jupiter.engine.extension.TimeoutExtension.interceptLifecycleMethod(TimeoutExtension.java:126)
	at org.junit.jupiter.engine.extension.TimeoutExtension.interceptBeforeEachMethod(TimeoutExtension.java:76)
	at org.junit.jupiter.engine.execution.ExecutableInvoker$ReflectiveInterceptorCall.lambda$ofVoidMethod$0(ExecutableInvoker.java:115)
	at org.junit.jupiter.engine.execution.ExecutableInvoker.lambda$invoke$0(ExecutableInvoker.java:105)
	at org.junit.jupiter.engine.execution.InvocationInterceptorChain$InterceptedInvocation.proceed(InvocationInterceptorChain.java:106)
	at org.junit.jupiter.engine.execution.InvocationInterceptorChain.proceed(InvocationInterceptorChain.java:64)
	at org.junit.jupiter.engine.execution.InvocationInterceptorChain.chainAndInvoke(InvocationInterceptorChain.java:45)
	at org.junit.jupiter.engine.execution.InvocationInterceptorChain.invoke(InvocationInterceptorChain.java:37)
	at org.junit.jupiter.engine.execution.ExecutableInvoker.invoke(ExecutableInvoker.java:104)
	at org.junit.jupiter.engine.execution.ExecutableInvoker.invoke(ExecutableInvoker.java:98)
	at org.junit.jupiter.engine.descriptor.ClassBasedTestDescriptor.invokeMethodInExtensionContext(ClassBasedTestDescriptor.java:506)
	at org.junit.jupiter.engine.descriptor.ClassBasedTestDescriptor.lambda$synthesizeBeforeEachMethodAdapter$21(ClassBasedTestDescriptor.java:491)
	at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.lambda$invokeBeforeEachMethods$3(TestMethodTestDescriptor.java:171)
	at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.lambda$invokeBeforeMethodsOrCallbacksUntilExceptionOccurs$6(TestMethodTestDescriptor.java:199)
	at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)
	at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.invokeBeforeMethodsOrCallbacksUntilExceptionOccurs(TestMethodTestDescriptor.java:199)
	at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.invokeBeforeEachMethods(TestMethodTestDescriptor.java:168)
	at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.execute(TestMethodTestDescriptor.java:131)
	at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.execute(TestMethodTestDescriptor.java:66)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$6(NodeTestTask.java:151)
	at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$8(NodeTestTask.java:141)
	at org.junit.platform.engine.support.hierarchical.Node.around(Node.java:137)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$9(NodeTestTask.java:139)
	at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.executeRecursively(NodeTestTask.java:138)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.execute(NodeTestTask.java:95)
	at java.base/java.util.ArrayList.forEach(ArrayList.java:1541)
	at org.junit.platform.engine.support.hierarchical.SameThreadHierarchicalTestExecutorService.invokeAll(SameThreadHierarchicalTestExecutorService.java:41)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$6(NodeTestTask.java:155)
	at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$8(NodeTestTask.java:141)
	at org.junit.platform.engine.support.hierarchical.Node.around(Node.java:137)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$9(NodeTestTask.java:139)
	at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.executeRecursively(NodeTestTask.java:138)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.execute(NodeTestTask.java:95)
	at java.base/java.util.ArrayList.forEach(ArrayList.java:1541)
	at org.junit.platform.engine.support.hierarchical.SameThreadHierarchicalTestExecutorService.invokeAll(SameThreadHierarchicalTestExecutorService.java:41)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$6(NodeTestTask.java:155)
	at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$8(NodeTestTask.java:141)
	at org.junit.platform.engine.support.hierarchical.Node.around(Node.java:137)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$9(NodeTestTask.java:139)
	at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.executeRecursively(NodeTestTask.java:138)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.execute(NodeTestTask.java:95)
	at org.junit.platform.engine.support.hierarchical.SameThreadHierarchicalTestExecutorService.submit(SameThreadHierarchicalTestExecutorService.java:35)
	at org.junit.platform.engine.support.hierarchical.HierarchicalTestExecutor.execute(HierarchicalTestExecutor.java:57)
	at org.junit.platform.engine.support.hierarchical.HierarchicalTestEngine.execute(HierarchicalTestEngine.java:54)
	at org.junit.platform.launcher.core.DefaultLauncher.execute(DefaultLauncher.java:220)
	at org.junit.platform.launcher.core.DefaultLauncher.lambda$execute$6(DefaultLauncher.java:188)
	at org.junit.platform.launcher.core.DefaultLauncher.withInterceptedStreams(DefaultLauncher.java:202)
	at org.junit.platform.launcher.core.DefaultLauncher.execute(DefaultLauncher.java:181)
	at org.junit.platform.launcher.core.DefaultLauncher.execute(DefaultLauncher.java:128)
	at org.apache.maven.surefire.junitplatform.JUnitPlatformProvider.invokeAllTests(JUnitPlatformProvider.java:150)
	at org.apache.maven.surefire.junitplatform.JUnitPlatformProvider.invoke(JUnitPlatformProvider.java:124)
	at org.apache.maven.surefire.booter.ForkedBooter.invokeProviderInSameClassLoader(ForkedBooter.java:384)
	at org.apache.maven.surefire.booter.ForkedBooter.runSuitesInProcess(ForkedBooter.java:345)
	at org.apache.maven.surefire.booter.ForkedBooter.execute(ForkedBooter.java:126)
	at org.apache.maven.surefire.booter.ForkedBooter.main(ForkedBooter.java:418)
13:14:51.837 [main] DEBUG _org.springframework.web.servlet.HandlerMapping.Mappings - 
	c.e.c.c.RouteController:
	{GET [/route]}: selectAll()
	{PUT [/route]}: addRoute(Route)
	{POST [/route/update]}: updateRoute(Route)
13:14:51.838 [main] DEBUG org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping - 3 mappings in org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping
13:14:51.853 [main] DEBUG org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter - ControllerAdvice beans: 0 @ModelAttribute, 0 @InitBinder, 1 RequestBodyAdvice, 1 ResponseBodyAdvice
13:14:51.856 [main] DEBUG org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver - ControllerAdvice beans: 0 @ExceptionHandler, 1 ResponseBodyAdvice
13:14:51.856 [main] INFO org.springframework.mock.web.MockServletContext - Initializing Spring TestDispatcherServlet ''
13:14:51.856 [main] INFO org.springframework.test.web.servlet.TestDispatcherServlet - Initializing Servlet ''
13:14:51.868 [main] DEBUG org.springframework.test.web.servlet.TestDispatcherServlet - Detected AcceptHeaderLocaleResolver
13:14:51.868 [main] DEBUG org.springframework.test.web.servlet.TestDispatcherServlet - Detected FixedThemeResolver
13:14:51.868 [main] DEBUG org.springframework.test.web.servlet.TestDispatcherServlet - Detected org.springframework.web.servlet.view.DefaultRequestToViewNameTranslator@36327cec
13:14:51.869 [main] DEBUG org.springframework.test.web.servlet.TestDispatcherServlet - Detected org.springframework.web.servlet.support.SessionFlashMapManager@410ae5ac
13:14:51.869 [main] DEBUG org.springframework.test.web.servlet.TestDispatcherServlet - enableLoggingRequestDetails='false': request parameters and headers will be masked to prevent unsafe logging of potentially sensitive data
13:14:51.869 [main] INFO org.springframework.test.web.servlet.TestDispatcherServlet - Completed initialization in 12 ms
13:14:51.890 [main] DEBUG org.springframework.test.web.servlet.TestDispatcherServlet - PUT "/route", parameters={}
13:14:51.896 [main] DEBUG org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping - Mapped to com.example.cicdassignment.controller.RouteController#addRoute(Route)
13:14:51.900 [main] DEBUG org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor - Read "application/json" to [Route(id=23, departure=ytad, arrival=malmo, vehicle=train, expectDeparture=ystad c, expectArrival=ma (truncated)...]
13:14:51.901 [main] DEBUG org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor - Using 'application/json', given [*/*] and supported [application/json, application/*+json]
13:14:51.901 [main] DEBUG org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor - Writing [true]
13:14:51.902 [main] DEBUG org.springframework.test.web.servlet.TestDispatcherServlet - Completed 200 OK
13:14:51.911 [main] DEBUG org.springframework.validation.beanvalidation.OptionalValidatorFactoryBean - Failed to set up a Bean Validation provider
javax.validation.NoProviderFoundException: Unable to create a Configuration, because no Jakarta Bean Validation provider could be found. Add a provider like Hibernate Validator (RI) to your classpath.
	at javax.validation.Validation$GenericBootstrapImpl.configure(Validation.java:291)
	at org.springframework.validation.beanvalidation.LocalValidatorFactoryBean.afterPropertiesSet(LocalValidatorFactoryBean.java:275)
	at org.springframework.validation.beanvalidation.OptionalValidatorFactoryBean.afterPropertiesSet(OptionalValidatorFactoryBean.java:40)
	at org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder$StandaloneConfiguration.mvcValidator(StandaloneMockMvcBuilder.java:533)
	at org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder.registerMvcSingletons(StandaloneMockMvcBuilder.java:404)
	at org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder.initWebAppContext(StandaloneMockMvcBuilder.java:385)
	at org.springframework.test.web.servlet.setup.AbstractMockMvcBuilder.build(AbstractMockMvcBuilder.java:157)
	at com.example.cicdassignment.controller.RouteControllerTest.setUp(RouteControllerTest.java:43)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.base/java.lang.reflect.Method.invoke(Method.java:566)
	at org.junit.platform.commons.util.ReflectionUtils.invokeMethod(ReflectionUtils.java:725)
	at org.junit.jupiter.engine.execution.MethodInvocation.proceed(MethodInvocation.java:60)
	at org.junit.jupiter.engine.execution.InvocationInterceptorChain$ValidatingInvocation.proceed(InvocationInterceptorChain.java:131)
	at org.junit.jupiter.engine.extension.TimeoutExtension.intercept(TimeoutExtension.java:149)
	at org.junit.jupiter.engine.extension.TimeoutExtension.interceptLifecycleMethod(TimeoutExtension.java:126)
	at org.junit.jupiter.engine.extension.TimeoutExtension.interceptBeforeEachMethod(TimeoutExtension.java:76)
	at org.junit.jupiter.engine.execution.ExecutableInvoker$ReflectiveInterceptorCall.lambda$ofVoidMethod$0(ExecutableInvoker.java:115)
	at org.junit.jupiter.engine.execution.ExecutableInvoker.lambda$invoke$0(ExecutableInvoker.java:105)
	at org.junit.jupiter.engine.execution.InvocationInterceptorChain$InterceptedInvocation.proceed(InvocationInterceptorChain.java:106)
	at org.junit.jupiter.engine.execution.InvocationInterceptorChain.proceed(InvocationInterceptorChain.java:64)
	at org.junit.jupiter.engine.execution.InvocationInterceptorChain.chainAndInvoke(InvocationInterceptorChain.java:45)
	at org.junit.jupiter.engine.execution.InvocationInterceptorChain.invoke(InvocationInterceptorChain.java:37)
	at org.junit.jupiter.engine.execution.ExecutableInvoker.invoke(ExecutableInvoker.java:104)
	at org.junit.jupiter.engine.execution.ExecutableInvoker.invoke(ExecutableInvoker.java:98)
	at org.junit.jupiter.engine.descriptor.ClassBasedTestDescriptor.invokeMethodInExtensionContext(ClassBasedTestDescriptor.java:506)
	at org.junit.jupiter.engine.descriptor.ClassBasedTestDescriptor.lambda$synthesizeBeforeEachMethodAdapter$21(ClassBasedTestDescriptor.java:491)
	at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.lambda$invokeBeforeEachMethods$3(TestMethodTestDescriptor.java:171)
	at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.lambda$invokeBeforeMethodsOrCallbacksUntilExceptionOccurs$6(TestMethodTestDescriptor.java:199)
	at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)
	at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.invokeBeforeMethodsOrCallbacksUntilExceptionOccurs(TestMethodTestDescriptor.java:199)
	at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.invokeBeforeEachMethods(TestMethodTestDescriptor.java:168)
	at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.execute(TestMethodTestDescriptor.java:131)
	at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.execute(TestMethodTestDescriptor.java:66)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$6(NodeTestTask.java:151)
	at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$8(NodeTestTask.java:141)
	at org.junit.platform.engine.support.hierarchical.Node.around(Node.java:137)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$9(NodeTestTask.java:139)
	at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.executeRecursively(NodeTestTask.java:138)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.execute(NodeTestTask.java:95)
	at java.base/java.util.ArrayList.forEach(ArrayList.java:1541)
	at org.junit.platform.engine.support.hierarchical.SameThreadHierarchicalTestExecutorService.invokeAll(SameThreadHierarchicalTestExecutorService.java:41)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$6(NodeTestTask.java:155)
	at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$8(NodeTestTask.java:141)
	at org.junit.platform.engine.support.hierarchical.Node.around(Node.java:137)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$9(NodeTestTask.java:139)
	at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.executeRecursively(NodeTestTask.java:138)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.execute(NodeTestTask.java:95)
	at java.base/java.util.ArrayList.forEach(ArrayList.java:1541)
	at org.junit.platform.engine.support.hierarchical.SameThreadHierarchicalTestExecutorService.invokeAll(SameThreadHierarchicalTestExecutorService.java:41)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$6(NodeTestTask.java:155)
	at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$8(NodeTestTask.java:141)
	at org.junit.platform.engine.support.hierarchical.Node.around(Node.java:137)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.lambda$executeRecursively$9(NodeTestTask.java:139)
	at org.junit.platform.engine.support.hierarchical.ThrowableCollector.execute(ThrowableCollector.java:73)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.executeRecursively(NodeTestTask.java:138)
	at org.junit.platform.engine.support.hierarchical.NodeTestTask.execute(NodeTestTask.java:95)
	at org.junit.platform.engine.support.hierarchical.SameThreadHierarchicalTestExecutorService.submit(SameThreadHierarchicalTestExecutorService.java:35)
	at org.junit.platform.engine.support.hierarchical.HierarchicalTestExecutor.execute(HierarchicalTestExecutor.java:57)
	at org.junit.platform.engine.support.hierarchical.HierarchicalTestEngine.execute(HierarchicalTestEngine.java:54)
	at org.junit.platform.launcher.core.DefaultLauncher.execute(DefaultLauncher.java:220)
	at org.junit.platform.launcher.core.DefaultLauncher.lambda$execute$6(DefaultLauncher.java:188)
	at org.junit.platform.launcher.core.DefaultLauncher.withInterceptedStreams(DefaultLauncher.java:202)
	at org.junit.platform.launcher.core.DefaultLauncher.execute(DefaultLauncher.java:181)
	at org.junit.platform.launcher.core.DefaultLauncher.execute(DefaultLauncher.java:128)
	at org.apache.maven.surefire.junitplatform.JUnitPlatformProvider.invokeAllTests(JUnitPlatformProvider.java:150)
	at org.apache.maven.surefire.junitplatform.JUnitPlatformProvider.invoke(JUnitPlatformProvider.java:124)
	at org.apache.maven.surefire.booter.ForkedBooter.invokeProviderInSameClassLoader(ForkedBooter.java:384)
	at org.apache.maven.surefire.booter.ForkedBooter.runSuitesInProcess(ForkedBooter.java:345)
	at org.apache.maven.surefire.booter.ForkedBooter.execute(ForkedBooter.java:126)
	at org.apache.maven.surefire.booter.ForkedBooter.main(ForkedBooter.java:418)
13:14:51.919 [main] DEBUG _org.springframework.web.servlet.HandlerMapping.Mappings - 
	c.e.c.c.RouteController:
	{GET [/route]}: selectAll()
	{PUT [/route]}: addRoute(Route)
	{POST [/route/update]}: updateRoute(Route)
13:14:51.920 [main] DEBUG org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping - 3 mappings in org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping
13:14:51.933 [main] DEBUG org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter - ControllerAdvice beans: 0 @ModelAttribute, 0 @InitBinder, 1 RequestBodyAdvice, 1 ResponseBodyAdvice
13:14:51.938 [main] DEBUG org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver - ControllerAdvice beans: 0 @ExceptionHandler, 1 ResponseBodyAdvice
13:14:51.939 [main] INFO org.springframework.mock.web.MockServletContext - Initializing Spring TestDispatcherServlet ''
13:14:51.940 [main] INFO org.springframework.test.web.servlet.TestDispatcherServlet - Initializing Servlet ''
13:14:51.942 [main] DEBUG org.springframework.test.web.servlet.TestDispatcherServlet - Detected AcceptHeaderLocaleResolver
13:14:51.942 [main] DEBUG org.springframework.test.web.servlet.TestDispatcherServlet - Detected FixedThemeResolver
13:14:51.943 [main] DEBUG org.springframework.test.web.servlet.TestDispatcherServlet - Detected org.springframework.web.servlet.view.DefaultRequestToViewNameTranslator@554c4eaa
13:14:51.943 [main] DEBUG org.springframework.test.web.servlet.TestDispatcherServlet - Detected org.springframework.web.servlet.support.SessionFlashMapManager@29fd8e67
13:14:51.943 [main] DEBUG org.springframework.test.web.servlet.TestDispatcherServlet - enableLoggingRequestDetails='false': request parameters and headers will be masked to prevent unsafe logging of potentially sensitive data
13:14:51.944 [main] INFO org.springframework.test.web.servlet.TestDispatcherServlet - Completed initialization in 2 ms
13:14:51.945 [main] DEBUG org.springframework.test.web.servlet.TestDispatcherServlet - POST "/route/update", parameters={}
13:14:51.957 [main] DEBUG org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping - Mapped to com.example.cicdassignment.controller.RouteController#updateRoute(Route)
13:14:51.964 [main] DEBUG org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor - Read "application/json" to [Route(id=23, departure=ytad, arrival=malmo, vehicle=train, expectDeparture=ystad c, expectArrival=ma (truncated)...]
13:14:51.965 [main] DEBUG org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor - Using 'application/json', given [*/*] and supported [application/json, application/*+json]
13:14:51.966 [main] DEBUG org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor - Writing [true]
13:14:51.966 [main] DEBUG org.springframework.test.web.servlet.TestDispatcherServlet - Completed 200 OK
[[1;34mINFO[m] [1;32mTests run: [0;1;32m3[m, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.24 s - in com.example.cicdassignment.controller.[1mRouteControllerTest[m
[[1;34mINFO[m] Running com.example.cicdassignment.service.[1mRouteServiceImplTest[m
[[1;34mINFO[m] [1;32mTests run: [0;1;32m5[m, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.007 s - in com.example.cicdassignment.service.[1mRouteServiceImplTest[m
[[1;34mINFO[m] Running com.example.cicdassignment.service.[1mAccountServiceImplTest[m
[[1;34mINFO[m] [1;32mTests run: [0;1;32m8[m, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.102 s - in com.example.cicdassignment.service.[1mAccountServiceImplTest[m
[[1;34mINFO[m] Running com.example.cicdassignment.[1mCicdAssignmentApplicationTests[m
13:14:52.248 [main] DEBUG org.springframework.test.context.BootstrapUtils - Instantiating CacheAwareContextLoaderDelegate from class [org.springframework.test.context.cache.DefaultCacheAwareContextLoaderDelegate]
13:14:52.252 [main] DEBUG org.springframework.test.context.BootstrapUtils - Instantiating BootstrapContext using constructor [public org.springframework.test.context.support.DefaultBootstrapContext(java.lang.Class,org.springframework.test.context.CacheAwareContextLoaderDelegate)]
13:14:52.267 [main] DEBUG org.springframework.test.context.BootstrapUtils - Instantiating TestContextBootstrapper for test class [com.example.cicdassignment.CicdAssignmentApplicationTests] from class [org.springframework.boot.test.context.SpringBootTestContextBootstrapper]
13:14:52.277 [main] INFO org.springframework.boot.test.context.SpringBootTestContextBootstrapper - Neither @ContextConfiguration nor @ContextHierarchy found for test class [com.example.cicdassignment.CicdAssignmentApplicationTests], using SpringBootContextLoader
13:14:52.281 [main] DEBUG org.springframework.test.context.support.AbstractContextLoader - Did not detect default resource location for test class [com.example.cicdassignment.CicdAssignmentApplicationTests]: class path resource [com/example/cicdassignment/CicdAssignmentApplicationTests-context.xml] does not exist
13:14:52.282 [main] DEBUG org.springframework.test.context.support.AbstractContextLoader - Did not detect default resource location for test class [com.example.cicdassignment.CicdAssignmentApplicationTests]: class path resource [com/example/cicdassignment/CicdAssignmentApplicationTestsContext.groovy] does not exist
13:14:52.282 [main] INFO org.springframework.test.context.support.AbstractContextLoader - Could not detect default resource locations for test class [com.example.cicdassignment.CicdAssignmentApplicationTests]: no resource found for suffixes {-context.xml, Context.groovy}.
13:14:52.283 [main] INFO org.springframework.test.context.support.AnnotationConfigContextLoaderUtils - Could not detect default configuration classes for test class [com.example.cicdassignment.CicdAssignmentApplicationTests]: CicdAssignmentApplicationTests does not declare any static, non-private, non-final, nested classes annotated with @Configuration.
13:14:52.337 [main] DEBUG org.springframework.test.context.support.ActiveProfilesUtils - Could not find an 'annotation declaring class' for annotation type [org.springframework.test.context.ActiveProfiles] and class [com.example.cicdassignment.CicdAssignmentApplicationTests]
13:14:52.401 [main] DEBUG org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider - Identified candidate component class: file [/var/lib/jenkins/workspace/multipleScan_master/target/classes/com/example/cicdassignment/CicdAssignmentApplication.class]
13:14:52.402 [main] INFO org.springframework.boot.test.context.SpringBootTestContextBootstrapper - Found @SpringBootConfiguration com.example.cicdassignment.CicdAssignmentApplication for test class com.example.cicdassignment.CicdAssignmentApplicationTests
13:14:52.477 [main] DEBUG org.springframework.boot.test.context.SpringBootTestContextBootstrapper - @TestExecutionListeners is not present for class [com.example.cicdassignment.CicdAssignmentApplicationTests]: using defaults.
13:14:52.480 [main] INFO org.springframework.boot.test.context.SpringBootTestContextBootstrapper - Loaded default TestExecutionListener class names from location [META-INF/spring.factories]: [org.springframework.boot.test.mock.mockito.MockitoTestExecutionListener, org.springframework.boot.test.mock.mockito.ResetMocksTestExecutionListener, org.springframework.boot.test.autoconfigure.restdocs.RestDocsTestExecutionListener, org.springframework.boot.test.autoconfigure.web.client.MockRestServiceServerResetTestExecutionListener, org.springframework.boot.test.autoconfigure.web.servlet.MockMvcPrintOnlyOnFailureTestExecutionListener, org.springframework.boot.test.autoconfigure.web.servlet.WebDriverTestExecutionListener, org.springframework.boot.test.autoconfigure.webservices.client.MockWebServiceServerTestExecutionListener, org.springframework.test.context.web.ServletTestExecutionListener, org.springframework.test.context.support.DirtiesContextBeforeModesTestExecutionListener, org.springframework.test.context.event.ApplicationEventsTestExecutionListener, org.springframework.test.context.support.DependencyInjectionTestExecutionListener, org.springframework.test.context.support.DirtiesContextTestExecutionListener, org.springframework.test.context.transaction.TransactionalTestExecutionListener, org.springframework.test.context.jdbc.SqlScriptsTestExecutionListener, org.springframework.test.context.event.EventPublishingTestExecutionListener]
13:14:52.501 [main] INFO org.springframework.boot.test.context.SpringBootTestContextBootstrapper - Using TestExecutionListeners: [org.springframework.test.context.web.ServletTestExecutionListener@5ba03c82, org.springframework.test.context.support.DirtiesContextBeforeModesTestExecutionListener@60df7989, org.springframework.test.context.event.ApplicationEventsTestExecutionListener@7ab1127c, org.springframework.boot.test.mock.mockito.MockitoTestExecutionListener@6e60f18, org.springframework.boot.test.autoconfigure.SpringBootDependencyInjectionTestExecutionListener@5cf8676a, org.springframework.test.context.support.DirtiesContextTestExecutionListener@47f04e4d, org.springframework.test.context.transaction.TransactionalTestExecutionListener@3520963d, org.springframework.test.context.jdbc.SqlScriptsTestExecutionListener@388d14e, org.springframework.test.context.event.EventPublishingTestExecutionListener@1cd43562, org.springframework.boot.test.mock.mockito.ResetMocksTestExecutionListener@59939293, org.springframework.boot.test.autoconfigure.restdocs.RestDocsTestExecutionListener@68b366e2, org.springframework.boot.test.autoconfigure.web.client.MockRestServiceServerResetTestExecutionListener@2d74c81b, org.springframework.boot.test.autoconfigure.web.servlet.MockMvcPrintOnlyOnFailureTestExecutionListener@10b687f2, org.springframework.boot.test.autoconfigure.web.servlet.WebDriverTestExecutionListener@55f4887d, org.springframework.boot.test.autoconfigure.webservices.client.MockWebServiceServerTestExecutionListener@26837057]
13:14:52.507 [main] DEBUG org.springframework.test.context.support.AbstractDirtiesContextTestExecutionListener - Before test class: context [DefaultTestContext@294aba23 testClass = CicdAssignmentApplicationTests, testInstance = [null], testMethod = [null], testException = [null], mergedContextConfiguration = [WebMergedContextConfiguration@69909c14 testClass = CicdAssignmentApplicationTests, locations = '{}', classes = '{class com.example.cicdassignment.CicdAssignmentApplication}', contextInitializerClasses = '[]', activeProfiles = '{}', propertySourceLocations = '{}', propertySourceProperties = '{org.springframework.boot.test.context.SpringBootTestContextBootstrapper=true}', contextCustomizers = set[org.springframework.boot.test.context.filter.ExcludeFilterContextCustomizer@6f6c6077, org.springframework.boot.test.json.DuplicateJsonObjectContextCustomizerFactory$DuplicateJsonObjectContextCustomizer@58606c91, org.springframework.boot.test.mock.mockito.MockitoContextCustomizer@0, org.springframework.boot.test.web.client.TestRestTemplateContextCustomizer@66deec87, org.springframework.boot.test.autoconfigure.actuate.metrics.MetricsExportContextCustomizerFactory$DisableMetricExportContextCustomizer@2721044, org.springframework.boot.test.autoconfigure.properties.PropertyMappingContextCustomizer@0, org.springframework.boot.test.autoconfigure.web.servlet.WebDriverContextCustomizerFactory$Customizer@2b7e8044, org.springframework.boot.test.context.SpringBootTestArgs@1, org.springframework.boot.test.context.SpringBootTestWebEnvironment@7dc222ae], resourceBasePath = 'src/main/webapp', contextLoader = 'org.springframework.boot.test.context.SpringBootContextLoader', parent = [null]], attributes = map['org.springframework.test.context.web.ServletTestExecutionListener.activateListener' -> true]], class annotated with @DirtiesContext [false] with mode [null].
Junit test

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::               (v2.7.15)

2023-09-26 13:14:52.964  INFO 7107 --- [           main] c.e.c.CicdAssignmentApplicationTests     : Starting CicdAssignmentApplicationTests using Java 11.0.20.1 on ubuntu1 with PID 7107 (started by jenkins in /var/lib/jenkins/workspace/multipleScan_master)
2023-09-26 13:14:52.965  INFO 7107 --- [           main] c.e.c.CicdAssignmentApplicationTests     : No active profile set, falling back to 1 default profile: "default"
2023-09-26 13:14:54.075  INFO 7107 --- [           main] .s.d.r.c.RepositoryConfigurationDelegate : Bootstrapping Spring Data JDBC repositories in DEFAULT mode.
2023-09-26 13:14:54.096  INFO 7107 --- [           main] .s.d.r.c.RepositoryConfigurationDelegate : Finished Spring Data repository scanning in 13 ms. Found 0 JDBC repository interfaces.
2023-09-26 13:14:54.633  INFO 7107 --- [           main] trationDelegate$BeanPostProcessorChecker : Bean 'org.springframework.ws.config.annotation.DelegatingWsConfiguration' of type [org.springframework.ws.config.annotation.DelegatingWsConfiguration$$EnhancerBySpringCGLIB$$9a235ad5] is not eligible for getting processed by all BeanPostProcessors (for example: not eligible for auto-proxying)
2023-09-26 13:14:54.730  INFO 7107 --- [           main] .w.s.a.s.AnnotationActionEndpointMapping : Supporting [WS-Addressing August 2004, WS-Addressing 1.0]
2023-09-26 13:14:54.928  INFO 7107 --- [           main] c.a.d.s.b.a.DruidDataSourceAutoConfigure : Init DruidDataSource
2023-09-26 13:14:55.395  INFO 7107 --- [           main] com.alibaba.druid.pool.DruidDataSource   : {dataSource-1} inited
2023-09-26 13:14:58.276  INFO 7107 --- [           main] c.e.c.CicdAssignmentApplicationTests     : Started CicdAssignmentApplicationTests in 5.728 seconds (JVM running for 10.516)
JAccount(id=2, name=tiger, contact=4567, payment=swish, history=0, booking=1, type=admin, routeId=null, departure=malmo, arrival=goteborg, vehicle=train, expectDeparture=malmo c, expectArrival=goteborg c, price=500, deliver=sl, promotion=)
[Account(id=1, name=niuniu, contact=1234, payment=credit card, history=3000, booking=1, type=user), Account(id=2, name=tiger, contact=4567, payment=swish, history=0, booking=1, type=admin), Account(id=3, name=panda, contact=6789, payment=swish, history=2000, booking=0, type=delivery), Account(id=4, name=deer, contact=9807, payment=credit card, history=0, booking=2, type=delivery), Account(id=5, name=testName, contact=1111, payment=swish, history=0, booking=0, type=user), Account(id=7, name=testName, contact=1111, payment=swish, history=0, booking=2, type=user), Account(id=8, name=testName, contact=1111, payment=swish, history=0, booking=2, type=user), Account(id=9, name=testName2, contact=2222, payment=swish2, history=0, booking=0, type=admin), Account(id=10, name=testName, contact=1111, payment=swish, history=0, booking=2, type=user), Account(id=11, name=testName, contact=1111, payment=swish, history=0, booking=2, type=user), Account(id=12, name=testName, contact=1111, payment=swish, history=0, booking=2, type=user), Account(id=13, name=testName, contact=1111, payment=swish, history=0, booking=2, type=user), Account(id=15, name=testName, contact=1111, payment=swish, history=0, booking=2, type=user), Account(id=17, name=miaomiao, contact=1234, payment=credit card, history=0, booking=0, type=user), Account(id=18, name=cat, contact=0000, payment=credit card, history=0, booking=0, type=admin)]
2023-09-26 13:14:58.455  INFO 7107 --- [           main] o.s.t.c.transaction.TransactionContext   : Began transaction (1) for test context [DefaultTestContext@294aba23 testClass = CicdAssignmentApplicationTests, testInstance = com.example.cicdassignment.CicdAssignmentApplicationTests@2d114d27, testMethod = testUpdateAccount@CicdAssignmentApplicationTests, testException = [null], mergedContextConfiguration = [WebMergedContextConfiguration@69909c14 testClass = CicdAssignmentApplicationTests, locations = '{}', classes = '{class com.example.cicdassignment.CicdAssignmentApplication}', contextInitializerClasses = '[]', activeProfiles = '{}', propertySourceLocations = '{}', propertySourceProperties = '{org.springframework.boot.test.context.SpringBootTestContextBootstrapper=true}', contextCustomizers = set[org.springframework.boot.test.context.filter.ExcludeFilterContextCustomizer@6f6c6077, org.springframework.boot.test.json.DuplicateJsonObjectContextCustomizerFactory$DuplicateJsonObjectContextCustomizer@58606c91, org.springframework.boot.test.mock.mockito.MockitoContextCustomizer@0, org.springframework.boot.test.web.client.TestRestTemplateContextCustomizer@66deec87, org.springframework.boot.test.autoconfigure.actuate.metrics.MetricsExportContextCustomizerFactory$DisableMetricExportContextCustomizer@2721044, org.springframework.boot.test.autoconfigure.properties.PropertyMappingContextCustomizer@0, org.springframework.boot.test.autoconfigure.web.servlet.WebDriverContextCustomizerFactory$Customizer@2b7e8044, org.springframework.boot.test.context.SpringBootTestArgs@1, org.springframework.boot.test.context.SpringBootTestWebEnvironment@7dc222ae], resourceBasePath = 'src/main/webapp', contextLoader = 'org.springframework.boot.test.context.SpringBootContextLoader', parent = [null]], attributes = map['org.springframework.test.context.web.ServletTestExecutionListener.activateListener' -> true, 'org.springframework.test.context.web.ServletTestExecutionListener.populatedRequestContextHolder' -> true, 'org.springframework.test.context.web.ServletTestExecutionListener.resetRequestContextHolder' -> true, 'org.springframework.test.context.event.ApplicationEventsTestExecutionListener.recordApplicationEvents' -> false]]; transaction manager [org.springframework.jdbc.support.JdbcTransactionManager@28fef9a2]; rollback [true]
2023-09-26 13:14:58.480  INFO 7107 --- [           main] o.s.t.c.transaction.TransactionContext   : Rolled back transaction for test: [DefaultTestContext@294aba23 testClass = CicdAssignmentApplicationTests, testInstance = com.example.cicdassignment.CicdAssignmentApplicationTests@2d114d27, testMethod = testUpdateAccount@CicdAssignmentApplicationTests, testException = [null], mergedContextConfiguration = [WebMergedContextConfiguration@69909c14 testClass = CicdAssignmentApplicationTests, locations = '{}', classes = '{class com.example.cicdassignment.CicdAssignmentApplication}', contextInitializerClasses = '[]', activeProfiles = '{}', propertySourceLocations = '{}', propertySourceProperties = '{org.springframework.boot.test.context.SpringBootTestContextBootstrapper=true}', contextCustomizers = set[org.springframework.boot.test.context.filter.ExcludeFilterContextCustomizer@6f6c6077, org.springframework.boot.test.json.DuplicateJsonObjectContextCustomizerFactory$DuplicateJsonObjectContextCustomizer@58606c91, org.springframework.boot.test.mock.mockito.MockitoContextCustomizer@0, org.springframework.boot.test.web.client.TestRestTemplateContextCustomizer@66deec87, org.springframework.boot.test.autoconfigure.actuate.metrics.MetricsExportContextCustomizerFactory$DisableMetricExportContextCustomizer@2721044, org.springframework.boot.test.autoconfigure.properties.PropertyMappingContextCustomizer@0, org.springframework.boot.test.autoconfigure.web.servlet.WebDriverContextCustomizerFactory$Customizer@2b7e8044, org.springframework.boot.test.context.SpringBootTestArgs@1, org.springframework.boot.test.context.SpringBootTestWebEnvironment@7dc222ae], resourceBasePath = 'src/main/webapp', contextLoader = 'org.springframework.boot.test.context.SpringBootContextLoader', parent = [null]], attributes = map['org.springframework.test.context.web.ServletTestExecutionListener.activateListener' -> true, 'org.springframework.test.context.web.ServletTestExecutionListener.populatedRequestContextHolder' -> true, 'org.springframework.test.context.web.ServletTestExecutionListener.resetRequestContextHolder' -> true, 'org.springframework.test.context.event.ApplicationEventsTestExecutionListener.recordApplicationEvents' -> false]]
2023-09-26 13:14:58.487  INFO 7107 --- [           main] o.s.t.c.transaction.TransactionContext   : Began transaction (1) for test context [DefaultTestContext@294aba23 testClass = CicdAssignmentApplicationTests, testInstance = com.example.cicdassignment.CicdAssignmentApplicationTests@72c5064f, testMethod = testAddRoute@CicdAssignmentApplicationTests, testException = [null], mergedContextConfiguration = [WebMergedContextConfiguration@69909c14 testClass = CicdAssignmentApplicationTests, locations = '{}', classes = '{class com.example.cicdassignment.CicdAssignmentApplication}', contextInitializerClasses = '[]', activeProfiles = '{}', propertySourceLocations = '{}', propertySourceProperties = '{org.springframework.boot.test.context.SpringBootTestContextBootstrapper=true}', contextCustomizers = set[org.springframework.boot.test.context.filter.ExcludeFilterContextCustomizer@6f6c6077, org.springframework.boot.test.json.DuplicateJsonObjectContextCustomizerFactory$DuplicateJsonObjectContextCustomizer@58606c91, org.springframework.boot.test.mock.mockito.MockitoContextCustomizer@0, org.springframework.boot.test.web.client.TestRestTemplateContextCustomizer@66deec87, org.springframework.boot.test.autoconfigure.actuate.metrics.MetricsExportContextCustomizerFactory$DisableMetricExportContextCustomizer@2721044, org.springframework.boot.test.autoconfigure.properties.PropertyMappingContextCustomizer@0, org.springframework.boot.test.autoconfigure.web.servlet.WebDriverContextCustomizerFactory$Customizer@2b7e8044, org.springframework.boot.test.context.SpringBootTestArgs@1, org.springframework.boot.test.context.SpringBootTestWebEnvironment@7dc222ae], resourceBasePath = 'src/main/webapp', contextLoader = 'org.springframework.boot.test.context.SpringBootContextLoader', parent = [null]], attributes = map['org.springframework.test.context.web.ServletTestExecutionListener.activateListener' -> true, 'org.springframework.test.context.web.ServletTestExecutionListener.populatedRequestContextHolder' -> true, 'org.springframework.test.context.web.ServletTestExecutionListener.resetRequestContextHolder' -> true, 'org.springframework.test.context.event.ApplicationEventsTestExecutionListener.recordApplicationEvents' -> false]]; transaction manager [org.springframework.jdbc.support.JdbcTransactionManager@28fef9a2]; rollback [true]
2023-09-26 13:14:58.523  INFO 7107 --- [           main] o.s.t.c.transaction.TransactionContext   : Rolled back transaction for test: [DefaultTestContext@294aba23 testClass = CicdAssignmentApplicationTests, testInstance = com.example.cicdassignment.CicdAssignmentApplicationTests@72c5064f, testMethod = testAddRoute@CicdAssignmentApplicationTests, testException = [null], mergedContextConfiguration = [WebMergedContextConfiguration@69909c14 testClass = CicdAssignmentApplicationTests, locations = '{}', classes = '{class com.example.cicdassignment.CicdAssignmentApplication}', contextInitializerClasses = '[]', activeProfiles = '{}', propertySourceLocations = '{}', propertySourceProperties = '{org.springframework.boot.test.context.SpringBootTestContextBootstrapper=true}', contextCustomizers = set[org.springframework.boot.test.context.filter.ExcludeFilterContextCustomizer@6f6c6077, org.springframework.boot.test.json.DuplicateJsonObjectContextCustomizerFactory$DuplicateJsonObjectContextCustomizer@58606c91, org.springframework.boot.test.mock.mockito.MockitoContextCustomizer@0, org.springframework.boot.test.web.client.TestRestTemplateContextCustomizer@66deec87, org.springframework.boot.test.autoconfigure.actuate.metrics.MetricsExportContextCustomizerFactory$DisableMetricExportContextCustomizer@2721044, org.springframework.boot.test.autoconfigure.properties.PropertyMappingContextCustomizer@0, org.springframework.boot.test.autoconfigure.web.servlet.WebDriverContextCustomizerFactory$Customizer@2b7e8044, org.springframework.boot.test.context.SpringBootTestArgs@1, org.springframework.boot.test.context.SpringBootTestWebEnvironment@7dc222ae], resourceBasePath = 'src/main/webapp', contextLoader = 'org.springframework.boot.test.context.SpringBootContextLoader', parent = [null]], attributes = map['org.springframework.test.context.web.ServletTestExecutionListener.activateListener' -> true, 'org.springframework.test.context.web.ServletTestExecutionListener.populatedRequestContextHolder' -> true, 'org.springframework.test.context.web.ServletTestExecutionListener.resetRequestContextHolder' -> true, 'org.springframework.test.context.event.ApplicationEventsTestExecutionListener.recordApplicationEvents' -> false]]
2023-09-26 13:14:58.527  INFO 7107 --- [           main] o.s.t.c.transaction.TransactionContext   : Began transaction (1) for test context [DefaultTestContext@294aba23 testClass = CicdAssignmentApplicationTests, testInstance = com.example.cicdassignment.CicdAssignmentApplicationTests@43dddfdd, testMethod = testAddAccount@CicdAssignmentApplicationTests, testException = [null], mergedContextConfiguration = [WebMergedContextConfiguration@69909c14 testClass = CicdAssignmentApplicationTests, locations = '{}', classes = '{class com.example.cicdassignment.CicdAssignmentApplication}', contextInitializerClasses = '[]', activeProfiles = '{}', propertySourceLocations = '{}', propertySourceProperties = '{org.springframework.boot.test.context.SpringBootTestContextBootstrapper=true}', contextCustomizers = set[org.springframework.boot.test.context.filter.ExcludeFilterContextCustomizer@6f6c6077, org.springframework.boot.test.json.DuplicateJsonObjectContextCustomizerFactory$DuplicateJsonObjectContextCustomizer@58606c91, org.springframework.boot.test.mock.mockito.MockitoContextCustomizer@0, org.springframework.boot.test.web.client.TestRestTemplateContextCustomizer@66deec87, org.springframework.boot.test.autoconfigure.actuate.metrics.MetricsExportContextCustomizerFactory$DisableMetricExportContextCustomizer@2721044, org.springframework.boot.test.autoconfigure.properties.PropertyMappingContextCustomizer@0, org.springframework.boot.test.autoconfigure.web.servlet.WebDriverContextCustomizerFactory$Customizer@2b7e8044, org.springframework.boot.test.context.SpringBootTestArgs@1, org.springframework.boot.test.context.SpringBootTestWebEnvironment@7dc222ae], resourceBasePath = 'src/main/webapp', contextLoader = 'org.springframework.boot.test.context.SpringBootContextLoader', parent = [null]], attributes = map['org.springframework.test.context.web.ServletTestExecutionListener.activateListener' -> true, 'org.springframework.test.context.web.ServletTestExecutionListener.populatedRequestContextHolder' -> true, 'org.springframework.test.context.web.ServletTestExecutionListener.resetRequestContextHolder' -> true, 'org.springframework.test.context.event.ApplicationEventsTestExecutionListener.recordApplicationEvents' -> false]]; transaction manager [org.springframework.jdbc.support.JdbcTransactionManager@28fef9a2]; rollback [true]
2023-09-26 13:14:58.547  INFO 7107 --- [           main] o.s.t.c.transaction.TransactionContext   : Rolled back transaction for test: [DefaultTestContext@294aba23 testClass = CicdAssignmentApplicationTests, testInstance = com.example.cicdassignment.CicdAssignmentApplicationTests@43dddfdd, testMethod = testAddAccount@CicdAssignmentApplicationTests, testException = [null], mergedContextConfiguration = [WebMergedContextConfiguration@69909c14 testClass = CicdAssignmentApplicationTests, locations = '{}', classes = '{class com.example.cicdassignment.CicdAssignmentApplication}', contextInitializerClasses = '[]', activeProfiles = '{}', propertySourceLocations = '{}', propertySourceProperties = '{org.springframework.boot.test.context.SpringBootTestContextBootstrapper=true}', contextCustomizers = set[org.springframework.boot.test.context.filter.ExcludeFilterContextCustomizer@6f6c6077, org.springframework.boot.test.json.DuplicateJsonObjectContextCustomizerFactory$DuplicateJsonObjectContextCustomizer@58606c91, org.springframework.boot.test.mock.mockito.MockitoContextCustomizer@0, org.springframework.boot.test.web.client.TestRestTemplateContextCustomizer@66deec87, org.springframework.boot.test.autoconfigure.actuate.metrics.MetricsExportContextCustomizerFactory$DisableMetricExportContextCustomizer@2721044, org.springframework.boot.test.autoconfigure.properties.PropertyMappingContextCustomizer@0, org.springframework.boot.test.autoconfigure.web.servlet.WebDriverContextCustomizerFactory$Customizer@2b7e8044, org.springframework.boot.test.context.SpringBootTestArgs@1, org.springframework.boot.test.context.SpringBootTestWebEnvironment@7dc222ae], resourceBasePath = 'src/main/webapp', contextLoader = 'org.springframework.boot.test.context.SpringBootContextLoader', parent = [null]], attributes = map['org.springframework.test.context.web.ServletTestExecutionListener.activateListener' -> true, 'org.springframework.test.context.web.ServletTestExecutionListener.populatedRequestContextHolder' -> true, 'org.springframework.test.context.web.ServletTestExecutionListener.resetRequestContextHolder' -> true, 'org.springframework.test.context.event.ApplicationEventsTestExecutionListener.recordApplicationEvents' -> false]]
2023-09-26 13:14:58.552  INFO 7107 --- [           main] o.s.t.c.transaction.TransactionContext   : Began transaction (1) for test context [DefaultTestContext@294aba23 testClass = CicdAssignmentApplicationTests, testInstance = com.example.cicdassignment.CicdAssignmentApplicationTests@7a13ad55, testMethod = testDeleteAccount@CicdAssignmentApplicationTests, testException = [null], mergedContextConfiguration = [WebMergedContextConfiguration@69909c14 testClass = CicdAssignmentApplicationTests, locations = '{}', classes = '{class com.example.cicdassignment.CicdAssignmentApplication}', contextInitializerClasses = '[]', activeProfiles = '{}', propertySourceLocations = '{}', propertySourceProperties = '{org.springframework.boot.test.context.SpringBootTestContextBootstrapper=true}', contextCustomizers = set[org.springframework.boot.test.context.filter.ExcludeFilterContextCustomizer@6f6c6077, org.springframework.boot.test.json.DuplicateJsonObjectContextCustomizerFactory$DuplicateJsonObjectContextCustomizer@58606c91, org.springframework.boot.test.mock.mockito.MockitoContextCustomizer@0, org.springframework.boot.test.web.client.TestRestTemplateContextCustomizer@66deec87, org.springframework.boot.test.autoconfigure.actuate.metrics.MetricsExportContextCustomizerFactory$DisableMetricExportContextCustomizer@2721044, org.springframework.boot.test.autoconfigure.properties.PropertyMappingContextCustomizer@0, org.springframework.boot.test.autoconfigure.web.servlet.WebDriverContextCustomizerFactory$Customizer@2b7e8044, org.springframework.boot.test.context.SpringBootTestArgs@1, org.springframework.boot.test.context.SpringBootTestWebEnvironment@7dc222ae], resourceBasePath = 'src/main/webapp', contextLoader = 'org.springframework.boot.test.context.SpringBootContextLoader', parent = [null]], attributes = map['org.springframework.test.context.web.ServletTestExecutionListener.activateListener' -> true, 'org.springframework.test.context.web.ServletTestExecutionListener.populatedRequestContextHolder' -> true, 'org.springframework.test.context.web.ServletTestExecutionListener.resetRequestContextHolder' -> true, 'org.springframework.test.context.event.ApplicationEventsTestExecutionListener.recordApplicationEvents' -> false]]; transaction manager [org.springframework.jdbc.support.JdbcTransactionManager@28fef9a2]; rollback [true]
2023-09-26 13:14:58.562  INFO 7107 --- [           main] o.s.t.c.transaction.TransactionContext   : Rolled back transaction for test: [DefaultTestContext@294aba23 testClass = CicdAssignmentApplicationTests, testInstance = com.example.cicdassignment.CicdAssignmentApplicationTests@7a13ad55, testMethod = testDeleteAccount@CicdAssignmentApplicationTests, testException = [null], mergedContextConfiguration = [WebMergedContextConfiguration@69909c14 testClass = CicdAssignmentApplicationTests, locations = '{}', classes = '{class com.example.cicdassignment.CicdAssignmentApplication}', contextInitializerClasses = '[]', activeProfiles = '{}', propertySourceLocations = '{}', propertySourceProperties = '{org.springframework.boot.test.context.SpringBootTestContextBootstrapper=true}', contextCustomizers = set[org.springframework.boot.test.context.filter.ExcludeFilterContextCustomizer@6f6c6077, org.springframework.boot.test.json.DuplicateJsonObjectContextCustomizerFactory$DuplicateJsonObjectContextCustomizer@58606c91, org.springframework.boot.test.mock.mockito.MockitoContextCustomizer@0, org.springframework.boot.test.web.client.TestRestTemplateContextCustomizer@66deec87, org.springframework.boot.test.autoconfigure.actuate.metrics.MetricsExportContextCustomizerFactory$DisableMetricExportContextCustomizer@2721044, org.springframework.boot.test.autoconfigure.properties.PropertyMappingContextCustomizer@0, org.springframework.boot.test.autoconfigure.web.servlet.WebDriverContextCustomizerFactory$Customizer@2b7e8044, org.springframework.boot.test.context.SpringBootTestArgs@1, org.springframework.boot.test.context.SpringBootTestWebEnvironment@7dc222ae], resourceBasePath = 'src/main/webapp', contextLoader = 'org.springframework.boot.test.context.SpringBootContextLoader', parent = [null]], attributes = map['org.springframework.test.context.web.ServletTestExecutionListener.activateListener' -> true, 'org.springframework.test.context.web.ServletTestExecutionListener.populatedRequestContextHolder' -> true, 'org.springframework.test.context.web.ServletTestExecutionListener.resetRequestContextHolder' -> true, 'org.springframework.test.context.event.ApplicationEventsTestExecutionListener.recordApplicationEvents' -> false]]
2023-09-26 13:14:58.571  INFO 7107 --- [           main] o.s.t.c.transaction.TransactionContext   : Began transaction (1) for test context [DefaultTestContext@294aba23 testClass = CicdAssignmentApplicationTests, testInstance = com.example.cicdassignment.CicdAssignmentApplicationTests@6dbdeb69, testMethod = testChangeBooking@CicdAssignmentApplicationTests, testException = [null], mergedContextConfiguration = [WebMergedContextConfiguration@69909c14 testClass = CicdAssignmentApplicationTests, locations = '{}', classes = '{class com.example.cicdassignment.CicdAssignmentApplication}', contextInitializerClasses = '[]', activeProfiles = '{}', propertySourceLocations = '{}', propertySourceProperties = '{org.springframework.boot.test.context.SpringBootTestContextBootstrapper=true}', contextCustomizers = set[org.springframework.boot.test.context.filter.ExcludeFilterContextCustomizer@6f6c6077, org.springframework.boot.test.json.DuplicateJsonObjectContextCustomizerFactory$DuplicateJsonObjectContextCustomizer@58606c91, org.springframework.boot.test.mock.mockito.MockitoContextCustomizer@0, org.springframework.boot.test.web.client.TestRestTemplateContextCustomizer@66deec87, org.springframework.boot.test.autoconfigure.actuate.metrics.MetricsExportContextCustomizerFactory$DisableMetricExportContextCustomizer@2721044, org.springframework.boot.test.autoconfigure.properties.PropertyMappingContextCustomizer@0, org.springframework.boot.test.autoconfigure.web.servlet.WebDriverContextCustomizerFactory$Customizer@2b7e8044, org.springframework.boot.test.context.SpringBootTestArgs@1, org.springframework.boot.test.context.SpringBootTestWebEnvironment@7dc222ae], resourceBasePath = 'src/main/webapp', contextLoader = 'org.springframework.boot.test.context.SpringBootContextLoader', parent = [null]], attributes = map['org.springframework.test.context.web.ServletTestExecutionListener.activateListener' -> true, 'org.springframework.test.context.web.ServletTestExecutionListener.populatedRequestContextHolder' -> true, 'org.springframework.test.context.web.ServletTestExecutionListener.resetRequestContextHolder' -> true, 'org.springframework.test.context.event.ApplicationEventsTestExecutionListener.recordApplicationEvents' -> false]]; transaction manager [org.springframework.jdbc.support.JdbcTransactionManager@28fef9a2]; rollback [true]
2023-09-26 13:14:58.585  INFO 7107 --- [           main] o.s.t.c.transaction.TransactionContext   : Rolled back transaction for test: [DefaultTestContext@294aba23 testClass = CicdAssignmentApplicationTests, testInstance = com.example.cicdassignment.CicdAssignmentApplicationTests@6dbdeb69, testMethod = testChangeBooking@CicdAssignmentApplicationTests, testException = [null], mergedContextConfiguration = [WebMergedContextConfiguration@69909c14 testClass = CicdAssignmentApplicationTests, locations = '{}', classes = '{class com.example.cicdassignment.CicdAssignmentApplication}', contextInitializerClasses = '[]', activeProfiles = '{}', propertySourceLocations = '{}', propertySourceProperties = '{org.springframework.boot.test.context.SpringBootTestContextBootstrapper=true}', contextCustomizers = set[org.springframework.boot.test.context.filter.ExcludeFilterContextCustomizer@6f6c6077, org.springframework.boot.test.json.DuplicateJsonObjectContextCustomizerFactory$DuplicateJsonObjectContextCustomizer@58606c91, org.springframework.boot.test.mock.mockito.MockitoContextCustomizer@0, org.springframework.boot.test.web.client.TestRestTemplateContextCustomizer@66deec87, org.springframework.boot.test.autoconfigure.actuate.metrics.MetricsExportContextCustomizerFactory$DisableMetricExportContextCustomizer@2721044, org.springframework.boot.test.autoconfigure.properties.PropertyMappingContextCustomizer@0, org.springframework.boot.test.autoconfigure.web.servlet.WebDriverContextCustomizerFactory$Customizer@2b7e8044, org.springframework.boot.test.context.SpringBootTestArgs@1, org.springframework.boot.test.context.SpringBootTestWebEnvironment@7dc222ae], resourceBasePath = 'src/main/webapp', contextLoader = 'org.springframework.boot.test.context.SpringBootContextLoader', parent = [null]], attributes = map['org.springframework.test.context.web.ServletTestExecutionListener.activateListener' -> true, 'org.springframework.test.context.web.ServletTestExecutionListener.populatedRequestContextHolder' -> true, 'org.springframework.test.context.web.ServletTestExecutionListener.resetRequestContextHolder' -> true, 'org.springframework.test.context.event.ApplicationEventsTestExecutionListener.recordApplicationEvents' -> false]]
[JAccount(id=1, name=niuniu, contact=1234, payment=credit card, history=3000, booking=1, type=user, routeId=null, departure=malmo, arrival=goteborg, vehicle=train, expectDeparture=malmo c, expectArrival=goteborg c, price=500, deliver=sl, promotion=), JAccount(id=2, name=tiger, contact=4567, payment=swish, history=0, booking=1, type=admin, routeId=null, departure=malmo, arrival=goteborg, vehicle=train, expectDeparture=malmo c, expectArrival=goteborg c, price=500, deliver=sl, promotion=), JAccount(id=3, name=panda, contact=6789, payment=swish, history=2000, booking=0, type=delivery, routeId=null, departure=null, arrival=null, vehicle=null, expectDeparture=null, expectArrival=null, price=null, deliver=null, promotion=null), JAccount(id=4, name=deer, contact=9807, payment=credit card, history=0, booking=2, type=delivery, routeId=null, departure=malmo, arrival=stockholm, vehicle=car, expectDeparture=malmo c, expectArrival=stockholm c, price=1000, deliver=sj, promotion=), JAccount(id=5, name=testName, contact=1111, payment=swish, history=0, booking=0, type=user, routeId=null, departure=null, arrival=null, vehicle=null, expectDeparture=null, expectArrival=null, price=null, deliver=null, promotion=null), JAccount(id=7, name=testName, contact=1111, payment=swish, history=0, booking=2, type=user, routeId=null, departure=malmo, arrival=stockholm, vehicle=car, expectDeparture=malmo c, expectArrival=stockholm c, price=1000, deliver=sj, promotion=), JAccount(id=8, name=testName, contact=1111, payment=swish, history=0, booking=2, type=user, routeId=null, departure=malmo, arrival=stockholm, vehicle=car, expectDeparture=malmo c, expectArrival=stockholm c, price=1000, deliver=sj, promotion=), JAccount(id=9, name=testName2, contact=2222, payment=swish2, history=0, booking=0, type=admin, routeId=null, departure=null, arrival=null, vehicle=null, expectDeparture=null, expectArrival=null, price=null, deliver=null, promotion=null), JAccount(id=10, name=testName, contact=1111, payment=swish, history=0, booking=2, type=user, routeId=null, departure=malmo, arrival=stockholm, vehicle=car, expectDeparture=malmo c, expectArrival=stockholm c, price=1000, deliver=sj, promotion=), JAccount(id=11, name=testName, contact=1111, payment=swish, history=0, booking=2, type=user, routeId=null, departure=malmo, arrival=stockholm, vehicle=car, expectDeparture=malmo c, expectArrival=stockholm c, price=1000, deliver=sj, promotion=), JAccount(id=12, name=testName, contact=1111, payment=swish, history=0, booking=2, type=user, routeId=null, departure=malmo, arrival=stockholm, vehicle=car, expectDeparture=malmo c, expectArrival=stockholm c, price=1000, deliver=sj, promotion=), JAccount(id=13, name=testName, contact=1111, payment=swish, history=0, booking=2, type=user, routeId=null, departure=malmo, arrival=stockholm, vehicle=car, expectDeparture=malmo c, expectArrival=stockholm c, price=1000, deliver=sj, promotion=), JAccount(id=15, name=testName, contact=1111, payment=swish, history=0, booking=2, type=user, routeId=null, departure=malmo, arrival=stockholm, vehicle=car, expectDeparture=malmo c, expectArrival=stockholm c, price=1000, deliver=sj, promotion=), JAccount(id=17, name=miaomiao, contact=1234, payment=credit card, history=0, booking=0, type=user, routeId=null, departure=null, arrival=null, vehicle=null, expectDeparture=null, expectArrival=null, price=null, deliver=null, promotion=null), JAccount(id=18, name=cat, contact=0000, payment=credit card, history=0, booking=0, type=admin, routeId=null, departure=null, arrival=null, vehicle=null, expectDeparture=null, expectArrival=null, price=null, deliver=null, promotion=null)]
2023-09-26 13:14:58.616  INFO 7107 --- [           main] o.s.t.c.transaction.TransactionContext   : Began transaction (1) for test context [DefaultTestContext@294aba23 testClass = CicdAssignmentApplicationTests, testInstance = com.example.cicdassignment.CicdAssignmentApplicationTests@544300a6, testMethod = testupdateRoute@CicdAssignmentApplicationTests, testException = [null], mergedContextConfiguration = [WebMergedContextConfiguration@69909c14 testClass = CicdAssignmentApplicationTests, locations = '{}', classes = '{class com.example.cicdassignment.CicdAssignmentApplication}', contextInitializerClasses = '[]', activeProfiles = '{}', propertySourceLocations = '{}', propertySourceProperties = '{org.springframework.boot.test.context.SpringBootTestContextBootstrapper=true}', contextCustomizers = set[org.springframework.boot.test.context.filter.ExcludeFilterContextCustomizer@6f6c6077, org.springframework.boot.test.json.DuplicateJsonObjectContextCustomizerFactory$DuplicateJsonObjectContextCustomizer@58606c91, org.springframework.boot.test.mock.mockito.MockitoContextCustomizer@0, org.springframework.boot.test.web.client.TestRestTemplateContextCustomizer@66deec87, org.springframework.boot.test.autoconfigure.actuate.metrics.MetricsExportContextCustomizerFactory$DisableMetricExportContextCustomizer@2721044, org.springframework.boot.test.autoconfigure.properties.PropertyMappingContextCustomizer@0, org.springframework.boot.test.autoconfigure.web.servlet.WebDriverContextCustomizerFactory$Customizer@2b7e8044, org.springframework.boot.test.context.SpringBootTestArgs@1, org.springframework.boot.test.context.SpringBootTestWebEnvironment@7dc222ae], resourceBasePath = 'src/main/webapp', contextLoader = 'org.springframework.boot.test.context.SpringBootContextLoader', parent = [null]], attributes = map['org.springframework.test.context.web.ServletTestExecutionListener.activateListener' -> true, 'org.springframework.test.context.web.ServletTestExecutionListener.populatedRequestContextHolder' -> true, 'org.springframework.test.context.web.ServletTestExecutionListener.resetRequestContextHolder' -> true, 'org.springframework.test.context.event.ApplicationEventsTestExecutionListener.recordApplicationEvents' -> false]]; transaction manager [org.springframework.jdbc.support.JdbcTransactionManager@28fef9a2]; rollback [true]
2023-09-26 13:14:58.627  INFO 7107 --- [           main] o.s.t.c.transaction.TransactionContext   : Rolled back transaction for test: [DefaultTestContext@294aba23 testClass = CicdAssignmentApplicationTests, testInstance = com.example.cicdassignment.CicdAssignmentApplicationTests@544300a6, testMethod = testupdateRoute@CicdAssignmentApplicationTests, testException = [null], mergedContextConfiguration = [WebMergedContextConfiguration@69909c14 testClass = CicdAssignmentApplicationTests, locations = '{}', classes = '{class com.example.cicdassignment.CicdAssignmentApplication}', contextInitializerClasses = '[]', activeProfiles = '{}', propertySourceLocations = '{}', propertySourceProperties = '{org.springframework.boot.test.context.SpringBootTestContextBootstrapper=true}', contextCustomizers = set[org.springframework.boot.test.context.filter.ExcludeFilterContextCustomizer@6f6c6077, org.springframework.boot.test.json.DuplicateJsonObjectContextCustomizerFactory$DuplicateJsonObjectContextCustomizer@58606c91, org.springframework.boot.test.mock.mockito.MockitoContextCustomizer@0, org.springframework.boot.test.web.client.TestRestTemplateContextCustomizer@66deec87, org.springframework.boot.test.autoconfigure.actuate.metrics.MetricsExportContextCustomizerFactory$DisableMetricExportContextCustomizer@2721044, org.springframework.boot.test.autoconfigure.properties.PropertyMappingContextCustomizer@0, org.springframework.boot.test.autoconfigure.web.servlet.WebDriverContextCustomizerFactory$Customizer@2b7e8044, org.springframework.boot.test.context.SpringBootTestArgs@1, org.springframework.boot.test.context.SpringBootTestWebEnvironment@7dc222ae], resourceBasePath = 'src/main/webapp', contextLoader = 'org.springframework.boot.test.context.SpringBootContextLoader', parent = [null]], attributes = map['org.springframework.test.context.web.ServletTestExecutionListener.activateListener' -> true, 'org.springframework.test.context.web.ServletTestExecutionListener.populatedRequestContextHolder' -> true, 'org.springframework.test.context.web.ServletTestExecutionListener.resetRequestContextHolder' -> true, 'org.springframework.test.context.event.ApplicationEventsTestExecutionListener.recordApplicationEvents' -> false]]
[Route(id=1, departure=malmo, arrival=goteborg, vehicle=train, expectDeparture=malmo c, expectArrival=goteborg c, price=500, deliver=sl, promotion=), Route(id=2, departure=malmo, arrival=stockholm, vehicle=car, expectDeparture=malmo c, expectArrival=stockholm c, price=1000, deliver=sj, promotion=), Route(id=3, departure=malmo, arrival=stockholm, vehicle=train, expectDeparture=malmo c, expectArrival=stockholm c, price=600, deliver=sj, promotion=), Route(id=4, departure=stockholm, arrival=goteborg, vehicle=train, expectDeparture=stockholm c, expectArrival=stockholm c, price=300, deliver=sl, promotion=), Route(id=5, departure=stockholm, arrival=goteborg, vehicle=train, expectDeparture=stockholm c, expectArrival=stockholm c, price=300, deliver=sl, promotion=), Route(id=6, departure=stockholm, arrival=goteborg, vehicle=train, expectDeparture=stockholm c, expectArrival=stockholm c, price=300, deliver=sl, promotion=), Route(id=7, departure=stockholm, arrival=goteborg, vehicle=train, expectDeparture=stockholm c, expectArrival=stockholm c, price=300, deliver=sl, promotion=), Route(id=8, departure=stockholm, arrival=goteborg, vehicle=train, expectDeparture=stockholm c, expectArrival=goteborg c, price=550, deliver=sj, promotion=), Route(id=9, departure=stockholm, arrival=goteborg, vehicle=train, expectDeparture=stockholm c, expectArrival=goteborg c, price=650, deliver=sl, promotion=)]
Account(id=1, name=niuniu, contact=1234, payment=credit card, history=3000, booking=1, type=user)
[[1;34mINFO[m] [1;32mTests run: [0;1;32m12[m, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 6.435 s - in com.example.cicdassignment.[1mCicdAssignmentApplicationTests[m
2023-09-26 13:14:58.704  INFO 7107 --- [ionShutdownHook] com.alibaba.druid.pool.DruidDataSource   : {dataSource-1} closing ...
2023-09-26 13:14:58.715  INFO 7107 --- [ionShutdownHook] com.alibaba.druid.pool.DruidDataSource   : {dataSource-1} closed
[[1;34mINFO[m] 
[[1;34mINFO[m] Results:
[[1;34mINFO[m] 
[[1;34mINFO[m] [1;32mTests run: 36, Failures: 0, Errors: 0, Skipped: 0[m
[[1;34mINFO[m] 
[[1;34mINFO[m] 
[[1;34mINFO[m] [1m--- [0;32mmaven-jar-plugin:3.2.2:jar[m [1m(default-jar)[m @ [36mCICDAssignment[0;1m ---[m
[[1;34mINFO[m] Building jar: /var/lib/jenkins/workspace/multipleScan_master/target/CICDAssignment-0.0.1-SNAPSHOT.jar
[[1;34mINFO[m] 
[[1;34mINFO[m] [1m--- [0;32mspring-boot-maven-plugin:2.7.15:repackage[m [1m(repackage)[m @ [36mCICDAssignment[0;1m ---[m
[[1;34mINFO[m] Replacing main artifact with repackaged archive
[[1;34mINFO[m] [1m------------------------------------------------------------------------[m
[[1;34mINFO[m] [1;32mBUILD SUCCESS[m
[[1;34mINFO[m] [1m------------------------------------------------------------------------[m
[[1;34mINFO[m] Total time:  19.813 s
[[1;34mINFO[m] Finished at: 2023-09-26T13:15:00+02:00
[[1;34mINFO[m] [1m------------------------------------------------------------------------[m
[Pipeline] }
[Pipeline] // withEnv
[Pipeline] }
[Pipeline] // stage
[Pipeline] stage
[Pipeline] { (remove old docker container)
[Pipeline] tool
[Pipeline] envVarsForTool
[Pipeline] withEnv
[Pipeline] {
[Pipeline] sshPublisher
SSH: Connecting from host [ubuntu1]
SSH: Connecting with configuration [testServer] ...
SSH: EXEC: completed after 2,203 ms
SSH: Disconnecting configuration [testServer] ...
SSH: Transferred 0 file(s)
[Pipeline] }
[Pipeline] // withEnv
[Pipeline] }
[Pipeline] // stage
[Pipeline] stage
[Pipeline] { (SSH send )
[Pipeline] tool
[Pipeline] envVarsForTool
[Pipeline] withEnv
[Pipeline] {
[Pipeline] sshPublisher
SSH: Connecting from host [ubuntu1]
SSH: Connecting with configuration [testServer] ...
SSH: Disconnecting configuration [testServer] ...
SSH: Transferred 2 ( 1 + 1 ) file(s)
[Pipeline] }
[Pipeline] // withEnv
[Pipeline] }
[Pipeline] // stage
[Pipeline] stage
[Pipeline] { (run docker )
[Pipeline] tool
[Pipeline] envVarsForTool
[Pipeline] withEnv
[Pipeline] {
[Pipeline] sshPublisher
SSH: Connecting from host [ubuntu1]
SSH: Connecting with configuration [testServer] ...
SSH: EXEC: completed after 14,212 ms
SSH: Disconnecting configuration [testServer] ...
SSH: Transferred 0 file(s)
[Pipeline] }
[Pipeline] // withEnv
[Pipeline] }
[Pipeline] // stage
[Pipeline] }
[Pipeline] // withEnv
[Pipeline] }
[Pipeline] // withEnv
[Pipeline] }
[Pipeline] // node
[Pipeline] End of Pipeline

Could not update commit status, please check if your scan credentials belong to a member of the organization or a collaborator of the repository and repo:status scope is selected


GitHub has been notified of this commit’s build result

Finished: SUCCESS

```