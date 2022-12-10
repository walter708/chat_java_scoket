import javax.swing.JFrame;
public class App {
    public static void main(String[] args) throws Exception {
       Server server = new Server();
       server.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       server.startRunning();
    }
}
