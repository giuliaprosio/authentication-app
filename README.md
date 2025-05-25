## Authentication Backend Application 
An application to connect a web page with login or creation of a new account. 
The data is set up to be connected to a MySQL database. 

Once logged in, the communication uses JWT tokens. The 
user can connect to their Spotify account and review
cool analysis of their listening patterns!

To host it locally, clone the repo. You will then see in 

>./src/main/resources/application.yaml

That there are some `env vars` that need to be set.
In the next subsections we will see how to set them. 

In the file, write the following code. In the subsequent steps I will detail how
to fill out the various env vars.

### DataSource Configuration
In the `application.yaml` there are two environmental variables you need to set:
``SPRING_DATASOURCE_USERNAME`` and ``SPRING_DATASOURCE_PASSWORD``. 
These are the username and password associated to your local MySql database 
so that Spring can establish a connection to it.

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
account. Follow the instructions and then copy `SPOTIFY_CLIENT_ID` and `SPOTIFY_CLIENT_SECRET`.

### Others
For the communication with the MusicBrainz API it is necessary to append an email to the requests, 
thus the need for an `EMAIL` env var.

To check the safe communication with the Spotify API, a `state` parameter is requested and thus 
the need for a `STATE_KEY` env var.

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



