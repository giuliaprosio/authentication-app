## Authentication Backend Application 
An application to connect a web page with login or creation of a new account. 
The data is set up to be connected to a MySQL database. 

Once logged in, the communication uses JWT tokens. The 
user can connect to their Spotify account and review
cool analysis of their listening patterns!

To host it locally, after cloning the repo go in

>./src/main/resources/

And create a new file application.properties. 

In the file, write the following code. In the subsequent steps I will detail how
to fill out the various env vars.  
```
spring.application.name=<app_name>

# DataSource Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/<db_name>?serverTimezone=UTC
# for docker: replace localhost:3306 to mysql
spring.datasource.username=<username>
spring.datasource.password=<password>
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# Logging
logging.level.org.springframework.security=DEBUG

spring.devtools.restart.enabled=false
server.port=9090

# JWT
rsa.private-key=classpath:certs/private.pem
rsa.public-key=classpath:certs/public.pem

# state
state-key=<state_key>

# Spotify client id
my.client.id=<your_client_id>
my.client.secret=<your_client_secret>

# redirect uri
my.redirect.uri=http://localhost:5173/home # if 2-tier architecture (or :9090 with 1-tier)

# spotify user auth redirect
spotify.redirect.url=https://accounts.spotify.com/authorize?client_id=%s&response_type=%s&redirect_uri=%s&state=%s&scope=%s
# spotify calls
spotify.auth=https://accounts.spotify.com
spotify.user.analytics=https://api.spotify.com/v1/me
spotify.analytics=https://api.spotify.com/v1

# Music Brainz
music-brainz.base.url=https://musicbrainz.org/ws/2

# token cache expiration
token.cache.ttl=PT55M
```

### DataSource Configuration
You can either host a database locally on with docker.
If you will only deploy the application locally, connecting it to a local database 
will suffice 
```<your_database_source> ``` will then be ```localhost```. 
If you want to run it with docker, so that you can also run the application
with docker, ```<your_database_source> ``` will be the name that you give to your 
MySQL docker container. 

### JWT Token
In this case, you can see I am directing to a folder in `resources`
called `certs` that contains the files `private.pem` and `public.pem`. 
I have set up the JWT oauth to have asymmetric encryption, which means we have to
have a private key and a public key.
In order to generate the two, I used `OpenSSL`.

To do the same, once created the /cert directory, go into that 
from terminal and run the following commands: 

```
# create rsa key pair
openssl genrsa -out keypair.pem 2048

# extract public key
openssl rsa -in keypair.pem -pubout -out public.pem

# create private key in PKCS#8 format
openssl pkcs8 -topk8 -inform PEM -outform PEM -nocrypt -in keypair.pem -out private.pem
```

### Spotify
In order to finish the setup, you ought to create a [Spotify Web Developer](https://developer.spotify.com/) 
account. Follow the instructions and then copy `client_id` and `client_secret`
in the `application.properties` doc. 

### OpenAPI
Since I am using OpenAPI to generate the controllers and dtos, it is 
necessary before running the app to create the classes in `target`. To do so, 
if you use maven
run the command:
```
mvn clean compile
```

### Run the app with docker
If you want to run the application with docker, you should run from the terminal
```
spring-boot:build-image
docker run --network <network_shared_with_db> -p 9090:9090 <app_name>:0.0.1-SNAPSHOT
```



