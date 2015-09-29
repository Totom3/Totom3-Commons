package io.github.totom3.commons.selector;

/**
 *
 * @author Totom3
 */
public class TargetSelectorSettings implements Cloneable {

    public static final TargetSelectorSettings ALL_ENTITIES = ImmutableTargetSelectorSettings.create(LimitType.COUNT, false, null, true, 'e');
    
    public static final TargetSelectorSettings ALL_PLAYERS = ImmutableTargetSelectorSettings.create(LimitType.COUNT, true, null, false, 'a');
    
    public static final TargetSelectorSettings NEAREST_PLAYERS = ImmutableTargetSelectorSettings.create(LimitType.COUNT, true, 1, true, 'p');
    
    public static final TargetSelectorSettings RANDOM = ImmutableTargetSelectorSettings.create(LimitType.RANDOM, null, 1, false, 'r');
    
    private LimitType limitType;
    private Boolean onlyPlayers;
    private Integer defaultCount;
    private boolean sortsEntities;
    private char type;

    public TargetSelectorSettings(LimitType limitType, Boolean onlyPlayers, Integer defaultCount, boolean sortsEntities, char type) {
	this.limitType = limitType;
	this.onlyPlayers = onlyPlayers;
	this.defaultCount = defaultCount;
	this.sortsEntities = sortsEntities;
	this.type = type;
    }

    public TargetSelectorSettings() {
	this.type = 'e';
	this.onlyPlayers = false;
	this.sortsEntities = true;
	this.limitType = LimitType.COUNT;

    }

    public TargetSelectorSettings(TargetSelectorSettings settings) {
	this.type = settings.type;
	this.limitType = settings.limitType;
	this.onlyPlayers = settings.onlyPlayers;
	this.defaultCount = settings.defaultCount;
	this.sortsEntities = settings.sortsEntities;

    }

    public LimitType getLimitType() {
	return limitType;
    }

    public TargetSelectorSettings setLimitType(LimitType limitType) {
	this.limitType = limitType;
	return this;
    }

    public Boolean getOnlyPlayers() {
	return onlyPlayers;
    }

    public TargetSelectorSettings setOnlyPlayers(Boolean onlyPlayers) {
	this.onlyPlayers = onlyPlayers;
	return this;
    }

    public Integer getDefaultCount() {
	return defaultCount;
    }

    public TargetSelectorSettings setDefaultCount(Integer defaultCount) {
	this.defaultCount = defaultCount;
	return this;
    }

    public boolean sortsEntities() {
	return sortsEntities;
    }

    public TargetSelectorSettings setSortsEntities(boolean sortsEntities) {
	this.sortsEntities = sortsEntities;
	return this;
    }

    public char getType() {
	return type;
    }

    public TargetSelectorSettings setType(char type) {
	this.type = type;
	return this;
    }

    @Override
    public TargetSelectorSettings clone() {
	return new TargetSelectorSettings(this);
    }

    public static enum LimitType {

	COUNT,
	RANDOM;
    }
}
