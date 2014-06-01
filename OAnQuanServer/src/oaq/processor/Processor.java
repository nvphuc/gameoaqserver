package oaq.processor;

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
	
	protected void disconnect() {
		databaseAccessor.logout(player.getIdPlayer());
		player.getConnector().disconnect();
		getServer().removePlayer(player);
	}
	
	public Connector getConnector() {
		return player.getConnector();
	}
	
	protected Server getServer() {
		return player.getServer();
	}
	
	abstract public void run();

	abstract protected void handleMessage(String message);	
}
