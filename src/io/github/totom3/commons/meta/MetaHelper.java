package io.github.totom3.commons.meta;

import com.google.common.base.Preconditions;
import java.util.List;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.metadata.Metadatable;
import org.bukkit.plugin.java.JavaPlugin;

/** A utility class for the {@code Bukkit API}, helping the process of
 * getting and setting values from a {@code Metadatable} (an {@code Entity},
 * {@code Block}, {@code Player}, etc..).
 *
 * @author Totom3
 */
public class MetaHelper {
    /** 
     * The {@code Metadatable} object
     */
    private final Metadatable meta;
    
    /**
     * The {@code JavaPlugin} object. Required to set a {@code MetadataValue}.
     */
    private final JavaPlugin plugin;
    
    /** Constructs a new {@code MetaHelper} instance for the specified {@code Metadatable} and plugin.
     *
     * @param metadatable
     * @param plugin
     */
    public MetaHelper(Metadatable metadatable, JavaPlugin plugin) {
	this.plugin = Preconditions.checkNotNull(plugin, "Plugin cannot be null.");
	this.meta = Preconditions.checkNotNull(metadatable, "Metadatable cannot be null.");;
    }
    
    /** Returns whether or not the {@code Metadatable} instance has
     * a value set for the specified label.
     *
     * @param label the label of the {@code Metadata} to check.
     * @return {@code true} if a value was set for the specified label
     * (including {@code null} values), {@code false} otherwise.
     */
    public boolean contains(String label) {
	List<MetadataValue> list = meta.getMetadata(label);
	
	return list != null && !list.isEmpty();
    }
    
    /** Returns the Object set for the specified label.
     * If no value was set or if the value itself is {@code null},
     * {@code null} is returned. If the value is not a child class
     * of the specified one, {@code null} is also returned.
     *
     * @param <T> the type of the Object.
     * @param label the label of the Object in the {@code MetadataStore}.
     * @param clazz the {@code Class} of the required object. Set to {@code Object}'s
     * class for unknown type.
     * @return the object at the specified label in the {@code MetadataStore} of the
     * {@code Metadatable} associated with this {@code MetaHelper}, if it matches all
     * the conditions, {@code null} otherwise.
     * @see #getAmount(java.lang.String) 
     */
    public <T> T getObject(String label, Class<T> clazz) {
	List<MetadataValue> list = meta.getMetadata(label);
	
	if (list == null || list.isEmpty()) {
	    return null;
	}
	
	Object o = list.get(0);
	
	if (o == null) {
	    return null;
	}
	
	if (clazz != null && !clazz.isAssignableFrom(o.getClass())) {
	    return null;
	}
	
	return (T) o;
    }
    
    /** Sets the value of the object at the specified label.
     * This completely overrides any possible previous values set
     * for the same label.
     *
     * @param label the label under which the object is to be set
     * @param obj the object to set
     */
    public void setObject(String label, Object obj) {
	meta.setMetadata(label, new FixedMetadataValue(plugin, obj));
    }
    
    /** Returns the {@code Number} set for the specified label.
     * If no value was set for it, or if the value itself is {@code null},
     * then {@code 0} is returned.
     *
     * @param label the label of the {@code Number}.
     * @return the number associated with the specified label in the {@code Metadatable}'s {@code MetadataStore}.
     */
    public Number getAmount(String label) {
	List<MetadataValue> list = meta.getMetadata(label);
	
	if (list == null || list.isEmpty()) {
	    return 0;
	}
	
	if (!(list.get(0).value() instanceof Number)) {
	    return 0;
	}
	
	return (Number) list.get(0).value();
    }
    
    /** Adds the specified amount to the {@code Number} associated with the specified label.
     * If no value was previously set for the label, or if it was not a {@code Number}, then
     * it will be counted as a {@code 0} and this method will be the equivalent of calling
     * {@link MetaHelper#setAmount(java.lang.String, double)}.
     *
     * @param label the label of the old object to add
     * @param amount the amount to be added
     * @param decimal whether or not the transaction should be decimal. If set to {@code true},
     * both {@code Number}s will be casted to a {@code double} before added. If set to {@code false},
     * they will instead be casted to an {@code integer}.
     * @return the old value
     */
    public Number addAmount(String label, Number amount, boolean decimal) {
	Number old = getAmount(label);
	
	meta.setMetadata(label, new FixedMetadataValue(plugin, 
		(decimal) ? old.doubleValue() + amount.doubleValue() : old.intValue() + amount.intValue()));
	
	return old;
    }
    
    /** Sets the value of the {@code Number} at the specified label.
     * This completely overrides any possible previous values set
     * for the same label.
     *
     * @param label the label under which the object is to be set
     * @param amount the object to set
     */
    public void setAmount(String label, Number amount) {
	setObject(label, amount);
    }
    
    /** Returns the {@code String}, at the specified index. If
     * the object was not set or is {@code null}, {@code "null"}
     * is returned. If the object is not a {@code String}, it's
     * {@code String} representation will be called via {@link Object#toString()}.
     *
     * @param label the label of the {@code String} to get
     * @return the {@code String} at the specified index.
     */
    public String getString(String label) {
	List<MetadataValue> list = meta.getMetadata(label);
	
	if (list == null || list.isEmpty()) {
	    return "null";
	}
	
	Object o = list.get(0).value();
	
	if (o == null) {
	    return "null";
	}
	
	return o.toString();
    }
    
    /** Returns the {@code Metatable} object involved in this {@code MetaHelper}.
     *  
     * @return 
     */
    public Metadatable getMetadatable() {
	return meta;
    }
}
