package io.github.totom3.commons.binary;

import io.github.totom3.commons.bukkit.AbstractLocation;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 *
 * @author Totom3
 */
public class BaseLocationAdapter {

    static byte NO_X = 0x1;
    static byte NO_Y = 0x2;
    static byte NO_Z = 0x4;
    static byte NO_PITCH = 0x08;
    static byte NO_YAW = 0x10;

    protected AbstractLocation doRead(DataInput in) throws IOException {
	byte header = in.readByte();

	double x = 0, y = 0, z = 0;
	float pitch = 0, yaw = 0;

	if ((header & NO_X) == 0) {
	    x = in.readDouble();
	}
	if ((header & NO_Y) == 0) {
	    y = in.readDouble();
	}
	if ((header & NO_Z) == 0) {
	    z = in.readDouble();
	}
	if ((header & NO_PITCH) == 0) {
	    pitch = in.readFloat();
	}
	if ((header & NO_YAW) == 0) {
	    yaw = in.readFloat();
	}

	return AbstractLocation.create(x, y, z, pitch, yaw);
    }

    protected void doWrite(DataOutput out, double x, double y, double z, float pitch, float yaw) throws IOException {
	byte header = makeHeader(x, y, z, pitch, yaw);
	out.writeByte(header);

	if ((header & NO_X) == 0) {
	    out.writeDouble(x);
	}
	if ((header & NO_Y) == 0) {
	    out.writeDouble(y);
	}
	if ((header & NO_Z) == 0) {
	    out.writeDouble(z);
	}
	if ((header & NO_PITCH) == 0) {
	    out.writeFloat(pitch);
	}
	if ((header & NO_YAW) == 0) {
	    out.writeFloat(yaw);
	}
    }

    protected byte makeHeader(double x, double y, double z, float pitch, float yaw) {
	byte head = 0;

	if (x == 0) {
	    head |= NO_X;
	}
	if (y == 0) {
	    head |= NO_Y;
	}
	if (z == 0) {
	    head |= NO_Z;
	}
	if (pitch == 0) {
	    head |= NO_PITCH;
	}
	if (yaw == 0) {
	    head |= NO_YAW;
	}

	return head;
    }
}
