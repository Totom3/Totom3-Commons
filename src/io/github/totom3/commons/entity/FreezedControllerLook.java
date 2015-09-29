package io.github.totom3.commons.entity;

import net.minecraft.server.v1_8_R3.ControllerLook;
import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.EntityInsentient;

/**
 *
 * @author Totom3
 */
public class FreezedControllerLook extends ControllerLook {
    private boolean freezed;

    public FreezedControllerLook(EntityInsentient entity) {
	super(entity);
    }
    
    public FreezedControllerLook(EntityInsentient entity, boolean freezed) {
	super(entity);
	this.freezed = freezed;
    }

    @Override
    public void a() {
	if (!freezed) {
	    super.a();
	}
    }

    public boolean isFreezed() {
	return freezed;
    }
    
    public void setFreezed(boolean freezed) {
	this.freezed = freezed;
    }
    
    @Override
    public void a(double x, double y, double z, float yaw, float pitch) {
	super.a(x, y, z, yaw, pitch);
    }

    @Override
    public void a(Entity entity, float yaw, float pitch) {
	super.a(entity, yaw, pitch);
    }

    public double getX() {
	return e();
    }
    
    public double getY() {
	return f();
    }
    
    public double getZ() {
	return g();
    }
    
    @Override
    @Deprecated
    public double e() {
	return super.e();
    }

    @Override
    @Deprecated
    public double f() {
	return super.f();
    }

    @Override
    @Deprecated
    public double g() {
	return super.g();
    }

    @Override
    @Deprecated
    public boolean b() {
	return super.b();
    }

}
