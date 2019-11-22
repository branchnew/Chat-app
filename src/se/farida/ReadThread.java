package se.farida;

import java.io.*;
import java.net.*;

public class ReadThread extends Thread {
  private BufferedReader reader;
  private Client client;

  public ReadThread(Socket socket, Client client) {
    this.client = client;

    try {
      InputStream input = socket.getInputStream();
      reader = new BufferedReader(new InputStreamReader(input));
    } catch (IOException ex) {
      System.out.println("Error getting input stream: " + ex.getMessage());
      ex.printStackTrace();
    }
  }

  public void run() {
    synchronized (client){
      try {
        while(client.getUserName() == null) {
          client.wait();
        }
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

    while (true) {
      try {
        String response = reader.readLine();
        System.out.println(response);
      } catch (SocketException ex){
        System.out.println("You have been disconnected!");
        break;
      } catch (IOException  ex) {
        System.out.println("Error reading from server: " + ex.getMessage());
        ex.printStackTrace();
        break;
      }
    }

  }
}

