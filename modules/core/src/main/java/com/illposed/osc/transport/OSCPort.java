/*
 * Copyright (C) 2003-2014, C. Ramakrishnan / Illposed Software.
 * All rights reserved.
 *
 * This code is licensed under the BSD 3-Clause license.
 * See file LICENSE (or LICENSE.html) for more information.
 */

package com.illposed.osc.transport;

import com.illposed.osc.transport.udp.UDPTransport;
import com.illposed.osc.OSCSerializerAndParserBuilder;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;

/**
 * An abstract superclass.
 * To send OSC messages, use {@link OSCPortOut}.
 * To listen for OSC messages, use {@link OSCPortIn}.
 */
public class OSCPort {
	private final Transport transport;

	public Transport getTransport() {
		return transport;
	}

	public static final int DEFAULT_SC_OSC_PORT = 57110;
	public static final int DEFAULT_SC_LANG_OSC_PORT = 57120;

	protected OSCPort(
		final SocketAddress local,
		final SocketAddress remote,
		final OSCSerializerAndParserBuilder serializerAndParserBuilder)
		throws IOException
	{
		this.transport = new UDPTransport(local, remote);
	}

	/**
	 * The port that the SuperCollider <b>synth</b> engine
	 * usually listens to.
	 * @return default SuperCollider <b>synth</b> UDP port
	 * @see #DEFAULT_SC_OSC_PORT
	 */
	public static int defaultSCOSCPort() {
		return DEFAULT_SC_OSC_PORT;
	}

	/**
	 * The port that the SuperCollider <b>language</b> engine
	 * usually listens to.
	 * @return default SuperCollider <b>language</b> UDP port
	 * @see #DEFAULT_SC_LANG_OSC_PORT
	 */
	public static int defaultSCLangOSCPort() {
		return DEFAULT_SC_LANG_OSC_PORT;
	}

	/**
	 * Generates a wildcard IP address (matches all IPs) of the same
	 * family as the given address.
	 * @param address from this we figure out the IP address family (IP v4 or v6)
	 * @return
	 *   <code>0.0.0.0</code> if IP v4,
	 *   <code>::</code> if IP v6,
	 *   <i>undefined behavior</i> otherwise
	 * @throws UnknownHostException this should never occur
	 */
	public static InetAddress generateWildcard(final SocketAddress address) throws UnknownHostException {
		return InetAddress.getByName((extractFamily(address) == 4) ? "0.0.0.0" : "::");
	}

	/**
	 * Extracts the (IP) family of a given address.
	 * @param address the address of which to return the (IP) family of
	 * @return
	 *   <code>4</code> if IP v4,
	 *   <code>6</code> if IP v6,
	 *   <code>0</code> otherwise
	 */
	public static int extractFamily(final SocketAddress address) {
		final int family;
		if (address instanceof InetSocketAddress) {
			final InetSocketAddress iNetAddress = (InetSocketAddress) address;
			if (iNetAddress.getAddress() instanceof Inet4Address) {
				family = 4;
			} else if (iNetAddress.getAddress() instanceof Inet6Address) {
				family = 6;
			} else {
				family = 0;
			}
		} else {
			family = 0;
		}

		return family;
	}

	public void connect() throws IOException {
		transport.connect();
	}

	public void disconnect() throws IOException {
		transport.disconnect();
	}

	public boolean isConnected() {
		return transport.isConnected();
	}

	/**
	 * Close the socket and free-up resources.
	 * It is recommended that clients call this when they are done with the
	 * port.
	 * @throws IOException If an I/O error occurs on the channel
	 */
	public void close() throws IOException {
		transport.close();
	}

	@Override
	public String toString() {
		return String.format("[%s (%s)]", getClass().getSimpleName(), transport);
	}
}
