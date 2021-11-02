//
// Created by tangxiaolu on 2021/11/2.
//

#ifndef MOONLIGHTTREASUREBOX_ANR_SIGNAL_H
#define MOONLIGHTTREASUREBOX_ANR_SIGNAL_H 1
#include <stdint.h>
#include <sys/types.h>
#include <signal.h>
#endif //MOONLIGHTTREASUREBOX_ANR_SIGNAL_H


int block_anr_signal_trace_register(void (*handler)(int, siginfo_t *, void *));
void block_anr_signal_trace_unregister(void);