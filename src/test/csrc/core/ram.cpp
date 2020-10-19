#include "ram.h"

int ram_cpp::ram_offset(word_t addr) {
    // convert mem addr to offset in ram_cpp::ram
    return ((addr & MSK_W) - START_ADDR) & MSK_RAMSIZE;
}

word_t ram_cpp::memRead(word_t raddr) {
    word_t offset = ram_offset(raddr);
    return ram[offset];
}

void ram_cpp::memWrite(word_t waddr, word_t wdata) {
    word_t offset = ram_offset(waddr);
    ram[offset] = wdata;
}
