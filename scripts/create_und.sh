#!/usr/bin/env bash

wget https://github.com/wrpinheiro/spring-jpa-embedded-db/archive/master.zip
unzip master.zip
und create -db spring-jpa-embedded-db-master.udb -languages java c++
und -db ./spring-jpa-embedded-db-master.udb add ./spring-jpa-embedded-db-master
und -db spring-jpa-embedded-db-master.udb analyze
