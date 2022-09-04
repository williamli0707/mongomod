
# MongoMod

A Minecraft mod that connects to MongoDB and logs the current online players, when the server was last turned on or off. 

## Features

- MongoDB integration
- Uploads a log of names of all players online every 100 ticks
- Works with [Lermit Bot](https://github.com/williamli0707/lermitbot)

## Building MongoMod for Personal Use

- Create a MongoDB database. Allow connections from 0.0.0.0/0, and create a username/password. Call the first cluster `Cluster0`. In this cluster, create a database that corresponds with the `level-name` property in your Minecraft server.properties file. 
- Click the Connect button for the database and choose Java application. Copy the connection string you see and put your password in. In the mod, go to `src/main/java/com/github.williamli0707.mod/MongoMod.java` and find the method called `onServerStarting`. Replace the text inside MongoClients.create with your pasted connection string. 
- In the project terminal, type `mvn clean package`, and in the target directory you should see the jar file of the mod being built. Put this jar file into the mods folder of the server. 
- Start the server. 

## License

[GPL v3](https://www.gnu.org/licenses/gpl-3.0.txt)

