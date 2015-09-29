package io.github.totom3.commons.selector;

import com.google.common.base.Preconditions;

public class ImmutableTargetSelectorSettings extends TargetSelectorSettings {

    public static ImmutableTargetSelectorSettings wrap(TargetSelectorSettings settings) {
	Preconditions.checkNotNull(settings);
	
	if (settings instanceof ImmutableTargetSelectorSettings) {
	    return (ImmutableTargetSelectorSettings) settings;
	}
	
	return new ImmutableTargetSelectorSettings(settings);
    }

    public static ImmutableTargetSelectorSettings create(LimitType limitType, Boolean onlyPlayers, Integer defaultCount, boolean sortsEntities, char type) {
	return new ImmutableTargetSelectorSettings(limitType, onlyPlayers, defaultCount, sortsEntities, type);
    }

    private ImmutableTargetSelectorSettings(LimitType limitType, Boolean onlyPlayers, Integer defaultCount, boolean sortsEntities, char type) {
	super(limitType, onlyPlayers, defaultCount, sortsEntities, type);
    }

    private ImmutableTargetSelectorSettings(TargetSelectorSettings settings) {
	super(settings);
    }

    @Override
    public TargetSelectorSettings clone() {
	return ImmutableTargetSelectorSettings.wrap(this);
    }

    @Override
    public TargetSelectorSettings setType(char type) {
	throw new UnsupportedOperationException();
    }

    @Override
    public TargetSelectorSettings setSortsEntities(boolean sortsEntities) {
	throw new UnsupportedOperationException();
    }

    @Override
    public TargetSelectorSettings setDefaultCount(Integer defaultCount) {
	throw new UnsupportedOperationException();
    }

    @Override
    public TargetSelectorSettings setOnlyPlayers(Boolean onlyPlayers) {
	throw new UnsupportedOperationException();
    }

    @Override
    public TargetSelectorSettings setLimitType(LimitType limitType) {
	throw new UnsupportedOperationException();
    }

}
