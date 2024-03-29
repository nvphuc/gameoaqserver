package oaq.processor;

import oaq.newtype.PlayerStatus;
import oaq.player.Player;
import oaq.table.Table;

public class PlayerProcessor extends Processor {

	public PlayerProcessor(Player player) {
		super(player);
	}

	@Override
	public void run() {
		while (player.getStatus() == PlayerStatus.CONNECT
				|| player.getStatus() == PlayerStatus.EDIT_ACCOUNT) {
			handleMessage(getConnector().receiveMessage());
		}
		if (player.getStatus() == PlayerStatus.DISCONNECT) {
			disconnect();
		}
	}

	@Override
	protected void handleMessage(String message) {
		System.out.println("PlayerProcessor : " + message);
		
		String[] args = message.split("@");

		switch (args[0]) {

		case "Register":
			processRegister(args[1]);
			break;
			
		case "Login":
			processLogin(args[1]);
			break;
			
		case "Edit":
			getConnector().sendMessage("RSEdit");
			player.setStatus(PlayerStatus.EDIT_ACCOUNT);
			break;
			
		case "BackWaitingRoom":
			getConnector().sendMessage("RSBack");
			player.setStatus(PlayerStatus.CONNECT);
			break;
			
		case "UpdateUserName":
			processUpdateAccount(0, args[1]);
			break;

		case "UpdatePass":
			processUpdateAccount(1, args[1]);
			break;
			
		case "UpdateAvatar":
			processUpdateAvatar();
			break;

		case "CreateTable":
			processCreateTable(args[1]);
			break;

		case "PlayRight":
			processPlayRight();
			break;

		case "UpdataTables":
			processUpdateTables();
			break;

		case "JoinTable":
			processJoinTable(args[1]);
			break;

		default:
			player.setStatus(PlayerStatus.DISCONNECT);
			break;
		}
	}

	private void processUpdateAvatar() {
		boolean check = getConnector().receiveImage(player.getIdPlayer());
		if(check) {
			getConnector().sendMessage("OK");
			databaseAccessor.updateAvatar(player.getIdPlayer());
		}
		else {
			getConnector().sendMessage("ERROR");
		}		
	}

	private void processUpdateAccount(int type, String args) {
		String[] data = args.split(":");
		
		boolean check;
		
		switch(type) {
		case 0:
			check = databaseAccessor.updateUserName(player.getIdPlayer(), data[0], data[1]);
			break;
			
		case 1:
			check = databaseAccessor.updatePass(player.getIdPlayer(), data[0], data[1]);
			break;
			
		default:
			check = false;
			break;
		}
		
		if(check) {
			getConnector().sendMessage("OK");
		}
		else {
			getConnector().sendMessage("ERROR");
		}
	}

	private void processUpdateTables() {
		String msg = "RSUpdateTables@";
		int numberTable = getServer().getTables().size();
		if (numberTable > 0) {
			for (int i = 0; i < numberTable - 1; i++) {
				Table table = getServer().getTables().get(i);
				msg += table.getTableName() + ":";
			}
			msg += getServer().getTables().get(numberTable - 1).getTableName();
		} else {
			msg += "NONE";
		}
		getConnector().sendMessage(msg);
	}

	private void processPlayRight() {
		for (Table tmpTable : getServer().getTables()) {
			if (tmpTable.isAvailable()) {
				tmpTable.addPlayer(player);
				player.setStatus(PlayerStatus.PLAY_GAME);
				getConnector().sendMessage(
						"RSPlayRight@OK:" + player.getGame().orderNumber);
				new GameProcessor(player);
				return;
			}
		}
		getConnector().sendMessage("RSPlayRight@ERROR:NONE");
	}

	private void processCreateTable(String tableName) {
		if(getTable(tableName) == null) {
			Table table = new Table(getServer(), tableName);
			table.addPlayer(player);
			player.setStatus(PlayerStatus.PLAY_GAME);
			getConnector().sendMessage(
					"RSCreateTable@OK:" + player.getGame().orderNumber);
			new GameProcessor(player);
		} else {
			getConnector().sendMessage("RSCreateTable@ERROR:NONE");
		}
	}

	private void processJoinTable(String tableName) {
		Table table = getTable(tableName);
		if (table != null) {
			if (table.addPlayer(player)) {
				player.setStatus(PlayerStatus.PLAY_GAME);;
				getConnector().sendMessage("RSJoinTable@OK:" + player.getGame().orderNumber);
				new GameProcessor(player);
			}
		} else {
			getConnector().sendMessage("RSJoinTable@ERROR:NONE");
		}
	}
	
	private void processLogin(String args) {
		String[] data = args.split(":");
		player.setIdPlayer(databaseAccessor.access(data[0], data[1]));
		if (player.getIdPlayer() > 0) {

			player.setUserName(data[0]);
			getConnector().sendMessage("OK");

			player.setCredit(databaseAccessor.getCredit(player.getIdPlayer()));
			getConnector().sendMessage(player.getCredit() + "");

			player.setAvatar(databaseAccessor.getAvatar(player.getIdPlayer()));
			getConnector().sendImage(player.getAvatar());

		} else {
			getConnector().sendMessage("ERROR");
		}
	}
	
	private void processRegister(String args) {
		String[] data = args.split(":");
		boolean check = databaseAccessor.createAccount(data[0], data[1],data[2]);
		if (check)
			getConnector().sendMessage("OK");
		else
			getConnector().sendMessage("ERROR");
	}
	
	public Table getTable(int index) {
		return getServer().getTables().get(index);
	}
	
	public Table getTable(String tableName) {
		for(Table table : getServer().getTables()) {
			if(tableName.equals(table.getTableName())){
				return table;
			}
		}
		return null;
	}
}
