package com.polopoly.util.client;


public class DisconnectedPolopolyContext extends PolopolyContext {

	public DisconnectedPolopolyContext() {
		super(null, null, null, null, new InfoSystemOuputLogger());
	}

}
