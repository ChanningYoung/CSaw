#ifndef __RAM_H__
#define __RAM_H__

#include "common.h"

#define RAMSIZE (128 * 1024 * 1024) // 128 MiB RAM

// Interface between CPU and MEM(C++)
typedef struct {
    bool *imem_req_valid;
    word_t *imem_req_addr;
    /* ignore wen & wdata for imem */
    word_t *imem_resp_rdata;
} cpu_mem_if_t;

class ram_cpp {
public:
    ram_cpp(char *img, cpu_mem_if_t init_io);
    void eval();

private:
    word_t ram[RAMSIZE / sizeof(word_t)];
    cpu_mem_if_t io;
};

#endif