#!/usr/bin/env bash
if [ ! -f "bin/campus/Main.class" ]; then
    ./compile.sh
fi
java -cp bin campus.Main "$@"