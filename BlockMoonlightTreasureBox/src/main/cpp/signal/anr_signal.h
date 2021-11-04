//
// Created by tangxiaolu on 2021/11/2.
//

#ifndef MOONLIGHTTREASUREBOX_ANR_SIGNAL_H
#define MOONLIGHTTREASUREBOX_ANR_SIGNAL_H 1



#endif //MOONLIGHTTREASUREBOX_ANR_SIGNAL_H
#include <stdint.h>
#include <sys/types.h>
#include <signal.h>
#include <jni.h>

#ifdef __cplusplus
extern "C" {
#endif

int block_anr_signal_trace_register(void (*handler)(int, siginfo_t *, void *));
void block_anr_signal_trace_unregister(void);
void xc_trace_send_sigquit();

#ifdef __cplusplus
}
#endif

static pid_t xc_common_process_id;
