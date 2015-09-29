package net.amoebaman.amoebautils.nms;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import org.bukkit.Bukkit;

/**
 * Contains static methods useful for utilizing reflection as a workaround and
 * alternative for directly importing NMS (net.minecraft.server) and OBC
 * (org.bukkit.craftbukkit) classes, as plugins importing those classes will
 * break with each update of Bukkit/CraftBukkit.
 *
 * @author AmoebaMan
 */
public class ReflectionUtil {

    public final static String VERSION;

    static {
	String name = Bukkit.getServer().getClass().getPackage().getName();
	VERSION = name.substring(name.lastIndexOf('.') + 1);
    }

    /**
     * Gets a NMS (net.minecraft.server) class by its name.
     *
     * @param className the name of the class to grab
     *
     * @return the class for the name
     *
     * @throws IllegalArgumentException if there are no such classes.
     */
    public static Class<?> getNMSClass(String className) throws IllegalArgumentException {
	String fullName = "net.minecraft.server." + VERSION + "." + className;
	Class<?> clazz = null;

	try {
	    clazz = Class.forName(fullName);
	} catch (ClassNotFoundException e) {
	    throw new IllegalArgumentException(e);
	}
	return clazz;
    }

    /**
     * Gets an OBC (org.bukkit.craftbukkit) class by its name, including any
     * package and subpackage names.
     *
     * @param className the name of the class to grab
     *
     * @return the class, or null if none was found
     *
     * @throws IllegalArgumentException
     */
    public static Class<?> getOBCClass(String className) throws IllegalArgumentException {
	String fullName = "org.bukkit.craftbukkit." + VERSION + "." + className;
	Class<?> clazz = null;
	try {
	    clazz = Class.forName(fullName);
	} catch (ClassNotFoundException e) {
	    throw new IllegalArgumentException(e);
	}
	return clazz;
    }

    /**
     * Invokes the getHandle() method (used by many CraftBukkit wrapper classes)
     * to get the NMS instance behind an object.
     *
     * @param obj the object in question
     *
     * @return the object's NMS handle, or null if none was found
     */
    public static Object getHandle(Object obj) {
	try {
	    return getMethod(obj.getClass(), "getHandle").invoke(obj);
	} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
	    return null;
	}
    }

    /**
     * Gets a field belonging to a class by its name.
     *
     * @param clazz a class
     * @param name  the name of a field within the class
     *
     * @return the field in question, or null if none was found
     */
    public static Field getField(Class<?> clazz, String name) {
	try {
	    Field field = clazz.getDeclaredField(name);
	    field.setAccessible(true);
	    return field;
	} catch (NoSuchFieldException | SecurityException e) {
	    return null;
	}
    }

    /**
     * Gets a method belonging to a class by its name, and optionally by its
     * parameter types if precision is required.
     *
     * @param clazz a class
     * @param name  the name of the desired method
     * @param args  the parameter types of the desired method, in order
     *
     * @return the method in question, or null if none was found
     */
    public static Method getMethod(Class<?> clazz, String name, Class<?>... args) {
	for (Method m : clazz.getMethods()) {
	    if (m.getName().equals(name) && (args.length == 0 || Arrays.equals(args, m.getParameterTypes()))) {
		m.setAccessible(true);
		return m;
	    }
	}
	return null;
    }

    private ReflectionUtil() {
    }
}
