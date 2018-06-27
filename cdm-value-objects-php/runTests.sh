#!/usr/bin/env sh

LOCAL_DIR=$(pwd)

docker run --rm --interactive --tty --volume $PWD/target/test-classes:/app flexio/flexio-docker-web bash -c "cd /app; composer install; ./vendor/bin/phpunit test/BookTest.php"