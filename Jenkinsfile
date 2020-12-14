#!/usr/bin/env groovy
@Library('jenkinsPipelineLib') _
env.SERVICE_NAME = "int-zipchat"
env.ENV_NAME = "dev-envs-foundryint"
env.AUTODEPLOY_BRANCH = "development"
env.DOCKER_LABEL = "unset"
env.REPO_URL = 'https://gitlab.zwdev.io/Integration/int-zipchat.git'
env.NAMESPACE = "application-int"

pipelineJava(
        agent: "builder",
        runPreValidate: true,
        runBuild: true,
        runUnittests: true,
        runIntegrationtests: true,
        runDeployArtifactory: true,
        runDeployDocker: true,
        autoDeployService: true,
        debug: true
)

