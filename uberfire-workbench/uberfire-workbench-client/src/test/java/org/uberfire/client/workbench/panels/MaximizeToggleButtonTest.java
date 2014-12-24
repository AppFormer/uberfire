package org.uberfire.client.workbench.panels;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mvp.Command;

@RunWith(MockitoJUnitRunner.class)
public class MaximizeToggleButtonTest {

    MaximizeToggleButtonPresenter maximizeButton;

    @Mock Command maximizeCommand;
    @Mock Command unmaximizeCommand;
    @Mock MaximizeToggleButtonPresenter.View view;

    @Before
    public void setup() throws Exception {
        maximizeButton = new MaximizeToggleButtonPresenter( view );
        maximizeButton.setMaximizeCommand( maximizeCommand );
        maximizeButton.setUnmaximizeCommand( unmaximizeCommand );
    }

    @Test
    public void testMaximizeWhenClicked() throws Exception {
        maximizeButton.handleClick();

        verify( view ).setMaximized( true );
        assertTrue( maximizeButton.isMaximized() );
        verify( maximizeCommand ).execute();
        verify( unmaximizeCommand, never() ).execute();
    }

    @Test
    public void testUnaximizeWhenClickedAgain() throws Exception {
        maximizeButton.handleClick();
        maximizeButton.handleClick();
        assertFalse( maximizeButton.isMaximized() );
        verify( maximizeCommand ).execute();
        verify( unmaximizeCommand ).execute();
    }

    @Test
    public void testSetMaximizedDoesNotInvokeCommands() throws Exception {
        maximizeButton.setMaximized( true );
        maximizeButton.setMaximized( false );

        assertFalse( maximizeButton.isMaximized() );
        verify( maximizeCommand, never() ).execute();
        verify( unmaximizeCommand, never() ).execute();
    }

    @Test
    public void testSetMaximizedFromCallbackIsSafe() throws Exception {
        maximizeButton.setMaximizeCommand( new Command() {
            @Override
            public void execute() {
                maximizeButton.setMaximized( true );
            }
        } );

        maximizeButton.handleClick();

        assertTrue( maximizeButton.isMaximized() );
        verify( maximizeCommand, never() ).execute();
        verify( unmaximizeCommand, never() ).execute();
    }

}
