package org.uberfire.ext.editor.commons.backend.service.restriction;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.jboss.errai.security.shared.api.identity.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.VFSLockService;
import org.uberfire.backend.vfs.impl.LockInfo;
import org.uberfire.ext.editor.commons.service.restriction.PathOperationRestriction;

@RunWith(MockitoJUnitRunner.class)
public class LockRestrictorTest {

    @Mock
    private VFSLockService lockService;

    @Mock
    private User identity;

    @InjectMocks
    private LockRestrictor lockRestrictor;

    @Mock
    private Path path;

    @Mock
    private PathOperationRestriction pathOperationRestriction;

    @Mock
    private LockInfo lockInfo;

    @Mock
    private List<LockInfo> lockInfos;

    @Before
    public void setup(){
    }

    @Test
    public void lockFromAnotherUserShouldCauseRestriction() {
        when(lockInfo.isLocked()).thenReturn(true);
        when(lockService.retrieveLockInfo(any())).thenReturn(lockInfo);
        when(identity.getIdentifier()).thenReturn("456");
        when(lockInfo.lockedBy()).thenReturn("123");
        when(lockService.retrieveLockInfos(path, true)).thenReturn(Arrays.asList(lockInfo));
        when(lockInfos.isEmpty()).thenReturn(true);

        PathOperationRestriction result = lockRestrictor.hasRestriction(path);
        assertNotNull(result);
        assertTrue(result instanceof PathOperationRestriction);
    }

    @Test
    public void lockedFilesShouldCauseRestriction() {
        when(lockInfo.isLocked()).thenReturn(false);
        when(lockService.retrieveLockInfo(any())).thenReturn(lockInfo);
        when(identity.getIdentifier()).thenReturn("123");
        when(lockInfo.lockedBy()).thenReturn("123");
        when(lockInfos.size()).thenReturn(1);
        when(lockService.retrieveLockInfos(path, true)).thenReturn(lockInfos);
        when(lockInfos.isEmpty()).thenReturn(false);

        PathOperationRestriction result = lockRestrictor.hasRestriction(path);
        assertNotNull(result);
        assertTrue(result instanceof PathOperationRestriction);
    }

    @Test
    public void noLockShouldNotCauseRestriction(){
        when(lockInfo.isLocked()).thenReturn(false);
        when(lockService.retrieveLockInfo(any())).thenReturn(lockInfo);
        when(identity.getIdentifier()).thenReturn("456");
        when(lockInfo.lockedBy()).thenReturn("123");
        when(lockService.retrieveLockInfos(path, true)).thenReturn(lockInfos);
        when(lockInfos.isEmpty()).thenReturn(true);

        PathOperationRestriction result = lockRestrictor.hasRestriction(path);
        assertNull(result);
    }

    @Test
    public void lockBySameUserShouldNotCauseRestriction() {
        when(lockInfo.isLocked()).thenReturn(true);
        when(lockService.retrieveLockInfo(any())).thenReturn(lockInfo);
        when(identity.getIdentifier()).thenReturn("123");
        when(lockInfo.lockedBy()).thenReturn("123");
        when(lockService.retrieveLockInfos(path, true)).thenReturn(lockInfos);
        when(lockInfos.isEmpty()).thenReturn(true);

        PathOperationRestriction result = lockRestrictor.hasRestriction(path);
        assertNull(result);
    }

    @Test
    public void emptyRestrictionListShouldNotCauseRestriction() {
        when(lockInfo.isLocked()).thenReturn(false);
        when(lockService.retrieveLockInfo(any())).thenReturn(lockInfo);
        when(identity.getIdentifier()).thenReturn("123");
        when(lockInfo.lockedBy()).thenReturn("456");
        when(lockInfos.isEmpty()).thenReturn(true);
        when(lockService.retrieveLockInfos(path, true)).thenReturn(lockInfos);

        PathOperationRestriction result = lockRestrictor.hasRestriction(path);
        assertNull(result);
    }

}
