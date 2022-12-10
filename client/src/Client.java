import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.Border;

public class Client extends JFrame{
  private JTextField userText;
  private JTextArea chatArea;
  private ObjectInputStream input;
  private ObjectOutputStream output;
  private String message ="";
  private String serverIP;
  private Socket connection;
  
  public Client(String host){
    super("Instant Messaging : Client");
    serverIP = host;
    userText = new JTextField();
    userText.setEditable(false);
    userText.addActionListener(
      new ActionListener(){
        public void actionPerformed(ActionEvent event){
          sendMessage(event.getActionCommand());
          userText.setText("");
        }
      }
    );
    add(userText, BorderLayout.SOUTH);
    chatArea = new JTextArea();
    add(new JScrollPane(chatArea), BorderLayout.CENTER);
    setSize(300, 500);
    setVisible(true);
  }
  
  // setup connection to server 
  public void startRunning(){
    try{
      connectToServer();
      setUpStreams();
      whileChatting();
      
    }catch(EOFException eofException){
      showMessage("\n Client terminated connection");
    }catch(IOException ioException){
      ioException.printStackTrace();
    }finally{
      houseKeeping();
    }
  }
  
  /**This creates connection to the server 
   * This is different for the client side its
   * the client side that is responsible for creating 
   * connection.
   *  */ 
  private void connectToServer() throws IOException{
    showMessage("\n Attempting Connection...");
    connection = new Socket(InetAddress.getByName(serverIP), 6789);
    showMessage("Connected to " + connection.getInetAddress().getHostName());
  }
 
  // This method sets up the stream between the server and the client 
  private void setUpStreams() throws IOException{
    output = new ObjectOutputStream(connection.getOutputStream());
    output.flush();
    input  = new ObjectInputStream(connection.getInputStream());
    showMessage("\n Your stream are now available \n");
  }
  // The defines what the way the user can user the app to chat
  private void whileChatting() throws IOException{
    ableToType(true);
      do{
        try{
          message = (String) input.readObject();
          showMessage("\n" + message);
          
        }catch(ClassNotFoundException classNotFoundException){
          showMessage("\n have no clue what the sever set");
        }
    }while(!message.equals("SERVER - END"));
    
  }
  
  // This takes care of closing the stream and the connection 
  private void houseKeeping(){
    showMessage("\n closing application...");
    ableToType(false);
    try{
      output.close();
      input.close();
      connection.close();
    }catch(IOException ioException){
      ioException.printStackTrace();
    }
  }
  
  // This method sends message from the client to the server 
  private void sendMessage(String message){
    try{
      output.writeObject("CLIENT - "+ message);
      output.flush();
      showMessage("\nCLIENT - "+ message);
      
    }catch(IOException ioException){
      showMessage("\n somthing went wrong with sending message");
    }
  }
  
  // this method show the message history
  private void showMessage(final String message){
      SwingUtilities.invokeLater(
          new Runnable() {
             public void run(){
              chatArea.append(message);
             } 
          }
      );
  }
  
  // This enables and disables users permission to use testField
  private void ableToType(final boolean tof){
    SwingUtilities.invokeLater(
      new Runnable(){
        public void run(){
          userText.setEditable(tof);
        }
      }
    );
  }
  
}
  

