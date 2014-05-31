package oaq.player;

import oaq.table.Table;

public class GameVariables {
	public boolean isReady;
	public boolean isMoving;
	public int orderNumber;
	public int stonesNumber;
	public int bigStonesNumber;
	public int borrowingNumber;
	public Table table;

	public GameVariables(Table table, int orderNumber) {
		this.table = table;
		this.orderNumber = orderNumber;
	}

	public void resetVariables(int orderNumber) {
		this.orderNumber = orderNumber;
		isReady = false;
		isMoving = false;
		stonesNumber = 0;
		bigStonesNumber = 0;
		borrowingNumber = 0;
	}

	public int getPoints() {
		return stonesNumber + (bigStonesNumber * 10);
	}
}
