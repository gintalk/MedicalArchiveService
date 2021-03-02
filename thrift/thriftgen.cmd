#!/bin/bash

thrift -r --gen java service.thrift

cp gen-java/* ../src/ -rf
rm gen-java -rf