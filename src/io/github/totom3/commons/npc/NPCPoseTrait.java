package io.github.totom3.commons.npc;

import net.citizensnpcs.api.exception.NPCLoadException;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.util.DataKey;

/**
 *
 * @author Totom3
 */
public class NPCPoseTrait extends Trait {

    private NPCPose pose;

    public NPCPoseTrait() {
	super("npcpose");
	pose = NPCPose.STANDING;
    }

    public boolean isSitting() {
	return pose == NPCPose.SITTING;
    }

    public boolean isSleeping() {
	return pose == NPCPose.SLEEPING;
    }

    public boolean isStanding() {
	return pose == NPCPose.STANDING;
    }

    public NPCPose getPose() {
	return pose;
    }

    public void sitDown() {
	checkNPC();
	SitDownManager.get().sitDown(npc);
    }

    public void standUp() {
	checkNPC();
	
	pose = NPCPose.STANDING;
	
	if (!npc.isSpawned()) {
	    return;
	}

	if (pose == NPCPose.SITTING) {
	    SitDownManager.get().standUp(npc);
	} else if (pose == NPCPose.SLEEPING) {
	    SleepManager.get().wakeUp(npc);
	}
    }

    // Called by SitDownManager
    void setSitting() {
	pose = NPCPose.SITTING;
    }

    void setStanding() {
	pose = NPCPose.STANDING;
    }

    void setSleeping() {
	pose = NPCPose.SLEEPING;
    }
    
    @Override
    public void onSpawn() {
	if (pose == NPCPose.SITTING) {
	    SitDownManager.get().sitDown(npc);
	} else if (pose == NPCPose.SLEEPING) {
	    SleepManager.get().sleep(npc);
	}
    }

    @Override
    public void onDespawn() {	
	if (pose == NPCPose.SITTING) {
	    SitDownManager.get().standUp(npc);
	    pose = NPCPose.SITTING;
	} else if (pose == NPCPose.SLEEPING) {
	    SleepManager.get().wakeUp(npc);
	    pose = NPCPose.SLEEPING;
	}
    }

    @Override
    public void onRemove() {
	if (npc.isSpawned()) {
	    SitDownManager.get().standUp(npc);
	    SleepManager.get().wakeUp(npc);
	}
    }

    @Override
    public void save(DataKey key) {
	key.setString("pose", pose.name());
    }

    @Override
    public void load(DataKey key) throws NPCLoadException {
	String poseName = key.getString("pose", "<null>");
	try {
	    this.pose = NPCPose.valueOf(poseName);
	} catch (IllegalArgumentException ex) {
	    NPCLoadException ex2 = new NPCLoadException("read invalid pose '" + poseName + "'; expected STANDING, SITTING or SLEEPING");
	    ex2.printStackTrace();
	    throw ex2;
	}
    }

    private void checkNPC() throws IllegalStateException {
	if (npc == null) {
	    throw new IllegalStateException("Not linked to NPC");
	}
    }

    

    public static enum NPCPose {

	STANDING,
	SITTING,
	SLEEPING;
    }
}
