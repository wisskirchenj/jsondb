package de.cofinpro.jsondb;

import de.cofinpro.jsondb.client.controller.ClientController;
import de.cofinpro.jsondb.io.ConsolePrinter;
import de.cofinpro.jsondb.io.SocketConfig;
import de.cofinpro.jsondb.server.config.MessageResourceBundle;
import de.cofinpro.jsondb.server.controller.ServerController;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SocketCommunicationIT {

    @Mock
    ConsolePrinter printer;

    @Captor
    ArgumentCaptor<String> argCaptor;

    ServerController server;

    @Test
    void whenClientConnects_ServerResponds() throws IOException, InterruptedException {
        server = new ServerController(printer);
        startServerThread();
        new ClientController(new ConsolePrinter()).send();
        verify(printer, times(3)).printInfo(argCaptor.capture());
        List<String> serverOutput = argCaptor.getAllValues();
        assertEquals(MessageResourceBundle.STARTED_MSG, serverOutput.get(0));
        assertTrue(serverOutput.get(1).contains("Received: "));
        assertTrue(serverOutput.get(1).contains("Give me a record # "));
        assertTrue(serverOutput.get(2).contains("Sent: "));
        assertTrue(serverOutput.get(2).contains("A record # "));
        assertTrue(serverOutput.get(2).contains(" was sent"));
    }

    private void startServerThread() throws InterruptedException {
        new Thread(() -> {
            try {
                server.run();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
        waitTenMs();
    }

    private static void waitTenMs() throws InterruptedException {
        final CountDownLatch waiter = new CountDownLatch(1);
        assertFalse(waiter.await(10, TimeUnit.MILLISECONDS));
    }

    @Test
    void whenClientConnectsToServer_ClientGetsAnswer() throws IOException, InterruptedException {
        server = new ServerController(new ConsolePrinter());
        startServerThread();
        new ClientController(printer).send();
        verify(printer, times(3)).printInfo(argCaptor.capture());
        List<String> clientOutput = argCaptor.getAllValues();
        assertEquals(de.cofinpro.jsondb.client.config.MessageResourceBundle.STARTED_MSG, clientOutput.get(0));
        assertTrue(clientOutput.get(1).contains("Sent: "));
        assertTrue(clientOutput.get(1).contains("Give me a record # "));
        assertTrue(clientOutput.get(2).contains("Received: "));
        assertTrue(clientOutput.get(2).contains("A record # "));
        assertTrue(clientOutput.get(2).contains(" was sent"));
    }


    @Test
    void whenClientSendsInvalidRequest_ServerRespondsWithInvalidMessage() throws IOException, InterruptedException {
        server = new ServerController(printer);
        startServerThread();
        try (Socket mockClient = new Socket(InetAddress.getByName(SocketConfig.getSERVER_ADDRESS()),
                SocketConfig.getSERVER_PORT())) {
            new DataOutputStream(mockClient.getOutputStream()).writeUTF("something");
            assertEquals(MessageResourceBundle.INVALID_REQUEST_MSG,
                    new DataInputStream(mockClient.getInputStream()).readUTF());
        }
        waitTenMs();
        verify(printer, times(3)).printInfo(argCaptor.capture());
        List<String> serverOutput = argCaptor.getAllValues();
        assertEquals(MessageResourceBundle.STARTED_MSG, serverOutput.get(0));
        assertEquals("Received: something", serverOutput.get(1));
        assertEquals("Sent: " + MessageResourceBundle.INVALID_REQUEST_MSG, serverOutput.get(2));
    }


    @Test
    void whenSendCalledWithoutServer_ConnectExceptionThrown() {
        assertThrows(ConnectException.class, () -> new ClientController(printer).send());
        verify(printer, never()).printInfo(anyString());
    }
}
