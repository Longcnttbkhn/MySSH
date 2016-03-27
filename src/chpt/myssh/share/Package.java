package chpt.myssh.share;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;

public class Package implements Serializable {
	private int length;
	private byte[] buffer;

	public Package(int length, byte[] buffer) {
		this.length = length;
		this.buffer = buffer;
	}

	public int getLength() {
		return length;
	}

	public byte[] getBuffer() {
		return buffer;
	}

}
