package org.uberfire.workbench.model.menu.impl;

import java.util.ArrayList;
import java.util.Collection;

import org.uberfire.workbench.model.menu.EnabledStateChangeListener;
import org.uberfire.workbench.model.menu.MenuCustom;
import org.uberfire.workbench.model.menu.MenuPosition;
import org.uberfire.workbench.model.menu.MenuVisitor;

public abstract class BaseMenuCustom<T> implements MenuCustom<T> {

    private final String signatureId;
    private final Collection<String> traits = new ArrayList<String>();
    private final Collection<String> roles = new ArrayList<String>();
    private final String contributionPoint;
    private final String caption;
    private final MenuPosition position;
    private boolean enabled;

    protected BaseMenuCustom() {
        this( null, null, null, null, true );

    }

    protected BaseMenuCustom( boolean enabled ) {
        this( null, null, null, null, enabled );
    }

    protected BaseMenuCustom( final String signatureId ) {
        this( signatureId, null, null, null, true );
    }

    protected BaseMenuCustom( final String signatureId,
                              final String contributionPoint ) {
        this( signatureId, contributionPoint, null, null, true );
    }

    protected BaseMenuCustom( final String signatureId,
                              final String contributionPoint,
                              final String caption ) {
        this( signatureId, contributionPoint, caption, null, true );
    }

    public BaseMenuCustom( final String signatureId,
                           final String contributionPoint,
                           final String caption,
                           final MenuPosition position ) {
        this( signatureId, contributionPoint, caption, position, true );
    }

    public BaseMenuCustom( final String signatureId,
                           final String contributionPoint,
                           final String caption,
                           final MenuPosition position,
                           final boolean enabled ) {
        this.signatureId = signatureId;
        this.contributionPoint = contributionPoint;
        this.caption = caption;
        this.position = position;
        this.enabled = enabled;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled( boolean enabled ) {
        this.enabled = enabled;
    }

    @Override
    public String getContributionPoint() {
        return contributionPoint;
    }

    @Override
    public String getCaption() {
        return caption;
    }

    @Override
    public MenuPosition getPosition() {
        return position;
    }

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public void accept( final MenuVisitor visitor ) {
        visitor.visit( this );
    }

    @Override
    public void addEnabledStateChangeListener( final EnabledStateChangeListener listener ) {

    }

    @Override
    public String getSignatureId() {
        return signatureId;
    }

    @Override
    public Collection<String> getRoles() {
        return roles;
    }

    @Override
    public Collection<String> getTraits() {
        return traits;
    }
}
