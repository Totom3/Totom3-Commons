package com.bergerkiller.bukkit.common.utils;

import java.util.EnumMap;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

public class FaceUtils {

    private static final BlockFace[] AXIS = new BlockFace[4];
    private static final BlockFace[] RADIAL = {BlockFace.WEST, BlockFace.NORTH_WEST, BlockFace.NORTH, BlockFace.NORTH_EAST, BlockFace.EAST, BlockFace.SOUTH_EAST, BlockFace.SOUTH, BlockFace.SOUTH_WEST};
    private static final EnumMap<BlockFace, Integer> notches = new EnumMap<>(BlockFace.class);
    private static final double HALFROOTOFTWO = Math.sqrt(2) / 2;

    static {
	for (int i = 0; i < RADIAL.length; i++) {
	    notches.put(RADIAL[i], i);
	}
	for (int i = 0; i < AXIS.length; i++) {
	    AXIS[i] = RADIAL[i << 1];
	}
    }

    /**
     * Gets the Notch integer representation of a BlockFace<br>
     * <b>These are the horizontal faces, which exclude up and down</b>
     * <p>
     * @param face to get
     * <p>
     * @return Notch of the face
     */
    public static int faceToNotch(BlockFace face) {
	Integer notch = notches.get(face);
	return notch == null ? 0 : notch;
    }

    /**
     * Checks whether a given face is an offset along the X-axis
     * <p>
     * @param face to check
     * <p>
     * @return True if it is along the X-axis, False if not
     */
    public static boolean isAlongX(BlockFace face) {
	return face.getModX() != 0 && face.getModZ() == 0;
    }

    /**
     * Checks whether a given face is an offset along the Y-axis
     * <p>
     * @param face to check
     * <p>
     * @return True if it is along the Y-axis, False if not
     */
    public static boolean isAlongY(BlockFace face) {
	return isVertical(face);
    }

    /**
     * Checks whether a given face is an offset along the Z-axis
     * <p>
     * @param face to check
     * <p>
     * @return True if it is along the Z-axis, False if not
     */
    public static boolean isAlongZ(BlockFace face) {
	return face.getModZ() != 0 && face.getModX() == 0;
    }

    /**
     * Gets the Block Face at the notch index specified<br>
     * <b>These are the horizontal faces, which exclude up and down</b>
     * <p>
     * @param notch to get
     * <p>
     * @return BlockFace of the notch
     */
    public static BlockFace notchToFace(int notch) {
	return RADIAL[notch & 0x7];
    }

    /**
     * Rotates a given Block Face horizontally
     * <p>
     * @param from       face
     * @param notchCount to rotate at
     * <p>
     * @return rotated face
     */
    public static BlockFace rotate(BlockFace from, int notchCount) {
	return notchToFace(faceToNotch(from) + notchCount);
    }

    /**
     * Combines two non-subcardinal faces into one face<br>
     * - NORTH and WEST returns NORTH_WEST<br>
     * - NORTH and SOUTH returns NORTH (not possible to combine)
     * <p>
     * @param from face to combined
     * @param to   face to combined
     * <p>
     * @return the combined face
     */
    public static BlockFace combine(BlockFace from, BlockFace to) {
	if (from == BlockFace.NORTH) {
	    if (to == BlockFace.WEST) {
		return BlockFace.NORTH_WEST;
	    } else if (to == BlockFace.EAST) {
		return BlockFace.NORTH_EAST;
	    }
	} else if (from == BlockFace.EAST) {
	    if (to == BlockFace.NORTH) {
		return BlockFace.NORTH_EAST;
	    } else if (to == BlockFace.SOUTH) {
		return BlockFace.SOUTH_EAST;
	    }
	} else if (from == BlockFace.SOUTH) {
	    if (to == BlockFace.WEST) {
		return BlockFace.SOUTH_WEST;
	    } else if (to == BlockFace.EAST) {
		return BlockFace.SOUTH_EAST;
	    }
	} else if (from == BlockFace.WEST) {
	    if (to == BlockFace.NORTH) {
		return BlockFace.NORTH_WEST;
	    } else if (to == BlockFace.SOUTH) {
		return BlockFace.SOUTH_WEST;
	    }
	}
	return from;
    }

    /**
     * Subtracts two faces
     * <p>
     * @param face1
     * @param face2 to subtract from face1
     * <p>
     * @return Block Face result ofthe subtraction
     */
    public static BlockFace subtract(BlockFace face1, BlockFace face2) {
	return notchToFace(faceToNotch(face1) - faceToNotch(face2));
    }

    /**
     * Adds two faces together
     * <p>
     * @param face1
     * @param face2 to add to face1
     * <p>
     * @return Block Face result of the addition
     */
    public static BlockFace add(BlockFace face1, BlockFace face2) {
	return notchToFace(faceToNotch(face1) + faceToNotch(face2));
    }

    /**
     * Gets all the individual faces represented by a Block Face<br>
     * - NORTH_WEST returns NORTH and WEST<br>
     * - NORTH returns NORTH and SOUTH<br>
     * <p>
     * @param main face to get the faces for
     * <p>
     * @return an array of length 2 containing all the faces
     */
    public static BlockFace[] getFaces(BlockFace main) {
	switch (main) {
	    case SOUTH_EAST:
		return new BlockFace[]{BlockFace.SOUTH, BlockFace.EAST};
	    case SOUTH_WEST:
		return new BlockFace[]{BlockFace.SOUTH, BlockFace.WEST};
	    case NORTH_EAST:
		return new BlockFace[]{BlockFace.NORTH, BlockFace.EAST};
	    case NORTH_WEST:
		return new BlockFace[]{BlockFace.NORTH, BlockFace.WEST};
	    default:
		return new BlockFace[]{main, main.getOppositeFace()};
	}
    }

