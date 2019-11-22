package se.farida;

import java.io.*;
import java.net.*;

public class WriteThread extends Thread {
  private PrintWriter writer;
  private Socket socket;
  private Client client;

  public WriteThread(Socket socket, Client client) {
    this.socket = socket;
    this.client = client;

    try {
      OutputStream output = socket.getOutputStream();
      writer = new PrintWriter(output, true);
    } catch (IOException ex) {
      System.out.println("Error getting output stream: " + ex.getMessage());
      ex.printStackTrace();
    }
  }

  public void run() {
    try( BufferedReader reader = new BufferedReader(new InputStreamReader(System.in)) ) {
      System.out.println("\nHi! Enter your name: ");
      String userName = reader.readLine();
      this.client.setUserName(userName);
      this.writer.println(userName);

      synchronized (client) {
        client.notify();
      }

      String text ;
      do {
        text = reader.readLine();
        this.writer.println(text);
      } while (!text.equals("bye"));

    } catch (IOException e) {
      e.printStackTrace();
    }

    try {
      socket.close();
    } catch (IOException ex) {
      System.out.println("Error writing to server: " + ex.getMessage());
    }
  }
}

