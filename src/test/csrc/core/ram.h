#ifndef __RAM_H__
#define __RAM_H__

#include "common.h"

#define RAMSIZE (128 * 1024 * 1024) // 128 MiB RAM
#define MSK_W (1 << 32 - 1)
#define MSK_RAMSIZE (1 << 27 - 1)   // 128 MiB

// Interface between CPU and MEM(C++)
typedef struct {
    bool imem_req_valid;
    word_t imem_req_addr;
    /* ignore wen & wdata for imem */
    word_t imem_resp_rdata;
} cpu_mem_if_t;

class ram_cpp {
public:
    ram_cpp(char *img, cpu_mem_if_t init_io);
    void eval();

private:
    word_t ram[RAMSIZE / sizeof(word_t)];
    cpu_mem_if_t io;

    int ram_offset(word_t addr);    // convert mem addr to offset in ram_cpp::ram
    word_t memRead(word_t raddr);
    void memWrite(word_t waddr, word_t wdata);
};

#endif