package io.github.totom3.commons.entity;

import net.minecraft.server.v1_8_R3.ControllerMove;
import net.minecraft.server.v1_8_R3.EntityInsentient;

/**
 *
 * @author Totom3
 */
public class FreezedControllerMove extends ControllerMove {

    private boolean freezed;

    public FreezedControllerMove(EntityInsentient entity) {
	super(entity);
    }

    public FreezedControllerMove(EntityInsentient entityinsentient, boolean freezed) {
	super(entityinsentient);
	this.freezed = freezed;
    }

    @Override
    @Deprecated
    public final void c() {
	if (!freezed) {
	    super.c();
	}
    }

    public boolean isFreezed() {
	return freezed;
    }

    public void setFreezed(boolean freezed) {
	this.freezed = freezed;
    }

    public double getX() {
	return b;
    }

    public double getY() {
	return c;
    }

    public double getZ() {
	return d;
    }

    public double getMovSpeedMult() {
	return e;
    }

    @Override
    @Deprecated
    public double f() {
	return super.f();
    }

    @Override
    @Deprecated
    public double e() {
	return super.e();
    }

    @Override
    @Deprecated
    public double d() {
	return super.d();
    }

    @Override
    @Deprecated
    public double b() {
	return super.b();
    }

    @Override
    @Deprecated
    public boolean a() {
	return super.a();
    }

}
