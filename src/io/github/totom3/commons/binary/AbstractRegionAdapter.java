package io.github.totom3.commons.binary;

import io.github.totom3.commons.bukkit.AbstractLocation;
import io.github.totom3.commons.bukkit.AbstractRegion;
import java.io.IOException;

/**
 *
 * @author Totom3
 */
public class AbstractRegionAdapter implements BinaryAdapter<AbstractRegion>{

    @Override
    public AbstractRegion read(DeserializationContext context) throws IOException {
	AbstractLocation first = context.readObject(AbstractLocation.class);
	AbstractLocation second = context.readObject(AbstractLocation.class);
	
	return new AbstractRegion(first, second);
    }

    @Override
    public void write(AbstractRegion obj, SerializationContext context) throws IOException {
	context.writeObject(obj.getFirst());
	context.writeObject(obj.getSecond());
    }
}
