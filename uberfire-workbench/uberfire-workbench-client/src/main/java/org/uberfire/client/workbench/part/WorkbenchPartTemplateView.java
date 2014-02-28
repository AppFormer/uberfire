package org.uberfire.client.workbench.part;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import org.uberfire.client.workbench.Workbench;

@Dependent
@Named("WorkbenchPartTemplateView")
public class WorkbenchPartTemplateView
        extends Composite
        implements WorkbenchPartPresenter.View {

    private WorkbenchPartPresenter presenter;

    private final FlowPanel content = new FlowPanel();

    @Inject
    private Workbench workbench;

    public WorkbenchPartTemplateView() {
        initWidget( content );
    }

    @Override
    public void init( WorkbenchPartPresenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public WorkbenchPartPresenter getPresenter() {
        return this.presenter;
    }

    @Override
    public void setWrappedWidget( final IsWidget widget ) {
        content.add( widget );
    }

    @Override
    public IsWidget getWrappedWidget() {
        return content;
    }

    @Override
    public void onResize() {
    }
}