package oaq.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

import oaq.player.Player;
import oaq.table.Table;

public class Server {

	private Vector<Player> players;
	private Vector<Table> tables;
	
	public Server() {
		players = new Vector<Player>();
		tables = new Vector<Table>();
		ServerSocket server;
		try {
			server = new ServerSocket(9998);
			while (true) {
				Socket socket = server.accept();
				players.add(new Player(this, socket));
			}
		} catch (IOException e) {
			System.err.println(e);
		}
	}

	public Vector<Player> getPlayers() {
		return players;
	}

	public Vector<Table> getTables() {
		return tables;
	}
	
	public static void main(String[] args) {
		System.out.println("Start");
		new Server();
	}
}