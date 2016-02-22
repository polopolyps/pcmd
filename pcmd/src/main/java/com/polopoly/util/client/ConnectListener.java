package com.polopoly.util.client;

public interface ConnectListener {
	void connectedToPolopoly(PolopolyContext context);

	void willConnectToPolopoly(PolopolyClient client);
}
