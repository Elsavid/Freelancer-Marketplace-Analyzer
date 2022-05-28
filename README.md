# FreeLanceLot

The overall goal is to develop a web application that can analyze the content on Freelancer.com using its REST API.

[Freelancer.com](https://www.freelancer.com/) is a global freelancing and crowdsourcing marketplace with over 26 million users.

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. Below instructions are tested on macOS 11.6

### Prerequisites
- Java 8 or 11, since Play only supports [Java 8 or 11](https://www.playframework.com/documentation/2.8.x/Requirements)
- sbt

### Installing
#### Java 11
- Your system probably has pre-installed Java, check which Java version is installed, you can run following command
```
$ /usr/libexec/java_home -V
```
- If you don't have Java 11, you need to install it. In order to manage multiple Java versions and easily switch between them, I use jEnv. Other similar tool, such as SDKMAN, should also work. Follow jEnv [setup page](https://www.jenv.be/) and use it to configure java version to 11.
- In the end, you should output following:
```
$ java --version
openjdk version "11.0.14.1" 2022-02-08
OpenJDK Runtime Environment Temurin-11.0.14.1+1 (build 11.0.14.1+1)
OpenJDK 64-Bit Server VM Temurin-11.0.14.1+1 (build 11.0.14.1+1, mixed mode)
```
#### sbt
You can choose to install sbt on your machine by
```
$ brew install sbt
```
or

Using the sbt inside the project, so you can skip sbt installation

No matter how you plan to use sbt, proper Java version is required

### IDE Setup
You can continue to setup your preferred IDE by following [documentation](https://www.playframework.com/documentation/2.8.x/IDE). Please add any IDE configuration files to .gitignore.


## Running locally
Navigate to project folder, run `sbt run`. After the message `Server started, ...` displays, enter the following URL in a browser: <http://localhost:9000>


## Supported SBT commands

- `sbt run` : Start project in <http://localhost:9000>
- `sbt test` : Run all test cases
- `sbt jacoco` : Generate Java code coverage, available in `SOEN6441/target/scala-2.13/jacoco/report`
- `sbt doc` : SBT default doc generation task, which generates the Scaladoc in `SOEN6441/target/scala-2.13/api`
- `sbt genjavadoc:doc` : Customized SBT doc generation task, which generates the Javadoc in `SOEN6441/target/scala-2.13/genjavadoc-api` Note: after first run `sbt genjavadoc:doc`, if you wish to regenerate the javadoc, you need to run `sbt clean`, then `sbt genjavadoc:doc`

## Built With

* [Play](https://www.playframework.com/) - The web framework used


## Documentation

Design documentation is avaliable on [this](https://drive.google.com/drive/folders/1b6DG6JRsUpQ4E6mYhlDHLio8DEATh4kr?usp=sharing) shared folder
## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details

## Acknowledgments
