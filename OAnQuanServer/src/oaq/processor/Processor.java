package oaq.processor;

import javax.swing.ImageIcon;

import oaq.connector.Connector;
import oaq.database.DataBaseAccessor;
import oaq.player.Player;
import oaq.server.Server;

public abstract class Processor extends Thread {

	protected Player player;
	protected DataBaseAccessor databaseAccessor;
	
	public Processor(Player player) {
		this.player = player;
		this.databaseAccessor = new DataBaseAccessor();
		this.start();
	}
	
	public Connector getConnector() {
		return player.getConnector();
	}
	
	protected void sendMessage(String message) {
		player.getConnector().sendMessage(message);
	}
	
	protected void sendAvatar(ImageIcon avatar) {
		player.getConnector().sendImage(avatar);
	}
	
	protected String receiveMessage() {
		return player.getConnector().receiveMessage();
	}
	
	protected ImageIcon receiveAvatar() {
		return player.getConnector().receiveImage();
	}
	
	protected Server getServer() {
		return player.getServer();
	}
	
	abstract public void run();

	abstract protected void handleMessage(String message);	
}
