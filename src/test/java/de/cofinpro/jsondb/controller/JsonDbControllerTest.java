package de.cofinpro.jsondb.controller;

import de.cofinpro.jsondb.io.ConsolePrinter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Scanner;

import static de.cofinpro.jsondb.config.MessageResourceBundle.ERROR_MSG;
import static de.cofinpro.jsondb.config.MessageResourceBundle.OK_MSG;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JsonDbControllerTest {

    @Mock
    ConsolePrinter printerMock;

    @Mock
    Scanner scannerMock;

    JsonDbController controller;

    @BeforeEach
    void setup() {
        controller = new JsonDbController(printerMock);
    }

    @ParameterizedTest
    @ValueSource(strings = {"exi", "set a", "delete as", "get as", "anything wrong"})
    void whenWrongSyntax_ErrorMsgPrinted(String input) {
        when(scannerMock.nextLine()).thenReturn(input, "exit");
        controller.run(scannerMock);
        verify(printerMock).printInfo(ERROR_MSG);
    }

    @ParameterizedTest
    @ValueSource(strings = {"set 0", "delete 111", "set -2", "get 11111"})
    void whenWrongIndex_ErrorMsgPrinted(String input) {
        when(scannerMock.nextLine()).thenReturn(input, "exit");
        controller.run(scannerMock);
        verify(printerMock).printInfo(ERROR_MSG);
    }

    @Test
    void whenExitEnteredFirst_ProgramExitsSilently() {
        when(scannerMock.nextLine()).thenReturn("exit");
        controller.run(scannerMock);
        verify(printerMock, never()).printInfo(anyString());
    }

    @ParameterizedTest
    @ValueSource(strings = {"set 1", "delete 1", "set 100 something", "delete 100"})
    void whenGoodCommand_OkMsgPrinted(String input) {
        when(scannerMock.nextLine()).thenReturn(input,"exit");
        controller.run(scannerMock);
        verify(printerMock).printInfo(OK_MSG);
    }
}