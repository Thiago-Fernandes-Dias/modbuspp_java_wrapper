#include <stdint.h>
#include <stdbool.h>

#ifndef __cplusplus
extern "C"
{
#endif

    typedef struct modbus modbus;

    __attribute__((visibility("default"))) modbus *new_modbus(const char *, uint16_t);

    __attribute__((visibility("default"))) void set_slave_id(modbus *, int);

    __attribute__((visibility("default"))) bool modconnect(modbus *);

    __attribute__((visibility("default"))) void modclose(modbus *);

    __attribute__((visibility("default"))) int read_coils(modbus *, uint16_t, uint16_t, bool *);

    __attribute__((visibility("default"))) int read_input_bits(modbus *, uint16_t, uint16_t, bool *);

    __attribute__((visibility("default"))) int read_holding_registers(modbus *, uint16_t, uint16_t, uint16_t *);

    __attribute__((visibility("default"))) int read_input_registers(modbus *, uint16_t, uint16_t, uint16_t *);

    __attribute__((visibility("default"))) int write_coil(modbus *, uint16_t, const bool);

    __attribute__((visibility("default"))) int write_register(modbus *, uint16_t, const uint16_t &);

    __attribute__((visibility("default"))) int write_coils(modbus *, uint16_t, uint16_t, const bool *);

    __attribute__((visibility("default"))) int write_registers(modbus *, uint16_t, uint16_t, const uint16_t *);

    __attribute__((visibility("default"))) void show_error_message(modbus *);

#ifndef __cplusplus
}
#endif