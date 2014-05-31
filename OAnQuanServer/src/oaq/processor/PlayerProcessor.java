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
		while (player.getStatus() == PlayerStatus.CONNECT) {
			handleMessage(getConnector().receiveMessage());
		}
		if (player.getStatus() == PlayerStatus.DISCONNECT) {
			getServer().getPlayers().remove(player);
			getConnector().disconnect();
		}
	}

	@Override
	protected void handleMessage(String message) {

		String[] args = message.split("@");

		switch (args[0]) {

		case "Login":
			processLogin(args[1]);
			break;

		case "CreateTable":
			processCreateTable(args[1]);
			break;

		case "PlayRight":
			processPlayRight();
			break;

		case "Register":
			processRegister(args[1]);
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
				player.setStatus(PlayerStatus.PLAYGAME);
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
			player.setStatus(PlayerStatus.PLAYGAME);
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
				player.setStatus(PlayerStatus.PLAYGAME);;
				getConnector().sendMessage("RSJoinTable@OK:" + player.getGame().orderNumber);
				new GameProcessor(player);
			}
		} else {
			getConnector().sendMessage("RSJoinTable@ERROR:NONE");
		}
	}
	
	private void processLogin(String args) {
		String[] data = args.split(":");
		player.setIdPlayer(databaseAccessor.accessDataBase(data[0], data[1]));
		if (player.getIdPlayer() > 0) {
			player.setUserName(data[0]);
			getConnector().sendMessage("OK");
			player.setMoney(databaseAccessor.getMoney(player.getIdPlayer()));
			getConnector().sendMessage(player.getMoney() + "");
			player.setAvatar(databaseAccessor.getAvatar(player.getIdPlayer()));
			getConnector().sendImage(player.getAvatar());
		} else {
			getConnector().sendMessage("ERROR");
		}
	}

	private void processRegister(String args) {
		String[] data = args.split(":");
		boolean check = databaseAccessor.createAccount(data[0], data[1],
				data[2]);
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
