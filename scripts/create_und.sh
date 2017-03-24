#!/usr/bin/env bash

UDB_FILE=$1.udb

$SCITOOLS_HOME/und create -db $UDB_FILE -languages java c++
$SCITOOLS_HOME/und -db $UDB_FILE add $2
$SCITOOLS_HOME/und -db $UDB_FILE analyze
