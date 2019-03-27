#!/bin/bash
SCRIPT_DIR=$(dirname $(readlink -f $0))
java -cp "$SCRIPT_DIR/lib/*" org.codingmatters.value.objects.js.Main $@