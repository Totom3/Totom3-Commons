package io.github.totom3.commons.entity;

import net.minecraft.server.v1_8_R3.ControllerJump;
import net.minecraft.server.v1_8_R3.EntityInsentient;

/**
 *
 * @author Totom3
 */
public class FreezedControllerJump extends ControllerJump {

    private boolean freezed;
    
    public FreezedControllerJump(EntityInsentient entity) {
	super(entity);
    }

    public FreezedControllerJump(EntityInsentient entity, boolean freezed) {
	super(entity);
	this.freezed = freezed;
    }
    
    public boolean isFreezed() {
	return freezed;
    }

    public void setFreezed(boolean freezed) {
	this.freezed = freezed;
    }

    public boolean willJump() {
	return a;
    }
    
    public void setJump(boolean jump) {
	this.a = jump;
    }

    @Override
    public void b() {
	if (!freezed) {
	    super.b();
	}
    }

    @Override
    @Deprecated
    public void a() {
	super.a();
    }
}
