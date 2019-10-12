package com.gmail.bmskoh.strategyapp;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import javax.websocket.WebSocketContainer;

import com.gmail.bmskoh.strategyapp.comm.ConnectionClient;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class StrategyApplicationTests {
    @Mock
    ConnectionClient connClient;
    @InjectMocks
    StrategyApplication strategyApplication;

    @Test
    public void testAppStarts() {
        strategyApplication.run();

        verify(connClient).startConnection(any(WebSocketContainer.class));
    }
}