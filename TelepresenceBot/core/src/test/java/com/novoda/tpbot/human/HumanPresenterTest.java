package com.novoda.tpbot.human;

import com.novoda.tpbot.Result;
import com.novoda.tpbot.support.Observable;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class HumanPresenterTest {

    private static final String SERVER_ADDRESS = "http://192.168.0.1:3000";
    private static final Result SUCCESS_RESULT = Result.from("Connection Successful!");
    private static final Result FAILURE_RESULT = Result.from(new Exception("Connection Unsuccessful"));

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    HumanTpService tpService;

    @Mock
    HumanView humanView;

    private HumanPresenter presenter;

    @Before
    public void setUp() throws Exception {
        presenter = new HumanPresenter(tpService, humanView);
    }

    @Test
    public void givenSuccessfulConnection_whenStartPresenting_thenHumanViewOnConnectIsCalled() {
        when(tpService.connectTo(anyString())).thenReturn(Observable.just(SUCCESS_RESULT));

        presenter.startPresenting(SERVER_ADDRESS);

        verify(humanView).onConnect(SUCCESS_RESULT.message().get());
    }

    @Test
    public void givenUnsuccessfulConnection_whenStartPresenting_thenHumanViewOnErrorIsCalled() {
        when(tpService.connectTo(anyString())).thenReturn(Observable.just(FAILURE_RESULT));

        presenter.startPresenting(SERVER_ADDRESS);

        verify(humanView).onError(FAILURE_RESULT.exception().get().getMessage());
    }

    @Test
    public void givenAlreadyPresenting_whenStopPresentingIsCalled_thenTpServiceDisconnectIsCalled() {
        when(tpService.connectTo(anyString())).thenReturn(Observable.just(SUCCESS_RESULT));
        presenter.startPresenting(SERVER_ADDRESS);

        presenter.stopPresenting();

        verify(tpService).disconnect();
    }

    @Test
    public void givenAlreadyPresenting_whenStopPresentingIsCalled_thenConnectionObservableObserversAreDetached() {
        Observable<Result> observable = Observable.just(SUCCESS_RESULT);
        Observable<Result> spyObservable = Mockito.spy(observable);
        when(tpService.connectTo(anyString())).thenReturn(spyObservable);
        presenter.startPresenting(SERVER_ADDRESS);

        presenter.stopPresenting();

        verify(spyObservable).detachObservers();
    }

}