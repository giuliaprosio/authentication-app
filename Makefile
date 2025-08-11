.PHONY: up stop down build it mysql

up: build
	docker-compose up --build

stop:
	docker-compose stop

down:
	docker-compose down -v

build:
	mvnw.cmd clean package -DskipTests

it: up
	mvnw.cmd verify -DskipUnitTests -Pintegration-test

mysql:
	docker-compose up mysql