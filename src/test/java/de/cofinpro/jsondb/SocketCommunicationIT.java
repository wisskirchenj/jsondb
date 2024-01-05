package de.cofinpro.jsondb;

import de.cofinpro.jsondb.client.controller.ClientController;
import de.cofinpro.jsondb.io.ConsolePrinter;
import de.cofinpro.jsondb.io.json.DatabaseCommand;
import de.cofinpro.jsondb.io.json.DatabaseResponse;
import de.cofinpro.jsondb.server.config.MessageResourceBundle;
import de.cofinpro.jsondb.server.controller.ServerController;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.List;

import static de.cofinpro.jsondb.client.config.MessageResourceBundle.RECEIVED_MSG_TEMPLATE;
import static de.cofinpro.jsondb.client.config.MessageResourceBundle.SENT_MSG_TEMPLATE;
import static de.cofinpro.jsondb.client.config.MessageResourceBundle.STARTED_MSG;
import static de.cofinpro.jsondb.io.json.GsonPooled.POOLED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SocketCommunicationIT {

    static final String VALUE_RESPONSE_TEMPLATE = "Received: {\"response\":\"OK\",\"value\":%s}";
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

    @AfterAll
    static void closeServer() throws IOException {
        String[] args = new String[]{"-t", "exit"};
        new ClientController(new ConsolePrinter()).send(args);
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
        String[] args = new String[]{"-t", "get", "-k", "1"};
        new ClientController(printer).send(args);
        verify(printer, times(3)).printInfo(argCaptor.capture());
        List<String> clientOutput = argCaptor.getAllValues();
        assertEquals(STARTED_MSG, clientOutput.get(0));
        assertEquals(SENT_MSG_TEMPLATE.formatted(POOLED.gson()
                        .toJson(new DatabaseCommand("get", "1", null))),
                clientOutput.get(1));
        assertEquals(RECEIVED_MSG_TEMPLATE.formatted(POOLED.gson().toJson(DatabaseResponse.error())),
                clientOutput.get(2));
    }

    @Test
    void whenClientRequestsSet_ClientGetsAnswer() throws IOException {
        String[] args = new String[]{"-t", "set", "-k", "17", "-v", "hi there!"};
        new ClientController(printer).send(args);
        verify(printer, times(3)).printInfo(argCaptor.capture());
        List<String> clientOutput = argCaptor.getAllValues();
        assertEquals(STARTED_MSG, clientOutput.get(0));
        DatabaseCommand command = new DatabaseCommand("set", "17", "hi there!");
        assertEquals(SENT_MSG_TEMPLATE.formatted(POOLED.gson().toJson(command)),
                clientOutput.get(1));
        assertEquals(RECEIVED_MSG_TEMPLATE.formatted(POOLED.gson().toJson(DatabaseResponse.ok())),
                clientOutput.get(2));
    }

    @Test
    void whenClientRequestsDelete_ClientGetsAnswer() throws IOException {
        String[] args = new String[]{"-t", "delete", "-k", "new"};
        new ClientController(printer).send(args);
        verify(printer, times(3)).printInfo(argCaptor.capture());
        List<String> clientOutput = argCaptor.getAllValues();
        assertEquals(STARTED_MSG, clientOutput.get(0));
        DatabaseCommand command = new DatabaseCommand("delete", "new", null);
        assertEquals(SENT_MSG_TEMPLATE.formatted(POOLED.gson().toJson(command)),
                clientOutput.get(1));
        assertEquals(RECEIVED_MSG_TEMPLATE.formatted(POOLED.gson().toJson(DatabaseResponse.error())),
                clientOutput.get(2));
    }


    @Test
    void whenClientReadsSetNestedKey_SetStoresNestedJson() throws IOException {
        String[] args = new String[]{"-t", "delete", "-k", "17"};
        new ClientController(printer).send(args);
        args = new String[]{"-in", "set.json"};
        new ClientController(printer).send(args);
        args = new String[]{"-t", "get", "-k", "17"};
        new ClientController(printer).send(args);
        verify(printer, times(9)).printInfo(argCaptor.capture());
        List<String> clientOutput = argCaptor.getAllValues();
        assertEquals(STARTED_MSG, clientOutput.get(0));
        assertEquals(SENT_MSG_TEMPLATE.formatted("{\"type\":\"set\",\"key\":[\"17\", \"new_one\"],\"value\":\"a new value\"}"),
                clientOutput.get(4));
        assertEquals(RECEIVED_MSG_TEMPLATE.formatted(POOLED.gson().toJson(DatabaseResponse.ok())),
                clientOutput.get(5));
        assertEquals(VALUE_RESPONSE_TEMPLATE.formatted("{\"new_one\":\"a new value\"}"),
                clientOutput.get(8));
    }


    @Test
    void whenClientSetNewAttribToNestedKey_SetAddsToNestedJson() throws IOException {
        String[] args = new String[]{"-in", "set.json"};
        new ClientController(printer).send(args);
        args = new String[]{"-in", "set_17new.json"};
        new ClientController(printer).send(args);
        args = new String[]{"-t", "get", "-k", "17"};
        new ClientController(printer).send(args);
        verify(printer, times(9)).printInfo(argCaptor.capture());
        List<String> clientOutput = argCaptor.getAllValues();
        assertEquals(RECEIVED_MSG_TEMPLATE.formatted(POOLED.gson().toJson(DatabaseResponse.ok())),
                clientOutput.get(2));
        assertEquals(RECEIVED_MSG_TEMPLATE.formatted(POOLED.gson().toJson(DatabaseResponse.ok())),
                clientOutput.get(5));
        assertTrue(clientOutput.get(8).contains("\"new_one\":\"a new value\""));
        assertTrue(clientOutput.get(8).contains("\"very_new\":\"222 what up?\""));
    }

    @Test
    void whenNestedSetToNonExistingParent_SetCreatesParentAndAddsNestedValue() throws IOException {
        String[] args = new String[]{"-t", "delete", "-k", "nested"};
        new ClientController(printer).send(args);
        args = new String[]{"-in", "set_create_parent.json"};
        new ClientController(printer).send(args);
        args = new String[]{"-t", "get", "-k", "nested"};
        new ClientController(printer).send(args);
        verify(printer, times(9)).printInfo(argCaptor.capture());
        List<String> clientOutput = argCaptor.getAllValues();
        assertEquals(RECEIVED_MSG_TEMPLATE.formatted(POOLED.gson().toJson(DatabaseResponse.ok())),
                clientOutput.get(5));
        assertTrue(clientOutput.get(8).contains("\"complex\":\"true\""));
        assertEquals(VALUE_RESPONSE_TEMPLATE.formatted("{\"attrib\":{\"complex\":\"true\"}}"), clientOutput.get(8));
    }

    @Test
    void whenDeleteNestedAttribute_ValueIsDeleted() throws IOException {
        String[] args = new String[]{"-t", "delete", "-k", "17"};
        new ClientController(printer).send(args);
        args = new String[]{"-in", "set_17new.json"};
        new ClientController(printer).send(args);
        args = new String[]{"-in", "get_nested.json"};
        new ClientController(printer).send(args);
        args = new String[]{"-in", "delete17_nested.json"};
        new ClientController(printer).send(args);
        args = new String[]{"-in", "get_nested.json"};
        new ClientController(printer).send(args);
        args = new String[]{"-t", "get", "-k", "17"};
        new ClientController(printer).send(args);
        verify(printer, times(18)).printInfo(argCaptor.capture());
        List<String> clientOutput = argCaptor.getAllValues();
        assertEquals(VALUE_RESPONSE_TEMPLATE.formatted("\"222 what up?\""),
                clientOutput.get(8));
        assertEquals(RECEIVED_MSG_TEMPLATE.formatted(POOLED.gson().toJson(DatabaseResponse.ok())),
                clientOutput.get(11));
        assertEquals(RECEIVED_MSG_TEMPLATE.formatted(POOLED.gson().toJson(DatabaseResponse.error())),
                clientOutput.get(14));
        assertEquals(VALUE_RESPONSE_TEMPLATE.formatted("{}"), clientOutput.get(17));
    }

    @Test
    void whenClientSendsInvalidRequest_ClientReceivesInvalidMessage() throws IOException {
        String[] args = new String[]{"-t", "gett", "-k", "1"};
        new ClientController(printer).send(args);
        verify(printer, times(3)).printInfo(argCaptor.capture());
        List<String> clientOutput = argCaptor.getAllValues();
        assertEquals(STARTED_MSG, clientOutput.get(0));
        assertEquals(SENT_MSG_TEMPLATE.formatted(POOLED.gson()
                        .toJson(new DatabaseCommand("gett", "1", null))),
                clientOutput.get(1));
        assertEquals(RECEIVED_MSG_TEMPLATE.formatted(POOLED.gson().toJson(new DatabaseResponse(
                MessageResourceBundle.ERROR_MSG, null, MessageResourceBundle.INVALID_REQUEST_MSG))),
                clientOutput.get(2));
    }
}
