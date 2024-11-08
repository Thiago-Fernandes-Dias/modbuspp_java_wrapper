package com.thiago;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;

public class Main {
    private final static int SLAVE_ID = 1;
    private final static char PORT = 5020;
    private final static String SERVER_IP = "127.0.0.1";
    private final static char ADDRESS = 0; // Endereço de escrita (ponto inicial) no MODBUS

    public static void main(String[] args) {
        System.out.println("Calling a method from the modbus_client_interface class library");

        // Carregar a biblioteca de funções compartilhadas
        System.load("/app/modbuspp_interface.so");

        // Espaço de memória fora da JVM
        Arena arena = Arena.ofConfined();

        // Criar MethodHandles, para chamar as funções em C++ através do Java.
        Linker linker = Linker.nativeLinker();

        // Encontrar os endereços das funções.
        SymbolLookup lookup = SymbolLookup.loaderLookup();

        // Funções da modbuspp_interface.h, que usam a classe C++ modbus
        // MemorySegment -> Endereço da função em C++ na memória;
        // FunctionDescriptor -> Map da Declaração da função. O primeiro argumento
        // é o retorno da função e os próximos são os parâmetros, sempre ValueLayouts;
        // MethodHandle -> objeto usado para chamar a função em C++.
        MemorySegment newModbusAddr = lookup.find("new_modbus").orElseThrow();
        FunctionDescriptor newModbusFuncDesc = FunctionDescriptor.of(ValueLayout.ADDRESS, ValueLayout.ADDRESS,
                ValueLayout.JAVA_CHAR);
        MethodHandle newModbus = linker.downcallHandle(newModbusAddr, newModbusFuncDesc);
        MemorySegment connectAddr = lookup.find("modconnect").orElseThrow();
        FunctionDescriptor connectFuncDesc = FunctionDescriptor.of(ValueLayout.JAVA_BOOLEAN, ValueLayout.ADDRESS);
        MethodHandle connect = linker.downcallHandle(connectAddr, connectFuncDesc);
        MemorySegment setSlaveIdAdd = lookup.find("set_slave_id").orElseThrow();
        FunctionDescriptor setSlaveIdFuncDesc = FunctionDescriptor.ofVoid(ValueLayout.ADDRESS, ValueLayout.JAVA_INT);
        MethodHandle setSlaveId = linker.downcallHandle(setSlaveIdAdd, setSlaveIdFuncDesc);
        MemorySegment showErrorMsgAddr = lookup.find("show_error_message").orElseThrow();
        FunctionDescriptor showErrorMsgFuncDesc = FunctionDescriptor.ofVoid(ValueLayout.ADDRESS);
        MethodHandle showErrorMsg = linker.downcallHandle(showErrorMsgAddr, showErrorMsgFuncDesc);
        MemorySegment readHoldingRegistersAddr = lookup.find("read_holding_registers").orElseThrow();
        FunctionDescriptor readHoldingRegistersFuncDesc = FunctionDescriptor.of(ValueLayout.JAVA_INT,
                ValueLayout.ADDRESS, ValueLayout.JAVA_CHAR, ValueLayout.JAVA_CHAR, ValueLayout.ADDRESS);
        MethodHandle readHoldingRegisters = linker.downcallHandle(readHoldingRegistersAddr,
                readHoldingRegistersFuncDesc);
        MemorySegment writeRegistersAddr = lookup.find("write_registers").orElseThrow();
        FunctionDescriptor writeRegistersFuncDesc = FunctionDescriptor.of(ValueLayout.JAVA_INT,
                ValueLayout.ADDRESS, ValueLayout.JAVA_CHAR, ValueLayout.JAVA_CHAR, ValueLayout.ADDRESS);
        MethodHandle writeRegisters = linker.downcallHandle(writeRegistersAddr,
                writeRegistersFuncDesc);
        MemorySegment writeCoilAddr = lookup.find("write_coil").orElseThrow();
        FunctionDescriptor writeCoilFuncDesc = FunctionDescriptor.of(ValueLayout.JAVA_INT,
                ValueLayout.ADDRESS, ValueLayout.JAVA_CHAR, ValueLayout.JAVA_BOOLEAN);
        MethodHandle writeCoil = linker.downcallHandle(writeCoilAddr,
                writeCoilFuncDesc);
        MemorySegment readCoilsAddr = lookup.find("read_coils").orElseThrow();
        FunctionDescriptor readCoilsFuncDesc = FunctionDescriptor.of(ValueLayout.JAVA_INT,
                ValueLayout.ADDRESS, ValueLayout.JAVA_CHAR, ValueLayout.JAVA_CHAR, ValueLayout.ADDRESS);
        MethodHandle readCoils = linker.downcallHandle(readCoilsAddr,
                readCoilsFuncDesc);

        // Exemplo
        try {
            MemorySegment hostStrAddr = arena.allocateUtf8String(SERVER_IP);
            MemorySegment modbusObj = (MemorySegment) newModbus.invoke(hostStrAddr, (char) PORT);
            setSlaveId.invoke(modbusObj, SLAVE_ID);
            if ((boolean) connect.invoke(modbusObj)) {
                // Escrita de registers
                MemorySegment writeRegistersBuff = arena.allocateArray(ValueLayout.JAVA_CHAR, 6);
                for (int i = 0; i < 6; i++) {
                    writeRegistersBuff.setAtIndex(ValueLayout.JAVA_CHAR, i, (char) 123);
                }
                int writeRegistersResult = (int) writeRegisters.invoke(modbusObj,
                        ADDRESS, (char) 5, writeRegistersBuff);
                System.out.printf("Write registers result: %d\n", writeRegistersResult);

                // Leitura de holding registers
                MemorySegment readRegistersBuff = arena.allocateArray(ValueLayout.JAVA_CHAR, 6);
                int readHoldingRegistersResult = (int) readHoldingRegisters.invoke(modbusObj,
                        ADDRESS, (char) 5, readRegistersBuff);
                System.out.printf("Read holding registers result: %d\n", readHoldingRegistersResult);
                readRegistersBuff.elements(ValueLayout.JAVA_CHAR).forEach(
                        (ms) -> System.out.printf("Value: %d\n", (int) ms.getAtIndex(ValueLayout.JAVA_CHAR, 0)));

                // Escrita de um coil
                int writeCoilResult = (int) writeCoil.invoke(modbusObj, ADDRESS, true);
                System.out.printf("Write coil result: %d\n", writeCoilResult);

                // Leitura de coils
                MemorySegment readCoilsBuff = arena.allocateArray(ValueLayout.JAVA_BOOLEAN, 6);
                int readCoilsResult = (int) readCoils.invoke(modbusObj, ADDRESS, (char) 6, readCoilsBuff);
                System.out.printf("Read coils result: %d\n", readCoilsResult);
                readCoilsBuff.elements(ValueLayout.JAVA_BOOLEAN).forEach(
                        (ms) -> System.out.printf("Value: %b\n", ms.getAtIndex(ValueLayout.JAVA_BOOLEAN, 0)));

            } else {
                System.out.println("Failed to connect to modbus server");
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }

        arena.close();
    }
}