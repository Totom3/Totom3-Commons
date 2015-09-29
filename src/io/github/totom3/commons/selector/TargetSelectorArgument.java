package io.github.totom3.commons.selector;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

/**
 *
 * @author Totom3
 */
public enum TargetSelectorArgument {

    COUNT("c"),

    CENTER("center"),

    // range and min_range are not testable for performance reasons
    RANGE("r"),

    MIN_RANGE("rm"),

    LEVEL("l", /*<editor-fold defaultstate="collapsed" desc="Lambda">*/
	  (d) -> {
	      int lvl = d.getData(Number.class).intValue();
	      Entity ent = d.getTested();

	      if (!(ent instanceof Player)) {
		  return false;
	      }

	      return ((Player) ent).getLevel() <= lvl;
	  } /*</editor-fold>*/),

    MIN_LEVEL("lm", /*<editor-fold defaultstate="collapsed" desc="Lambda">*/
	      (d) -> {
		  int lvl = d.getData(Number.class).intValue();
		  Entity ent = d.getTested();

		  if (!(ent instanceof Player)) {
		      return false;
		  }

		  return ((Player) ent).getLevel() >= lvl;
	      } /*</editor-fold>*/),

    X("x"),

    Y("y"),

    Z("z"),

    BOX_X("dx", /*<editor-fold defaultstate="collapsed" desc="Lambda">*/
	  (d) -> {
	      double a = d.getOrigin().getX();
	      double b = d.getData(Number.class).doubleValue() + a;
	      double x = d.getTested().getLocation().getX();

	      if (x == a) {
		  return true;
	      }

	      return (x > a) ? x <= b : x >= b;
	  }
    /*</editor-fold>*/),

    BOX_Y("dy", /*<editor-fold defaultstate="collapsed" desc="Lambda">*/
	  (d) -> {
	      double a = d.getOrigin().getY();
	      double b = d.getData(Number.class).doubleValue() + a;
	      double y = d.getTested().getLocation().getY();

	      if (y == a) {
		  return true;
	      }

	      return (y > a) ? y <= b : y >= b;
	  }
    /*</editor-fold>*/),

    BOX_Z("dz", /*<editor-fold defaultstate="collapsed" desc="Lambda">*/
	  (d) -> {
	      double a = d.getOrigin().getZ();
	      double b = d.getData(Number.class).doubleValue() + a;
	      double z = d.getTested().getLocation().getZ();

	      if (z == a) {
		  return true;
	      }

	      return (z > a) ? z <= b : z >= b;
	  }
    /*</editor-fold>*/),

    VERT_ROTATION("rx", /*<editor-fold defaultstate="collapsed" desc="Lambda">*/
		  (d) -> {
		      float pitch = d.getData(Number.class).floatValue();
		      Entity ent = d.getTested();

		      return ent.getLocation().getPitch() <= pitch;
		  } /*</editor-fold>*/),

    MIN_VERT_ROTATION("rxm", /*<editor-fold defaultstate="collapsed" desc="Lambda">*/
		      (d) -> {
			  float pitch = d.getData(Number.class).floatValue();
			  Entity ent = d.getTested();

			  return ent.getLocation().getPitch() >= pitch;
		      } /*</editor-fold>*/),

    HORIZ_ROTATION("ry", /*<editor-fold defaultstate="collapsed" desc="Lambda">*/
		   (d) -> {
		       float yaw = d.getData(Number.class).floatValue();
		       Entity ent = d.getTested();

		       return ent.getLocation().getYaw() <= yaw;
		   } /*</editor-fold>*/),

    MIN_HORIZ_ROTATION("rym", //<editor-fold defaultstate="collapsed" desc="Lambda">
		       (d) -> {
			   float yaw = d.getData(Number.class).floatValue();
			   Entity ent = d.getTested();

			   return ent.getLocation().getYaw() >= yaw;
		       } //</editor-fold>
    ),

    GAMEMODE("m", //<editor-fold defaultstate="collapsed" desc="Lambda">
	     (d) -> {
		 int gamemode = d.getData(Number.class).intValue();
		 Entity ent = d.getTested();

		 if (gamemode == -1) {
		     return true;
		 }

		 return (ent instanceof HumanEntity) && ((HumanEntity) ent).getGameMode().getValue() == gamemode;
	     }
    //</editor-fold>
    ),

