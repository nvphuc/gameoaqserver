package oaq.processor;

import oaq.newtype.PlayerStatus;
import oaq.player.Player;
import oaq.table.Table;


public class GameProcessor extends Processor {

	public GameProcessor(Player player) {
		super(player);
		getTable().sendInforTableToAllPlayers();		
	}

	@Override
	public void run() {
		while (player.getStatus() == PlayerStatus.PLAYGAME) {
			String message = getConnector().receiveMessage();
			handleMessage(message);
		}
	}

	@Override
	protected void handleMessage(String message) {
		System.out.println("Game Nhan : " + message);
		String[] args = message.split("@");
		String[] data;

		switch (args[0]) {
		
		case "Chat":
			processChat(args[1]);
			break;

		case "Ready":
			processReady();
			break;

		case "Move":
			processMove(args[1]);
			break;

		case "FinishMove":
			getTable().updateGame();
			break;

		case "Raidan":
			getTable().raidan(Integer.parseInt(args[1]));
			break;
		}

	}

	private void processMove(String message) {
		getTable().sendMessageToAllPlayers("Move@" + message);
		String[] data = message.split(":");
		int index = Integer.parseInt(data[0]);
		int selectedCell = Integer.parseInt(data[1]);
		int direction = Integer.parseInt(data[2]);
		getTable().updateBoard(index, selectedCell, direction);
	}

	// xong ===========================================================================================================
	
	private void processReady() {
		player.getGame().isReady = true;
		String msg = "Ready@" + player.getGame().orderNumber;
		getTable().sendMessageToAllPlayers(msg);
		getTable().readyNumber++;
	}
	
	private void processChat(String message) {
		
		getTable().sendMessageToAllPlayers(message);
	}
	
	private Table getTable() {
		return player.getGame().table;
	}
}
