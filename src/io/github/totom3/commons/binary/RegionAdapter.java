package io.github.totom3.commons.binary;

import io.github.totom3.commons.bukkit.Region;
import java.io.IOException;
import org.bukkit.Location;

/**
 *
 * @author Totom3
 */
public class RegionAdapter implements BinaryAdapter<Region> {

    @Override
    public Region read(DeserializationContext context) throws IOException {
	Location first = context.readObject(Location.class);
	Location second = context.readObject(Location.class);
	
	return new Region(first, second);
    }

    @Override
    public void write(Region obj, SerializationContext context) throws IOException {
	context.writeObject(obj.getFirst());
	context.writeObject(obj.getSecond());
    }
}
