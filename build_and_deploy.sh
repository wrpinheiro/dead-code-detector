#!/usr/bin/env bash

export APP_VERSION=$(grep "project\.version" build.gradle | sed -E "s/(.*) = '(.*)'/\2/")

./gradlew build
#export DOCKER_HOST="tcp://build.swarm.devfactory.com"
docker build -t wpinheiro_dead_code_detection_service .

# test your image before send to remote registry
docker run -it -p 8080:8080  wpinheiro_dead_code_detection_service

docker tag  wpinheiro_dead_code_detection_service registry2.swarm.devfactory.com/devfactory/wpinheiro_dead_code_detection_service:$APP_VERSION
docker push registry2.swarm.devfactory.com/devfactory/wpinheiro_dead_code_detection_service:$APP_VERSION

export DOCKER_HOST="tcp://webserver.devfactory.com"
docker service ps wpinheiro_dead_code_detection_service
docker service rm wpinheiro_dead_code_detection_service
docker service create -p 17951:8080 --name wpinheiro_dead_code_detection_service --replicas 1 --limit-memory 1g registry2.swarm.devfactory.com/devfactory/wpinheiro_dead_code_detection_service:$APP_VERSION
docker service ps wpinheiro_dead_code_detection_service
