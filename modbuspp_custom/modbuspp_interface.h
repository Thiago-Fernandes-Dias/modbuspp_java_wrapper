#include <stdint.h>
#include <stdbool.h>

#ifndef __cplusplus
extern "C"
{
#endif

    typedef struct modbus modbus;

    __declspec(dllexport) modbus *new_modbus(const char *, uint16_t);

    __declspec(dllexport) void set_slave_id(modbus *, int);

    __declspec(dllexport) bool modconnect(modbus *);

    __declspec(dllexport) void close(modbus *);

    __declspec(dllexport) int read_coils(modbus *, uint16_t, uint16_t, bool *);

    __declspec(dllexport) int read_input_bits(modbus *, uint16_t, uint16_t, bool *);

    __declspec(dllexport) int read_holding_registers(modbus *, uint16_t, uint16_t, uint16_t *);

    __declspec(dllexport) int read_input_registers(modbus *, uint16_t, uint16_t, uint16_t *);

    __declspec(dllexport) int write_coil(modbus *, uint16_t, const bool &);

    __declspec(dllexport) int write_register(modbus *, uint16_t, const uint16_t &);

    __declspec(dllexport) int write_coils(modbus *, uint16_t, uint16_t, const bool *);

    __declspec(dllexport) int write_registers(modbus *, uint16_t, uint16_t, const uint16_t *);

    __declspec(dllexport) void show_error_message(modbus *);

#ifndef __cplusplus
}
#endif