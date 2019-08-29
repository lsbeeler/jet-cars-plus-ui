#!/bin/bash

source setenv.sh

ps -e | grep 'jet-cars-plus-ui' | grep java | awk '{ print $1 }' | xargs kill -9
