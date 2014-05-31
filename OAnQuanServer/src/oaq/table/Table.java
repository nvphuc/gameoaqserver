package oaq.table;

import oaq.connector.InforPlayer;
import oaq.connector.InforTable;
import oaq.player.GameVariables;
import oaq.player.Player;
import oaq.server.Server;

public class Table extends Thread {
	private Server server;
	private String tableName;
	private Player[] players;
	private int playersNumber;
	private int winner;
	public int curPlayer;
	public int readyNumber;
	public int moveFinish;
	public boolean hasPlayer, isEndGame;
	public int[] board;
	public boolean[] bigStones;

	// test
	public void showBoard() {
		System.out.println("Test=======================================");
		System.out.print(board[11]);		
		for(int i = 10; i > 5; i--){
			System.out.print("  " + board[i]);
		}
		System.out.println();
		System.out.print("  ");
		for(int i = 0; i < 5; i++){
			System.out.print("  " + board[i]);
		}
		System.out.println("  " + board[5]);
		
		System.out.println(bigStones[0] + " " + bigStones[1]);
		
		System.out.println(getPlayer(0).getGame().stonesNumber + " " + getPlayer(0).getGame().bigStonesNumber + " " + getPlayer(0).getGame().borrowingNumber);
		System.out.println(getPlayer(1).getGame().stonesNumber + " " + getPlayer(1).getGame().bigStonesNumber + " " + getPlayer(1).getGame().borrowingNumber);
		System.out.println("=======================================");
	}

	public Table(Server server, String tableName) {
		this.server = server;
		this.tableName = tableName;
		this.server.getTables().add(this);
		players = new Player[2];
		for (int i = 0; i < 2; i++) {
			players[i] = null;
		}
		playersNumber = 0;
		winner = -1;
		resetTable();
		this.start();
	}

	public void resetTable() {
		hasPlayer = true;
		isEndGame = false;
		curPlayer = -1;
		readyNumber = 0;
		moveFinish = 0;
		board = new int[12];
		for (int i = 0; i < 5; i++) {
			board[i] = 5;
			board[i + 6] = 5;
		}
		board[5] = 0;
		board[11] = 0;
		bigStones = new boolean[2];
		bigStones[0] = true;
		bigStones[1] = true;
	}