    /**
     * Gets the direction a minecart faces when on a given track
     * <p>
     * @param raildirection of the rails
     * <p>
     * @return minecart direction
     */
    public static BlockFace getRailsCartDirection(final BlockFace raildirection) {
	switch (raildirection) {
	    case NORTH_EAST:
	    case SOUTH_WEST:
		return BlockFace.NORTH_WEST;
	    case NORTH_WEST:
	    case SOUTH_EAST:
		return BlockFace.SOUTH_WEST;
	    default:
		return raildirection;
	}
    }

    /**
     * Gets the rail direction from a Direction<br>
     * NORTH becomes SOUTH and WEST becomes EAST
     * <p>
     * @param direction to convert
     * <p>
     * @return rail direction
     */
    public static BlockFace toRailsDirection(BlockFace direction) {
	switch (direction) {
	    case NORTH:
		return BlockFace.SOUTH;
	    case WEST:
		return BlockFace.EAST;
	    default:
		return direction;
	}
    }

    /**
     * Gets whether a given Block Face is sub-cardinal (such as NORTH_WEST)
     * <p>
     * @param face to check
     * <p>
     * @return True if sub-cardinal, False if not
     */
    public static boolean isSubCardinal(final BlockFace face) {
	switch (face) {
	    case NORTH_EAST:
	    case SOUTH_EAST:
	    case SOUTH_WEST:
	    case NORTH_WEST:
		return true;
	    default:
		return false;
	}
    }

    /**
     * Checks whether a face is up or down
     * <p>
     * @param face to check
     * <p>
     * @return True if it is UP or DOWN
     */
    public static boolean isVertical(BlockFace face) {
	return face == BlockFace.UP || face == BlockFace.DOWN;
    }

    /**
     * Gets the BlockFace.UP or BlockFace.DOWN constant based on the up
     * parameter
     * <p>
     * @param up parameter
     * <p>
     * @return UP if up is true, DOWN if up is false
     */
    public static BlockFace getVertical(boolean up) {
	return up ? BlockFace.UP : BlockFace.DOWN;
    }

    /**
     * Gets the BlockFace.UP or BlockFace.DOWN based on the delta-y parameter
     * <p>
     * @param dy parameter
     * <p>
     * @return UP if dy >= 0, DOWN if dy < 0
     */
    public static BlockFace getVertical(double dy) {
	return getVertical(dy >= 0.0);
    }

    /**
     * Gets the Vector direction from a Block Face
     * <p>
     * @param face   to use
     * @param length of the vector
     * <p>
     * @return Vector of the direction and length
     */
    public static Vector faceToVector(BlockFace face, double length) {
	return faceToVector(face).multiply(length);
    }

    /**
     * Gets the Vector direction from a Block Face
     * <p>
     * @param face to use
     * <p>
     * @return Vector of the direction and length 1
     */
    public static Vector faceToVector(BlockFace face) {
	return new Vector(face.getModX(), face.getModY(), face.getModZ());
    }

    /**
     * Gets the co-sinus value from a Block Face treated as an Angle
     * <p>
     * @param face to get the co-sinus value from
     * <p>
     * @return co-sinus value
     */
    public static double cos(final BlockFace face) {
	switch (face) {
	    case SOUTH_WEST:
	    case NORTH_WEST:
		return -HALFROOTOFTWO;
	    case SOUTH_EAST:
	    case NORTH_EAST:
		return HALFROOTOFTWO;
	    case EAST:
		return 1;
	    case WEST:
		return -1;
	    default:
		return 0;
	}
    }

    /**
     * Gets the sinus value from a Block Face treated as an Angle
     * <p>
     * @param face to get the sinus value from
     * <p>
     * @return sinus value
     */
    public static double sin(final BlockFace face) {
	switch (face) {
	    case NORTH_EAST:
	    case NORTH_WEST:
		return -HALFROOTOFTWO;
	    case SOUTH_WEST:
	    case SOUTH_EAST:
		return HALFROOTOFTWO;
	    case NORTH:
		return -1;
	    case SOUTH:
		return 1;
	    default:
		return 0;
	}
    }

    /**
     * Gets the horizontal Block Face from a given yaw angle<br>
     * This includes the NORTH_WEST faces
     * <p>
     * @param yaw angle
     * <p>
     * @return The Block Face of the angle
     */
    public static BlockFace yawToFace(float yaw) {
	return yawToFace(yaw, true);
    }

    /**
     * Gets the horizontal Block Face from a given yaw angle
     * <p>
     * @param yaw                      angle
     * @param useSubCardinalDirections setting, True to allow NORTH_WEST to be
     *                                 returned
     * <p>
     * @return The Block Face of the angle
     */
    public static BlockFace yawToFace(float yaw, boolean useSubCardinalDirections) {
	if (useSubCardinalDirections) {
	    return RADIAL[Math.round(yaw / 45f) & 0x7];
	} else {
	    return AXIS[Math.round(yaw / 90f) & 0x3];
	}
    }

    private FaceUtils() {
    }
}
