package com.thiago;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;

public class Main {
    public static void main(String[] args) {
        System.out.println("Calling a method from the modbus_client_interface class library");
        System.load("C:\\Users\\Mshimizu\\Projetos\\modbuspp_java_wrapper\\modbuspp_custom\\modbuspp_interface.dll");
        Arena arena = Arena.openConfined();
        Linker linker = Linker.nativeLinker();
        SymbolLookup lookup = SymbolLookup.loaderLookup();

        // Methods from the modbus_client_interface class library
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
        MemorySegment readInputRegistersAddr = lookup.find("read_input_registers").orElseThrow();
        FunctionDescriptor readInputRegistersFuncDesc = FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.ADDRESS,
                ValueLayout.JAVA_CHAR,
                ValueLayout.JAVA_CHAR, ValueLayout.ADDRESS);
        MethodHandle readInputRegisters = linker.downcallHandle(readInputRegistersAddr, readInputRegistersFuncDesc);
        MemorySegment writeCoilAddr = lookup.find("write_coil").orElseThrow();
        FunctionDescriptor writeCoilFuncDesc = FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.ADDRESS,
                ValueLayout.JAVA_CHAR, ValueLayout.ADDRESS);
        MethodHandle writeCoil = linker.downcallHandle(writeCoilAddr, writeCoilFuncDesc);
        MemorySegment showErrorMsgAddr = lookup.find("show_error_message").orElseThrow();
        FunctionDescriptor showErrorMsgFuncDesc = FunctionDescriptor.ofVoid(ValueLayout.ADDRESS);
        MethodHandle showErrorMsg = linker.downcallHandle(showErrorMsgAddr, showErrorMsgFuncDesc);
        MemorySegment readCoilsAddr = lookup.find("read_coils").orElseThrow();
        FunctionDescriptor readCoilsFuncDesc = FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.ADDRESS,
                ValueLayout.JAVA_CHAR, ValueLayout.JAVA_CHAR, ValueLayout.ADDRESS);
        MethodHandle readCoils = linker.downcallHandle(readCoilsAddr, readCoilsFuncDesc);
        try {
            MemorySegment modbusObj = (MemorySegment) newModbus.invoke(
                    arena.allocateUtf8String("127.0.0.1"), (char) 502);
            setSlaveId.invoke(modbusObj, 1);
            if ((boolean) connect.invoke(modbusObj)) {
                MemorySegment booladdr = arena.allocate(ValueLayout.JAVA_BOOLEAN);
                booladdr.set(ValueLayout.JAVA_BOOLEAN, 0, true);
                System.out.printf("Read coil result: %d\n",
                        (int) readCoils.invoke(modbusObj, (char) 0, (char) 1, booladdr));
                showErrorMsg.invoke(modbusObj);
            } else {
                System.out.println("Failed to connect to modbus server");
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}