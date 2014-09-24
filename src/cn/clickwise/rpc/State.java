package cn.clickwise.rpc;

import java.io.Serializable;

@SuppressWarnings("serial")
public class State extends Result implements Serializable{

	private int state;

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}
}
