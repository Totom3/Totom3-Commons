package io.github.totom3.commons.binary;

import java.io.IOException;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

/**
 *
 * @author Totom3
 */
public class LocationAdapter extends BaseLocationAdapter implements BinaryAdapter<Location> {

    @Override
    public Location read(DeserializationContext context) throws IOException {
	String worldName = context.readString();

	World world = null;
	if (worldName != null) {
	    world = Bukkit.getWorld(worldName);
	    if (world == null) {
		throw new DeserializingException("No such world " + worldName);
	    }
	}

	return doRead(context.in()).forWorld(world);
    }

    @Override
    public void write(Location loc, SerializationContext context) throws IOException {
	String worldName = (loc.getWorld() == null) ? null : loc.getWorld().getName();
	context.writeString(worldName);

	doWrite(context.out(), loc.getX(), loc.getY(), loc.getZ(), loc.getPitch(), loc.getYaw());
    }
}
