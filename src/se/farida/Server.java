package se.farida;

import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
  private int port;
  private Set<String> userNames = new HashSet<>();
  private Set<UserThread> userThreads = new HashSet<>();

  public Server(int port) {
    this.port = port;
  }

  public void execute() {
    try (ServerSocket serverSocket = new ServerSocket(port)) {

      System.out.println("The Server is listening on port " + port);

      while (true) {
        Socket socket = serverSocket.accept();
        System.out.println("New user connected");

        UserThread newUser = new UserThread(socket, this);
        userThreads.add(newUser);
        newUser.start();

      }

    } catch (IOException ex) {
      System.out.println("Error in the server: " + ex.getMessage());
      ex.printStackTrace();
    }
  }

  void broadcast(String message, UserThread sender) {
    String taggedMessage = "[" + sender.getUserName() + "]: " + message;
    userThreads.stream()
        .filter((user)->user != sender)
        .forEach((user)->user.sendMessage(taggedMessage));
  }

  void sendToOne(String message, UserThread sender, UserThread receiver){
    String taggedMessage =  "#" + sender.getUserName() + ":" + message;
    receiver.sendMessage(taggedMessage);
  }

  UserThread findUser(String name) throws NoSuchElementException{
    for(UserThread userThread : userThreads){
      if(userThread.getUserName().equals(name)){
        return userThread;
      }
    }
    throw new NoSuchElementException("This user doesn't exist!");
  }

  void addUserName(String userName) {
    userNames.add(userName);
  }

  void removeUser(String userName, UserThread user) {
    boolean removed = userNames.remove(userName);
    if (removed) {
      userThreads.remove(user);
      System.out.println(userName + " has quited");
    }
  }

  Set<String> getUserNames() {
    return this.userNames;
  }

  boolean hasUsers() {
    return !this.userNames.isEmpty();
  }

  public static void main(String[] args){

    if (args.length < 1) {
      System.out.println("Syntax: java Server <port-number>");
      System.exit(0);
    }

    int port = Integer.parseInt(args[0]);

    Server server = new Server(port);
    server.execute();
  }
}