    TEAM("team", //<editor-fold defaultstate="collapsed" desc="Lambda">
	 (d) -> {

	     String rawTeamName = d.getData(String.class);
	     Entity ent = d.getTested();
	     Scoreboard sb = Bukkit.getScoreboardManager().getMainScoreboard();

	     if (rawTeamName.isEmpty()) {
		 OfflinePlayer p = new OfflinePlayer() {

		     @Override
		     public boolean isOnline() {
			 throw new UnsupportedOperationException();
		     }

		     @Override
		     public String getName() {
			 if (ent instanceof HumanEntity) {
			     return ent.getName();
			 } else {
			     return ent.getUniqueId().toString();
			 }
		     }

		     @Override
		     public UUID getUniqueId() {
			 throw new UnsupportedOperationException();
		     }

		     @Override
		     public boolean isBanned() {
			 throw new UnsupportedOperationException();
		     }

		     @Override
		     public void setBanned(boolean banned) {
			 throw new UnsupportedOperationException();
		     }

		     @Override
		     public boolean isWhitelisted() {
			 throw new UnsupportedOperationException();
		     }

		     @Override
		     public void setWhitelisted(boolean value) {
			 throw new UnsupportedOperationException();
		     }

		     @Override
		     public Player getPlayer() {
			 throw new UnsupportedOperationException();
		     }

		     @Override
		     public long getFirstPlayed() {
			 throw new UnsupportedOperationException();
		     }

		     @Override
		     public long getLastPlayed() {
			 throw new UnsupportedOperationException();
		     }

		     @Override
		     public boolean hasPlayedBefore() {
			 throw new UnsupportedOperationException();
		     }

		     @Override
		     public Location getBedSpawnLocation() {
			 throw new UnsupportedOperationException();
		     }

		     @Override
		     public boolean isOp() {
			 throw new UnsupportedOperationException();
		     }

		     @Override
		     public void setOp(boolean value) {
			 throw new UnsupportedOperationException();
		     }

		     @Override
		     public Map<String, Object> serialize() {
			 throw new UnsupportedOperationException();
		     }

		 };

		 return sb.getPlayerTeam(p) == null;
	     }

	     String teamName;
	     boolean negative;

	     if (rawTeamName.startsWith("!")) {
		 negative = true;
		 teamName = rawTeamName.substring(1);
	     } else {
		 negative = false;
		 teamName = rawTeamName;
	     }

	     Set<String> players = sb.getTeam(teamName).getEntries();

	     return negative ^ (players.contains(ent.getName()) || players.contains(ent.getUniqueId().toString()));

	 }//</editor-fold>
    ),

    NAME("name", //<editor-fold defaultstate="collapsed" desc="Lambda">
	 (d) -> {
	     String rawName = d.getData(String.class);
	     Entity ent = d.getTested();

	     String name;
	     boolean negative;

	     if (rawName.startsWith("!")) {
		 negative = true;
		 name = rawName.substring(1);
	     } else {
		 negative = false;
		 name = rawName;
	     }

	     // Same as: if negative then not equals, else equals
	     String customName = ent.getCustomName();
	     return negative ^ (ent.getName().equals(name) || (customName != null && customName.equals(name)));
	 }
    //</editor-fold>
    ),

    TYPE("type", //<editor-fold defaultstate="collapsed" desc="Lambda">
	 (d) -> {
	     EntityType type;
	     boolean negate = false;
	     String s = d.getData(String.class);
	     if (s.startsWith("!")) {
		 negate = true;
		 s = s.substring(1);
	     }
	     try {
		 type = EntityType.valueOf(s);
	     } catch (IllegalArgumentException ex) {
		 return negate;
	     }

	     Entity ent = d.getTested();

	     // Same as: if negative then not equals, else equals
	     return negate ^ ent.getType() == type;
	 }
    //</editor-fold>
    );

    private final static Map<String, TargetSelectorArgument> BY_NAME;

    static {
	BY_NAME = new HashMap<>();

	for (TargetSelectorArgument arg : values()) {
	    BY_NAME.put(arg.getName(), arg);
	}
    }

    public static final TargetSelectorArgument getByName(String name) {
	if (name == null) {
	    throw new NullPointerException();
	}

	TargetSelectorArgument arg = BY_NAME.get(name);
	if (arg == null) {
	    throw new IllegalArgumentException("argument '" + name + "' does not exist");
	}

	return arg;
    }

    private final String name;
    private final Predicate<TargetData> tester;

    private TargetSelectorArgument(String name) {
	this.name = name;
	this.tester = null;
    }

    private TargetSelectorArgument(String name, Predicate<TargetData> tester) {
	this.name = name;
	this.tester = tester;
    }

    public boolean isTestable() {
	return tester != null;
    }

    public boolean test(TargetData data) throws IllegalArgumentException {
	if (!isTestable()) {
	    throw new IllegalArgumentException("Argument " + this + " is not testable.");
	}

	return tester.test(data);
    }

    public String getName() {
	return name;
    }

    @Override
    public String toString() {
	return getName();
    }

    public static class TargetData {

	private final Object data;
	private final Entity tested;
	private final Location origin;

	public TargetData(Object data, Entity tested, Location origin) {
	    this.data = checkNotNull(data);
	    this.tested = checkNotNull(tested);
	    this.origin = checkNotNull(origin);
	}

	public Object getData() {
	    return data;
	}

	public <T> T getData(Class<T> clazz) {
	    if (!clazz.isInstance(data)) {
		throw new ClassCastException("Expected "+clazz.getName()+"; got instead "+data.getClass().getName());
	    }
	    return clazz.cast(data);
	}

	public Entity getTested() {
	    return tested;
	}

	public Location getOrigin() {
	    return origin;
	}

	@Override
	public int hashCode() {
	    int hash = 3;
	    hash = 11 * hash + Objects.hashCode(this.data);
	    hash = 11 * hash + Objects.hashCode(this.tested);
	    hash = 11 * hash + Objects.hashCode(this.origin);
	    return hash;
	}

	@Override
	public boolean equals(Object obj) {
	    if (obj == null) {
		return false;
	    }
	    if (!(obj instanceof TargetData)) {
		return false;
	    }
	    TargetData other = (TargetData) obj;
	    return data.equals(other.data) && tested.equals(other.tested) && origin.equals(other.origin);
	}

	@Override
	public String toString() {
	    return "TargetData{" + "data=" + data + ", tested=" + tested + ", origin=" + origin + '}';
	}
    }
}
