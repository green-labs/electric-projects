# electric-project

```
$ yarn
$ clj -A:dev -X user/main

Starting Electric compiler and server...
shadow-cljs - server version: 2.20.1 running at http://localhost:9630
shadow-cljs - nREPL server started on port 9001
[:app] Configuring build.
[:app] Compiling ...
[:app] Build completed. (224 files, 0 compiled, 0 warnings, 1.93s)

👉 App server available at http://0.0.0.0:8080
```

# Deployment

```
$ export ACCESS_TOKEN_SECRET=...
$ yarn
$ clojure -X:build build-client          # optimized release build
$ clojure -X:build uberjar               # contains demos and demo server, currently
```

## Docker
```
$ docker build --build-arg VERSION=$(git describe --tags --long --always --dirty) -t electric-project .
$ docker run  -e ACCESS_TOKEN_SECRET=${ACCESS_TOKEN_SECRET} --rm -p 8080:8080 electric-project
```
