#!/bin/bash

export ACCESS_TOKEN_SECRET=Rqsomqo7MYi7mHARqq79mcSx1qbODBDEYWYYAinnWO8
echo $ACCESS_TOKEN_SECRET
clojure -X:build build-client
clojure -X:build uberjar :jar-name "app.jar" :verbose true
java -jar app.jar