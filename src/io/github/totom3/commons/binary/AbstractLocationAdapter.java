package io.github.totom3.commons.binary;

import io.github.totom3.commons.bukkit.AbstractLocation;
import java.io.IOException;

/**
 *
 * @author Totom3
 */
public class AbstractLocationAdapter extends BaseLocationAdapter implements BinaryAdapter<AbstractLocation> {

    @Override
    public AbstractLocation read(DeserializationContext context) throws IOException {
	return doRead(context.in());
    }

    @Override
    public void write(AbstractLocation obj, SerializationContext context) throws IOException {
	doWrite(context.out(), obj.getX(), obj.getY(), obj.getZ(), obj.getPitch(), obj.getYaw());
    }
}
