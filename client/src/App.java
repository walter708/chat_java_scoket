import javax.swing.JFrame;

public class App {
    public static void main(String[] args) throws Exception {
        Client client; 
        client = new Client("127.0.0.1");
        client.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        client.startRunning();
    }
}
