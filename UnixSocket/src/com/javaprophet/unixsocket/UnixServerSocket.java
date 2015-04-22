package com.javaprophet.unixsocket;

import java.io.File;
import java.io.IOException;
import com.javaprophet.unixsocket.CLib.sockaddr_un;
import com.sun.jna.ptr.IntByReference;

public class UnixServerSocket {
	private int sockfd = 0;
	private boolean bound = false;
	private String file = "";
	private int backlog = 50;
	private boolean closed = false;
	
	public boolean isClosed() {
		return closed;
	}
	
	public UnixServerSocket(String file, int backlog) throws IOException {
		this.backlog = backlog;
		File f = new File(file);
		f.getParentFile().mkdirs();
		f.delete();
		this.file = f.getAbsolutePath();
	}
	
	public UnixServerSocket(String file) throws IOException {
		this(file, 50);
	}
	
	public void bind() throws IOException {
		if (bound) throw new IOException("Already bound!");
		sockfd = CLib.INSTANCE.socket(1, 1, 0);
		if (sockfd < 0) throw new IOException("Socket failed to be created!");
		byte[] fb = file.getBytes();
		sockaddr_un sau = new sockaddr_un();
		sau.sunpath = fb;
		sau.sunfamily = 1;
		int bind = CLib.INSTANCE.bind(sockfd, sau, fb.length + 2);
		if (bind != 0) throw new IOException("Socket failed to bind!");
		int listen = CLib.INSTANCE.listen(sockfd, 50);
		if (bind != 0) throw new IOException("Socket failed to listen!");
		bound = true;
	}
	
	public UnixSocket accept() throws IOException {
		if (!bound) bind();
		sockaddr_un remote = new sockaddr_un();
		int nsfd = CLib.INSTANCE.accept(sockfd, remote, new IntByReference(110));
		UnixSocket us = new UnixSocket(new String(remote.sunpath), nsfd);
		return us;
	}
	
	public void close() throws IOException {
		closed = true;
		int s = CLib.INSTANCE.close(sockfd);
		if (s < 0) throw new IOException("Closing failed!");
	}
}