	public void run() {
		while (hasPlayer) {
			// Cho ca hai ready
			while (readyNumber != 2) {
				try {
					sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			// Reset lai cac gia tri cua nguoi choi
			for (int i = 0; i < 2; i++) {
				Player player = players[i];
				player.getGame().resetVariables(i);
			}

			// Gui thong bao bat dau choi (client khoi tao cac hon soi)
			sendMessageToAllPlayers("StartGame");

			try {
				sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			sendMessageToAllPlayers("HideReady");
			
			// Gui thong bao danh den nguoi thang truoc hoac chu phong (2 la van
			// truoc hoa)
			if (winner != -1 && winner != 2) {
				curPlayer = winner;
			} else {
				curPlayer = 0;
			}

			// Gui thong bao danh xuong cac nguoi choi
			sendMessageToAllPlayers("Turn@" + curPlayer);

			// Cho den khi van dau ket thuc
			while (!isEndGame) {
				try {
					sleep(1000);
				} catch (InterruptedException e) {
				}
			}

			// Lay ket qua game
			winner = gameResult();

			// Gui ket qua ve client
			sendMessageToAllPlayers("GameResult@" + winner);
			
			try {
				sleep(3000);
			} catch (InterruptedException e) {
			}
			
			String report = "Report@";
			report += getPlayer(0).getGame().stonesNumber + ":" + getPlayer(0).getGame().bigStonesNumber + ":" + getPlayer(0).getGame().borrowingNumber;
			report += "#" + getPlayer(1).getGame().stonesNumber + ":" + getPlayer(1).getGame().bigStonesNumber + ":" + getPlayer(1).getGame().borrowingNumber;
			sendMessageToAllPlayers(report);
			
			resetTable();
		}
	}



	// Da xong
	// ===================================================================================================================

	public void raidan(int index) {
		sendMessageToAllPlayers("Raidan@" + index);
		
		int j = index;
		if(index == 1)
			j = 6;
		
		for(int i = 0; i < 5; i++) {
			if(players[index].getGame().stonesNumber != 0) {
				players[index].getGame().stonesNumber--;
				board[j]++;
				j++;
			}
			else {
				if(players[(index+1)%2].getGame().stonesNumber != 0) {
					players[index].getGame().borrowingNumber++;
					players[(index+1)%2].getGame().stonesNumber--;
					board[j]++;
					j++;
				}
				else {
					return;
				}
			}
		}
		
	}
	
	public void updateBoard(int index, int cell, int direction) {
		if (index == 1)
			cell += 6;
		getMoveBoard(getPlayer(index).getGame(), cell, direction);
		
	}

	private int getNext(int cell, int direction) {
		cell += direction;
		if (cell > 11)
			cell = 0;
		if (cell < 0)
			cell = 11;
		return cell;
	}

	public void getMoveBoard(GameVariables game, int cell, int direction) {
		int cur = cell;
		int amount = board[cur];
		board[cur] = 0;
		while (amount > 0) {
			cur = getNext(cur, direction);
			board[cur]++;
			amount--;
		}
		int next = getNext(cur, direction);
		//dung tai bigcell va khac 0
		if ((next == 5 || next == 11) && (board[next] != 0 || bigStones[((next%5)+1)%2] == true)) {
			return;
		} else if (board[next] == 0) {
			int s = 0;
			while ((next != cur) && (board[next] == 0)) {
				next = getNext(next, direction);
				if (board[next] == 0)
					break;
				else {
					s += board[next];
					board[next] = 0;
					if((next == 5 || next == 11) && bigStones[((next%5)+1)%2] == true) {
						game.bigStonesNumber++;
						bigStones[((next%5)+1)%2] = false;
					}
					next = getNext(next, direction);
				}
			}
			game.stonesNumber += s;
			return;
		} else {
			getMoveBoard(game, next, direction);
		}
	}

	private boolean isEmptyStone() {
		int i = 0;
		if (curPlayer == 1)
			i += 6;
		for (int j = 0; j < 5; j++) {
			if (board[i++] != 0)
				return false;
		}
		return true;
	}

	public boolean isEndGame() {
		return board[5] == 0 && board[11] == 0;
	}

	public synchronized void updateGame() {
		moveFinish++;
		if (moveFinish == 2) {
			moveFinish = 0;
			if (!isEndGame()) {
				curPlayer = (curPlayer + 1) % 2;
				sendMessageToAllPlayers("Turn@" + curPlayer);
				if (isEmptyStone()) {
					sendMessageToAllPlayers("RequestRaidan@" + curPlayer);
				}			
			}
			else {
				isEndGame = true;
			}
		}
	}
	
	
	public String getTableName() {
		return tableName;
	}

	public boolean addPlayer(Player player) {
		if (playersNumber < 2) {
			playersNumber++;
			for (int index = 0; index < 2; index++) {
				if (players[index] == null) {
					players[index] = player;
					players[index].setGame(new GameVariables(this, index));
					return true;
				}
			}
		}
		return false;
	}

	public void removePlayer(int index) {
		players[index] = null;
	}

	public void removePlayer(Player player) {
		for (int i = 0; i < 2; i++) {
			if (players[i] == player) {
				players[i] = null;
				return;
			}
		}
	}

	public boolean isAvailable() {
		return playersNumber < 2;
	}

	public Player getPlayer(int index) {
		return players[index];
	}

	public void sendMessageToAllPlayers(String message) {
		for (Player player : players) {
			if (player != null)
				player.getConnector().sendMessage(message);
		}
	}

	private int gameResult() {
		for(int i = 0; i < 5; i++) {
			players[0].getGame().stonesNumber += board[i];
			players[1].getGame().stonesNumber += board[i + 6];
		}
		players[0].getGame().stonesNumber += players[1].getGame().borrowingNumber + players[0].getGame().bigStonesNumber * 10 - players[0].getGame().borrowingNumber;
		players[1].getGame().stonesNumber += players[0].getGame().borrowingNumber + players[1].getGame().bigStonesNumber * 10 - players[1].getGame().borrowingNumber;
		
		if (players[0].getGame().getPoints() > players[1].getGame().getPoints()) {
			return 0;
		} else if (players[0].getGame().getPoints() < players[1].getGame()
				.getPoints()) {
			return 1;
		} else
			return 2;
	}

	public void sendInforTableToAllPlayers() {
		InforPlayer[] inforPlayers = new InforPlayer[2];
		for (int i = 0; i < 2; i++) {
			if (players[i] != null) {
				Player player = players[i];
				int ready = 0;
				if (player.getGame().isReady)
					ready = 1;
				inforPlayers[i] = new InforPlayer(player.getUserName(),
						player.getAvatar(), ready);
			} else {
				inforPlayers[i] = null;
			}
		}
		InforTable inforTable = new InforTable(inforPlayers);
		for (int i = 0; i < 2; i++) {
			if (getPlayer(i) != null) {
				players[i].getConnector().sendMessage("InforTable");
				players[i].getConnector().sendInforTable(inforTable);
			}
		}
	}

}
