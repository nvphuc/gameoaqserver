package oaq.player;

import java.net.Socket;

import javax.swing.ImageIcon;

import oaq.connector.Connector;
import oaq.newtype.PlayerStatus;
import oaq.processor.PlayerProcessor;
import oaq.server.Server;


public class Player {
	
	private Server server;
	private Connector connector;
	private String userName;
	private ImageIcon avatar;
	private int money;
	private int idPlayer;
	private GameVariables game;
	private PlayerStatus status;

	public Player(Server server, Socket socket) {
		this.server = server;
		connector = new Connector(socket);
		userName = "";
		avatar = null;
		money = 0;
		idPlayer = -1;
		game = null;
		status = PlayerStatus.CONNECT;
		new PlayerProcessor(this);
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public ImageIcon getAvatar() {
		return avatar;
	}

	public void setAvatar(ImageIcon avatar) {
		this.avatar = avatar;
	}

	public int getMoney() {
		return money;
	}

	public void setMoney(int money) {
		this.money = money;
	}

	public int getIdPlayer() {
		return idPlayer;
	}

	public void setIdPlayer(int idPlayer) {
		this.idPlayer = idPlayer;
	}

	public PlayerStatus getStatus() {
		return status;
	}

	public void setStatus(PlayerStatus status) {
		this.status = status;
	}

	public Server getServer() {
		return server;
	}

	public Connector getConnector() {
		return connector;
	}

	public GameVariables getGame() {
		return game;
	}

	public void setGame(GameVariables game) {
		this.game = game;
	}
}
