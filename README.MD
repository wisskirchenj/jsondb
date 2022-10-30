# IDEA EDU Course ...

Implemented in the Java Developer Track of hyperskill.org's JetBrain Academy.

Purpose of doing this project, is to further practise core java topics as multi-threading, a socket,
server-socket server-client application, the Gson and Jcommander libraries and some more POJO java and
collect some experience with REDIS NoSQL-DB and it's Java connector Jedis.

This is a graduate project of the Java Developer Track.

## Technology / External Libraries

- POJO Java 18,
- Socket & ServerSocket - connection between server and client(s)
- multi-threading with Java Executor-Service (Java-core .util.concurrent)
- Apache GSON library
- JCommander library to parse CL-arguments
- Redis / Jedis -> used in Stage 4 - also CI-Container as GitHub-actions service
- Apache Log4j SLF4J API binding to Log4j 2 logging and
- Junit 5 with
- Mockito (mockito-inline) testing.

## Repository Contents

The sources of main project tasks (6 stages) and unit, mockito testing.

## Program description

A server-client(s) application that simulates the Json storage and retrieval in a server-side
Json-database. The DB-realization is done via self-made Json-file File-Database (in later stages) and the
Client-Server connection is "low-level" build via Socket to ServerSocket connections. Clients are processed
multithreaded using ExecutorService.

Have fun!

## Project completion

Project was completed on 30.10.22.

## Progress

24.09.22 Project started - git repo and gradle setup.

24.09.22 Stage 1 completed - a cell database simulated by y string array. Some CL commands to set, get and delete in 
the 'database'.

14.10.22 Stage 2 completed - a client and server application are implemented, which communicate via sockets. A very
basic connection is established in this stage - requesting and receiving a random record #.

16.10.22 Stage 3 completed - server applications now handles the cell database and works on socket connection
for clients in a loop until exit command set. com.beust.jcommander used to parse CL-parameter options

21.10.22 Stage 4 completed - socket communication now in Json Format (Database Command and Response). Use of Gson-library.
Database is now a Redis KeyStore which is connected from the Java Server with the Jedis-library.

24.10.22 Stage 5 completed - multithreaded request handling with executor service. 
Database file-based hand-made using ReentrantReadWriteLock.

24.10.22 Stage 6 completed - multithreaded and now with arbitrary Json-values to store - and set/get/delete can
accept hierarchical keychain to set/look/delete nested in Json.