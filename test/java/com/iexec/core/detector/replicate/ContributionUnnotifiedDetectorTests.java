package com.evera.core.detector.replicate;

import com.evera.common.replicate.ReplicateStatus;
import com.evera.core.chain.IexecHubService;
import com.evera.core.detector.replicate.ContributionUnnotifiedDetector;
import com.evera.core.replicate.Replicate;
import com.evera.core.replicate.ReplicatesService;
import com.evera.core.task.Task;
import com.evera.core.task.TaskService;
import com.evera.core.task.TaskStatus;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ContributionUnnotifiedDetectorTests {

    private final static String CHAIN_TASK_ID = "chainTaskId";

    @Mock
    private TaskService taskService;

    @Mock
    private ReplicatesService replicatesService;

    @Mock
    private IexecHubService everaHubService;

    @Spy
    @InjectMocks
    private ContributionUnnotifiedDetector contributionDetector;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldDetectUnNotifiedContributed() {
        Task task = mock(Task.class);
        when(task.getChainTaskId()).thenReturn(any());
        when(taskService.findByCurrentStatus(Arrays.asList(TaskStatus.INITIALIZED, TaskStatus.RUNNING))).thenReturn(Collections.singletonList(task));

        Replicate replicate1 = mock(Replicate.class);
        when(replicate1.getCurrentStatus()).thenReturn(ReplicateStatus.COMPUTED);
        when(replicate1.isCreatedMoreThanNPeriodsAgo(anyInt(), anyLong())).thenReturn(true);

        when(replicatesService.getReplicates(any())).thenReturn(Collections.singletonList(replicate1));
        when(everaHubService.doesWishedStatusMatchesOnChainStatus(any(), any(), any())).thenReturn(true);
        contributionDetector.detect();

        Mockito.verify(replicatesService, Mockito.times(3))//CAN_CONTRIBUTE & CONTRIBUTING & CONTRIBUTED
                .updateReplicateStatus(any(), any(), any(), any());
    }

    @Test
    public void shouldNotDetectUnNotifiedContributedIfNotContributed() {
        Task task = mock(Task.class);
        when(taskService.findByCurrentStatus(Arrays.asList(TaskStatus.INITIALIZED, TaskStatus.RUNNING))).thenReturn(Collections.singletonList(task));

        Replicate replicate1 = mock(Replicate.class);
        when(replicate1.getCurrentStatus()).thenReturn(ReplicateStatus.COMPUTED);
        when(replicate1.isCreatedMoreThanNPeriodsAgo(anyInt(), anyLong())).thenReturn(true);

        when(replicatesService.getReplicates(any())).thenReturn(Collections.singletonList(replicate1));
        when(everaHubService.doesWishedStatusMatchesOnChainStatus(any(), any(), any())).thenReturn(false);
        contributionDetector.detect();

        Mockito.verify(replicatesService, Mockito.times(0))
                .updateReplicateStatus(any(), any(), any(), any());
    }

    @Test
    public void shouldNotYetDetectUnNotifiedContributedIfContributingPeriodTooShort() {
        Task task = mock(Task.class);
        when(taskService.findByCurrentStatus(Arrays.asList(TaskStatus.INITIALIZED, TaskStatus.RUNNING))).thenReturn(Collections.singletonList(task));

        Replicate replicate1 = mock(Replicate.class);
        when(replicate1.getCurrentStatus()).thenReturn(ReplicateStatus.COMPUTED);
        when(replicate1.isCreatedMoreThanNPeriodsAgo(anyInt(), anyLong())).thenReturn(false);

        when(replicatesService.getReplicates(task.getChainTaskId())).thenReturn(Collections.singletonList(replicate1));
        when(everaHubService.doesWishedStatusMatchesOnChainStatus(any(), any(), any())).thenReturn(true);
        contributionDetector.detect();

        Mockito.verify(replicatesService, Mockito.times(0))
                .updateReplicateStatus(any(), any(), any(), any());
    }

}
