package de.cofinpro.jsondb;

import de.cofinpro.jsondb.client.controller.ClientController;
import de.cofinpro.jsondb.io.ConsolePrinter;
import de.cofinpro.jsondb.server.config.MessageResourceBundle;
import de.cofinpro.jsondb.server.controller.ServerController;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.ConnectException;
import java.util.List;

import static de.cofinpro.jsondb.client.config.MessageResourceBundle.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SocketCommunicationIT {

    @Mock
    ConsolePrinter printer;

    @Captor
    ArgumentCaptor<String> argCaptor;

    static ServerController server;

    @BeforeAll
    static void setupServer() {
        server = new ServerController(new ConsolePrinter());
        startServerThread();
    }

    static void startServerThread() {
        new Thread(() -> {
            try {
                server.run();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    @Test
    void whenClientConnectsToServer_ClientGetsAnswer() throws IOException {
        String[] args =new String[]{"-t", "get", "-i", "1"};
        new ClientController(printer).send(args);
        verify(printer, times(3)).printInfo(argCaptor.capture());
        List<String> clientOutput = argCaptor.getAllValues();
        assertEquals(STARTED_MSG, clientOutput.get(0));
        assertEquals(SENT_MSG_TEMPLATE.formatted(String.join(" ", args)),
                clientOutput.get(1));
        assertEquals(RECEIVED_MSG_TEMPLATE.formatted(MessageResourceBundle.ERROR_MSG),
                clientOutput.get(2));
    }

    @Test
    void whenClientRequestsSet_ClientGetsAnswer() throws IOException {
        String[] args =new String[]{"-t", "set", "-i", "17", "-m", "hi there!"};
        new ClientController(printer).send(args);
        verify(printer, times(3)).printInfo(argCaptor.capture());
        List<String> clientOutput = argCaptor.getAllValues();
        assertEquals(STARTED_MSG, clientOutput.get(0));
        assertEquals(SENT_MSG_TEMPLATE.formatted(String.join(" ", args)),
                clientOutput.get(1));
        assertEquals(RECEIVED_MSG_TEMPLATE.formatted(MessageResourceBundle.OK_MSG),
                clientOutput.get(2));
    }

    @Test
    void whenClientSendsInvalidRequest_ClientReceivesInvalidMessage() throws IOException {
        String[] args =new String[]{"-t", "gett", "-i", "1"};
        new ClientController(printer).send(args);
        verify(printer, times(3)).printInfo(argCaptor.capture());
        List<String> clientOutput = argCaptor.getAllValues();
        assertEquals(STARTED_MSG, clientOutput.get(0));
        assertEquals(SENT_MSG_TEMPLATE.formatted(String.join(" ", args)),
                clientOutput.get(1));
        assertEquals(RECEIVED_MSG_TEMPLATE.formatted(MessageResourceBundle.INVALID_REQUEST_MSG),
                clientOutput.get(2));
    }

    @Test
    void whenExitSend_ServerStopsAndNextConnectThrowsConnectException() throws IOException {
        String[] args =new String[]{"-t", "exit"};
        new ClientController(printer).send(args);
        verify(printer, times(3)).printInfo(argCaptor.capture());
        List<String> clientOutput = argCaptor.getAllValues();
        assertEquals(STARTED_MSG, clientOutput.get(0));
        assertEquals(SENT_MSG_TEMPLATE.formatted(String.join(" ", args)),
                clientOutput.get(1));
        assertEquals(RECEIVED_MSG_TEMPLATE.formatted(MessageResourceBundle.OK_MSG),
                clientOutput.get(2));
        assertThrows(ConnectException.class, () -> new ClientController(printer).send(new String[]{}));
        startServerThread();
    }
}
