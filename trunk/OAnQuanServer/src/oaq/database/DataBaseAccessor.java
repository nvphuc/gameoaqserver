package oaq.database;

import java.awt.Toolkit;

import javax.swing.ImageIcon;

public class DataBaseAccessor {

	public int accessDataBase(String userName, String pass) {

		return 1;
	}

	public ImageIcon getAvatar(int idPlayer) {
		return new ImageIcon("images/1.jpg");
	}

	public boolean createAccount(String userName, String pass1, String pass2) {
		
		return true;
	}

	public int getMoney(int idPlayer) {

		return 0;
	}

}
