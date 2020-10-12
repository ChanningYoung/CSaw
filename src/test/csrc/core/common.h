#ifndef __COMMON_H__
#define __COMMON_H__

#include "constants.h"

typedef unsigned long u64;

#if (XLEN == 64)
    typedef u64 word_t;
#endif

#endif