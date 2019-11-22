package se.farida;
import java.io.*;
import java.net.*;
import java.util.NoSuchElementException;

public class UserThread extends Thread {
  private Socket socket;
  private Server server;
  private PrintWriter writer;
  private String userName;

  public UserThread(Socket socket, Server server) {
    this.socket = socket;
    this.server = server;
  }

  public void run() {
    try {
      InputStream input = socket.getInputStream();
      BufferedReader reader = new BufferedReader(new InputStreamReader(input));

      OutputStream output = socket.getOutputStream();
      writer = new PrintWriter(output, true);

      printUsers();

      this.userName = reader.readLine();
      server.addUserName(this.userName);

      String serverMessage = "New user connected: " + this.userName;
      server.broadcast(serverMessage, this);

      String clientMessage;
      do {
        clientMessage = reader.readLine();
        if (clientMessage.startsWith("#")){
          String name = clientMessage.substring(1).split(" ")[0];
          String content = clientMessage.substring(name.length() + 2);
          try{
            UserThread user = server.findUser(name);
            server.sendToOne(content,this, user);
          } catch (NoSuchElementException ex){
            this.sendMessage(ex.getMessage());
          }
        } else {
          server.broadcast(clientMessage, this);
        }
      } while (!clientMessage.equals("bye"));

      socket.close();

    } catch (IOException ex) {
      System.out.println("Error in UserThread: " + ex.getMessage());
      ex.printStackTrace();
    } catch (NullPointerException ex){
      String serverMessage = this.userName + " has quited.";
      server.broadcast(serverMessage, this);
    } finally{
      server.removeUser(this.userName, this);
    }
  }

  public String getUserName() {
    return userName;
  }

  void printUsers() {
    if (server.hasUsers()) {
      writer.println("Connected users: " + server.getUserNames());
    } else {
      writer.println("No other users connected");
    }
  }

  void sendMessage(String message) {
    writer.println(message);
  }
}

