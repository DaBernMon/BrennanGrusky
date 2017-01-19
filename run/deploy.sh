#!/bin/bash

if [[ ! -z "$1" ]] && [ $1 = "dbrun" ]; then
	ant DBInit -f ../build.xml
elif [[ ! -z "$1" ]] && [ $1 = "db" ]; then
	ant DBInit -f ../build.xml
	exit 0
fi

ant war -f ../build.xml
rm -rf ../run/brennangrusky.war
cp ../brennangrusky.war ../run/brennangrusky.war
rm -rf ../brennangrusky.war

heroku login
heroku deploy:war --war ../run/brennangrusky.war --app brennangrusky -r 8.0.24.0
