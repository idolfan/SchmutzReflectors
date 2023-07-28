package basis;

import java.awt.Frame;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import game.Game;
import game.World;
import server.Client;
import server.Server;

public class GameFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	public static final boolean dev = false;

	public GameFrame() {
		this.setTitle("Schmutz-Example");
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		this.setResizable(false);
		this.setUndecorated(true);
		this.setExtendedState(Frame.MAXIMIZED_BOTH);
		this.add(new Game(this));

		this.pack();
		this.setVisible(true);
		this.setLocationRelativeTo(null);
	}

	public World connect(String ip, int port) {
		if(Client.startClient(ip, port) != null) {
			return Client.world;
		}
		return null;
	}

	public void disconnect() {
		if(Client.client != null) {
			try {
				Client.client.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void startServer(int[] configuration) {
		Server.startServer(configuration);
	}

	public void stopServer() {
		if(Server.server != null) {
			try {
				Server.server.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
