#include "VALU.h"
#include "verilated.h"
#include <iostream>
#include <cstdlib>
#include <ctime>
using namespace std;

#define TEST_RUNS 100

unsigned long golden(unsigned long A, unsigned long B, int op) {
    unsigned long result;
    unsigned shamt = B % 64;

    switch (op) {
        case 0:  result = A + B;     break;
        case 1:  result = A - B;     break;
        case 2:  result = A << shamt;    break;
        case 3:  result = A >> shamt;    break;
        case 4:  result = (unsigned long) ((long) A >> shamt);   break;
        case 5:  result = A & B;     break;
        case 6:  result = A | B;     break;
        case 7:  result = A ^ B;     break;
        case 8:  result = (unsigned long) ((long) A < (long) B);   break;
        case 9:  result = (unsigned long) (A < B);   break;
        default:    result = 0;
    }
    return result;
}

unsigned long rand64() {
    return (unsigned long) ((mrand48() << 32) | mrand48());
}

int main() {
    VALU* top = new VALU;

    srand48(time(NULL));
    
    for (int op = 0; op < 10; op++) {
        top->io_op = op;
        for (int i = 0; i < TEST_RUNS; i++) {
            unsigned long A = rand64(); unsigned long B = rand64();
            unsigned long ans = golden(A, B, op);
            top->io_src1 = A;   top->io_src2 = B;
            top->eval();
            if (top->io_out != ans) {
                cout << "------Error!------\n";
                cout << "A:      " << A << endl;
                cout << "B:      " << B << endl;
                cout << "Expect: " << ans << endl;
                cout << "Got:    " << top->io_out << endl;
                return 1;
            }
        }
    }
    cout << "------PASSED------\n";
    return 0;
}
