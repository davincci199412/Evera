package com.evera.core.chain;

import com.evera.common.chain.ChainApp;
import com.evera.common.chain.ChainCategory;
import com.evera.common.chain.ChainDeal;
import com.evera.core.configuration.ConfigurationService;
import com.evera.core.task.Task;
import com.evera.core.task.TaskService;
import com.evera.core.task.event.TaskCreatedEvent;
import io.reactivex.Flowable;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

public class DealWatcherServiceTests {

    @Mock
    private IexecHubService everaHubService;

    @Mock
    private ConfigurationService configurationService;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @Mock
    private TaskService taskService;

    @InjectMocks
    private DealWatcherService dealWatcherService;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldRun() {
        BigInteger blockNumber = BigInteger.TEN;
        when(configurationService.getLastSeenBlockWithDeal()).thenReturn(blockNumber);
        when(everaHubService.getDealEventObservableToLatest(blockNumber))
            .thenReturn(Flowable.just(Optional.empty()));

        dealWatcherService.run();

        Mockito.verify(everaHubService, Mockito.times(1))
            .getDealEventObservableToLatest(blockNumber);
    }

    @Test
    public void shouldUpdateLastSeenBlockWhenOneDeal() {
        BigInteger from = BigInteger.valueOf(0);
        BigInteger blockOfDeal = BigInteger.valueOf(3);
        Optional<DealEvent> dealEvent = Optional.of(DealEvent
                .builder()
                .chainDealId("chainDealId")
                .blockNumber(blockOfDeal)
                .build());

        when(configurationService.getLastSeenBlockWithDeal()).thenReturn(from);
        when(everaHubService.getDealEventObservableToLatest(from)).thenReturn(Flowable.just(dealEvent));

        dealWatcherService.subscribeToDealEventFromOneBlockToLatest(from);

        Mockito.verify(configurationService, Mockito.times(1))
                .setLastSeenBlockWithDeal(blockOfDeal);
    }

    @Test
    public void shouldUpdateLastSeenBlockWhenOneDealAndCreateTask() {
        ChainApp chainApp = new ChainApp();
        chainApp.setUri("0x00");

        ChainCategory chainCategory = new ChainCategory();

        ChainDeal chainDeal = ChainDeal.builder()
                .botFirst(BigInteger.valueOf(0))
                .botSize(BigInteger.valueOf(1))
                .chainApp(chainApp)
                .chainCategory(chainCategory)
                .params(Arrays.asList("param1"))
                .trust(BigInteger.valueOf(3))
                .build();

        BigInteger from = BigInteger.valueOf(0);
        BigInteger blockOfDeal = BigInteger.valueOf(3);
        Optional<DealEvent> dealEvent = Optional.of(DealEvent.builder()
                .chainDealId("chainDealId")
                .blockNumber(blockOfDeal)
                .build());

        Task task = new Task();

        when(everaHubService.getDealEventObservableToLatest(from)).thenReturn(Flowable.just(dealEvent));
        when(everaHubService.getChainDeal(dealEvent.get().getChainDealId())).thenReturn(Optional.of(chainDeal));
        when(taskService.addTask(any(), Mockito.anyInt(), any(), any(), Mockito.anyInt(), anyLong(), any())).thenReturn(Optional.of(task));
        when(configurationService.getLastSeenBlockWithDeal()).thenReturn(from);

        ArgumentCaptor<TaskCreatedEvent> argumentCaptor = ArgumentCaptor.forClass(TaskCreatedEvent.class);

        dealWatcherService.subscribeToDealEventFromOneBlockToLatest(from);

        Mockito.verify(configurationService, Mockito.times(1))
                .setLastSeenBlockWithDeal(blockOfDeal);
        Mockito.verify(applicationEventPublisher, Mockito.times(1))
                .publishEvent(Mockito.any(TaskCreatedEvent.class));

        Mockito.verify(applicationEventPublisher, Mockito.times(1))
                .publishEvent(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue()).isEqualTo(new TaskCreatedEvent(task.getChainTaskId()));
    }

    @Test
    public void shouldUpdateLastSeenBlockWhenOneDealAndNotCreateTaskSinceBotSizeIsZero() {
        BigInteger from = BigInteger.valueOf(0);
        BigInteger blockOfDeal = BigInteger.valueOf(3);
        Optional<DealEvent> dealEvent = Optional.of(DealEvent.builder()
            .chainDealId("chainDealId")
            .blockNumber(blockOfDeal)
            .build());

        ChainDeal chainDeal = ChainDeal.builder()
            .botFirst(BigInteger.valueOf(0))
            .botSize(BigInteger.valueOf(0))
            .build();

        when(everaHubService.getDealEventObservableToLatest(from)).thenReturn(Flowable.just(dealEvent));
        when(everaHubService.getChainDeal(dealEvent.get().getChainDealId())).thenReturn(Optional.of(chainDeal));
        when(configurationService.getLastSeenBlockWithDeal()).thenReturn(from);

        dealWatcherService.subscribeToDealEventFromOneBlockToLatest(from);

        Mockito.verify(configurationService, Mockito.times(1))
            .setLastSeenBlockWithDeal(blockOfDeal);
        Mockito.verify(applicationEventPublisher, Mockito.times(0))
            .publishEvent(any());
    }

