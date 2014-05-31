package oaq.connector;

import java.io.Serializable;

import javax.swing.ImageIcon;

public class InforPlayer implements Serializable {
	
	private String UserName;
	private ImageIcon Avatar;
	private int Status;
	
	// Status = 0: chua san sang; = 1: da san sang
	public InforPlayer(String UserName, ImageIcon Avatar, int Status) {
		this.UserName = UserName;
		this.Avatar = Avatar;
		this.Status = Status;
	}

	public String getUserName() {
		return UserName;
	}

	public ImageIcon getAvatar() {
		return Avatar;
	}

	public int getStatus() {
		return Status;
	}
}