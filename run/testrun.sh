#!/bin/bash

ant -f ../build.xml war
java -jar webapp-runner-8.0.24.0.jar ../brennangrusky.war