    @Test
    public void shouldUpdateLastSeenBlockWhenOneDealButNotCreateTaskSinceExceptionThrown() {
        BigInteger from = BigInteger.valueOf(0);
        BigInteger blockOfDeal = BigInteger.valueOf(3);
        Optional<DealEvent> dealEvent = Optional.of(DealEvent.builder()
            .chainDealId("chainDealId")
            .blockNumber(blockOfDeal)
            .build());

        ChainDeal chainDeal = ChainDeal.builder()
            .botFirst(BigInteger.valueOf(0))
            .botSize(BigInteger.valueOf(1))
            .build();


        when(everaHubService.getDealEventObservableToLatest(from)).thenReturn(Flowable.just(dealEvent));
        when(everaHubService.getChainDeal(dealEvent.get().getChainDealId())).thenReturn(Optional.of(chainDeal));
        when(configurationService.getLastSeenBlockWithDeal()).thenReturn(from);

        dealWatcherService.subscribeToDealEventFromOneBlockToLatest(from);

        Mockito.verify(configurationService, Mockito.times(1))
                .setLastSeenBlockWithDeal(blockOfDeal);
        Mockito.verify(applicationEventPublisher, Mockito.times(0))
                .publishEvent(any());
    }

    @Test
    public void shouldUpdateLastSeenBlockTwiceWhenTwoDeals() {
        BigInteger from = BigInteger.valueOf(0);
        BigInteger blockOfDeal1 = BigInteger.valueOf(3);
        Optional<DealEvent> dealEvent1 = Optional.of(DealEvent
                .builder()
                .chainDealId("chainDealId1")
                .blockNumber(blockOfDeal1)
                .build());
        BigInteger blockOfDeal2 = BigInteger.valueOf(5);
        Optional<DealEvent> dealEvent2 = Optional.of(DealEvent
                .builder()
                .chainDealId("chainDealId2")
                .blockNumber(blockOfDeal2)
                .build());

        when(configurationService.getLastSeenBlockWithDeal()).thenReturn(from);
        when(everaHubService.getDealEventObservableToLatest(from)).thenReturn(Flowable.just(dealEvent1, dealEvent2));

        dealWatcherService.subscribeToDealEventFromOneBlockToLatest(from);

        Mockito.verify(configurationService, Mockito.times(1))
                .setLastSeenBlockWithDeal(blockOfDeal1);
        Mockito.verify(configurationService, Mockito.times(1))
                .setLastSeenBlockWithDeal(blockOfDeal2);
    }

    @Test
    public void shouldNOtUpdateLastSeenBlockWhenReceivingOldMissedDeal() {
        BigInteger from = BigInteger.valueOf(5);
        BigInteger blockOfDeal1 = BigInteger.valueOf(3);
        Optional<DealEvent> dealEvent1 = Optional.of(DealEvent
                .builder()
                .chainDealId("chainDealId1")
                .blockNumber(blockOfDeal1)
                .build());

        when(configurationService.getLastSeenBlockWithDeal()).thenReturn(from);
        when(everaHubService.getDealEventObservableToLatest(from)).thenReturn(Flowable.just(dealEvent1));

        dealWatcherService.subscribeToDealEventFromOneBlockToLatest(from);

        Mockito.verify(configurationService, Mockito.times(0))
                .setLastSeenBlockWithDeal(blockOfDeal1);
    }

    @Test
    public void shouldReplayAllEventInRange() {
        BigInteger blockOfDeal1 = BigInteger.valueOf(3);
        Optional<DealEvent> dealEvent1 = Optional.of(DealEvent
                .builder()
                .chainDealId("chainDealId1")
                .blockNumber(blockOfDeal1)
                .build());

        when(configurationService.getLastSeenBlockWithDeal()).thenReturn(BigInteger.TEN);
        when(configurationService.getFromReplay()).thenReturn(BigInteger.ZERO);
        when(everaHubService.getDealEventObservable(any(), any())).thenReturn(Flowable.just(dealEvent1));

        dealWatcherService.replayDealEvent();

        Mockito.verify(everaHubService, Mockito.times(1))
                .getChainDeal(any());
    }

    @Test
    public void shouldNotReplayIfFromReplayEqualsLastSeenBlock() {
        BigInteger blockOfDeal1 = BigInteger.valueOf(3);
        Optional<DealEvent> dealEvent1 = Optional.of(DealEvent
                .builder()
                .chainDealId("chainDealId1")
                .blockNumber(blockOfDeal1)
                .build());

        when(configurationService.getLastSeenBlockWithDeal()).thenReturn(BigInteger.ZERO);
        when(configurationService.getFromReplay()).thenReturn(BigInteger.ZERO);
        when(everaHubService.getDealEventObservable(any(), any())).thenReturn(Flowable.just(dealEvent1));

        dealWatcherService.replayDealEvent();

        Mockito.verify(everaHubService, Mockito.times(0))
                .getChainDeal(any());
    }
}
