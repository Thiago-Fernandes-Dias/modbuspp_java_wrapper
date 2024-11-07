extern "C"
{
#include "modbuspp_interface.h"
}

#include "modbus.h"

#include <cstdint>
#include <stdbool.h>
#include <iostream>
#include <winsock2.h>

using namespace std;

__declspec(dllexport)
modbus *
new_modbus(const char *host, uint16_t port)
{
    return new modbus(host, port);
}

__declspec(dllexport) void set_slave_id(modbus *obj, int p1)
{
    obj->modbus_set_slave_id(p1);
}

__declspec(dllexport) bool modconnect(modbus *obj)
{
    return obj->modbus_connect();
}

__declspec(dllexport) void close(modbus *obj)
{
    obj->modbus_close();
}

__declspec(dllexport) int read_coils(modbus *obj, uint16_t p1, uint16_t p2, bool *p3)
{
    return obj->modbus_read_coils(p1, p2, p3);
}

__declspec(dllexport) int read_input_bits(modbus *obj, uint16_t p1, uint16_t p2, bool *p3)
{
    return obj->modbus_read_input_bits(p1, p2, p3);
}

__declspec(dllexport) int read_holding_registers(modbus *obj, uint16_t p1, uint16_t p2, uint16_t *p3)
{
    return obj->modbus_read_holding_registers(p1, p2, p3);
}

__declspec(dllexport) int read_input_registers(modbus *obj, uint16_t p1, uint16_t p2, uint16_t *p3)
{
    return obj->modbus_read_input_registers(p1, p2, p3);
}

__declspec(dllexport) int write_coil(modbus *obj, uint16_t p1, const bool &p2)
{
    return obj->modbus_write_coil(p1, p2);
}

__declspec(dllexport) int write_register(modbus *obj, uint16_t p1, const uint16_t &p2)
{
    return obj->modbus_write_register(p1, p2);
}

__declspec(dllexport) int write_coils(modbus *obj, uint16_t p1, uint16_t p2, const bool *p3)
{
    return obj->modbus_write_coils(p1, p2, p3);
}

__declspec(dllexport) void show_error_message(modbus *obj)
{
    cout << obj->error_msg << endl;
    cout << WSAGetLastError() << endl;
}
