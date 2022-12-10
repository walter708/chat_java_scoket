import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
  /**
   * Stream -> Communication over a network in java is 
   * carried through a stream 
   * Now there a two types of streams 
   * Input stream ->  is the  which is medium 
   * through which messesage gets to you 
   * Output stream -> This is the medium through which you message 
   * is sent to another person
   * 
   * ServerSocket -> This is a class that enables setting up the server 
   * 
   * socket -> This is what is responsible for setting up a connection
   * between server and client 
   */
public class Server extends JFrame{
  private JTextField userText;
  private JTextArea chatWindow;
  private ObjectInputStream input;
  private ObjectOutputStream output;
  private ServerSocket server;
  private Socket connection;
  
  public Server(){
    super("Instant Messaging : Server");
    userText = new JTextField();
    /** This is been set to false because if it is set to true 
     * message sent before connection is create messes up the stream
     */
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
    chatWindow = new JTextArea();
    chatWindow.setEditable(false);
    add(new JScrollPane(chatWindow));
    setSize(300, 500);
    setVisible(true);
  }
  
  public void startRunning(){
    try{
      // create the server 
      server = new ServerSocket(6789, 100);
      // adding this in a while so that the server stays alive.
      // until some ends it 
      while(true){
        try{
          
          waitForConnection();
          setupStreams();
          whileChatting();
        }catch(EOFException eofException){
          showMessage("\n Sever ended the connection");
        }finally{
          houseKeeping();
        }
      }
    }catch(IOException ioException){
      ioException.printStackTrace();
    }
  }
  
  
  
  /** The first that i will do is wait for someone to connect with me */
  private void waitForConnection() throws IOException {
    showMessage(" Waiting for connection... \n");
    /** Listens for a connection to be made to this socket and accepts it. 
     *  The method blocks the thread "while true"in startConnection
     * until a connection is made */
    connection = server.accept();
    /* Now this shows the Ip address of the person am connected to */
    showMessage(" Now connected to " + connection.getInetAddress().getHostName());
  }
  
  
  
  
    /** The next to do is set streams, i.e the output and the 
   * input streams 
   */
   private void setupStreams() throws IOException{
    output = new ObjectOutputStream(connection.getOutputStream());
    /** next you want to do is flush the pipline and ensure there are no left 
     * over data in the stream
     */
    output.flush();
    input = new ObjectInputStream(connection.getInputStream());
    /** N/B -> you can not flush the input strem only the person one the 
     * other can 
     */
    showMessage("\n Stream are now setup! \n");
    
  }
   
       /** The final thing to do is to setup a way to chat between the 
     * two parties connected 
     * 
     */
    private void whileChatting() throws IOException {
      String message = "You can now start chatting";
      showMessage(message);
      ableToType(true);
      do{
        try{
          message = (String) input.readObject();
          showMessage("\n" + message);
        }catch(ClassNotFoundException classNotFoundException){
          showMessage("\n I don't what the user typed");
        }
      }while(!message.equals("CLIENT - END"));
    }
          
  
  // Closing the input and the ouput stream and the connection 
  private void houseKeeping(){
    showMessage("\n closing connection...\n");
    ableToType(false);
    try{
      output.close();
      input.close();
      connection.close();
      showMessage("\n application closed\n");
    }catch(IOException ioException){
      ioException.printStackTrace();
    }
  }
  // This sends message to the client 
  private void sendMessage(String message){
    try{
      output.writeObject("SERVER - " + message);
      output.flush();
      showMessage("\nSERVER - " + message);
    }catch(IOException ioException){
      chatWindow.append("\n Don't know what went wrong with your message");
    }
  }
  
  
  // updates chatWindow
  private void showMessage(final String text){
    /** What SwingUtilities.invokeLater does is that it set aside a thread 
     * that Update you GUI when this method is called as suppose to 
     * building a new GUI and replacing the old one. 
     */
    SwingUtilities.invokeLater(
      new Runnable(){
        public void run(){
          chatWindow.append(text);
        }
      }
    );
  }
  
  // allow the user to type into the textField
  private void ableToType(final boolean tof){
    SwingUtilities.invokeLater(
      new Runnable() {
        public void run(){
          userText.setEditable(tof);
        }
      }
    );
  }
}
