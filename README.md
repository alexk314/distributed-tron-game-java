# Tron Application VS
## Authors:
- Philip Borchert: 2237188, alexander.koenemann@haw-hamburg.de
- Alexander Könemann: 2434660, philip.borchert@haw-hamburg.de

## Project Documentation
- Pdf document can be found in following path in this Repo:
`./FinalDocumentation/Projektdokumentation Tron Applikation Borchert Könemann.pdf`

## Project jars for executing the app:
- In order to start the Tron application you simply need to run the following command. You might need to check the prerequisites. 
  ```bash
  java -jar FinalProjectJar/tron-0.1.jar
  ```
- When you execute the command, you'll see a game menu. For a quick start just select 'local game' and click on 'host game'. 
- After playing a game, you can check the Application logs of each component in the Repo path. 
- If you want to test a multiplayer session, you have to start two applications. One Player has to host a game and the other can join after updating the game list. 

## Logs
- Each Application Run will create logs for all components. You can find Logs from a clean run from this application to get an overview of logging.

## Execute performance stress test:
- To enable the stress test, edit the game.properties file and set performanceStressTest=true.
- Afterwards execute the script starting multiple games. Note: It includes a fresh build and immediately starts the games:
    ```bash    
    ./tron_start_60_games
    ```
  
## Prerequisites
- Java 11
- Maven (required for build)
- `view-library` in local maven repository, 
    see [https://git.haw-hamburg.de/bai5-vsp-tron/view-library](https://git.haw-hamburg.de/bai5-vsp-tron/view-library)

## Build and Run manually
- Might need to set java version
```bash
export JAVA_HOME=/usr/java/java11/
```
- build executable jar
```bash
mvn clean package
```
### alternatively you can use the tron_start_game script, which will build the project first:
```bash
./tron_start_1_game or
./tron_start_2_games
```


