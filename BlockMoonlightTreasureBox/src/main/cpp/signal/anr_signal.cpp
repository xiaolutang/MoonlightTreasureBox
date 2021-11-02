//
// Created by tangxiaolu on 2021/11/2.
//

#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <inttypes.h>
#include <errno.h>
#include <signal.h>
#include <sys/syscall.h>
#include <android/log.h>

static sigset_t         xcc_signal_trace_oldset;
static struct sigaction xcc_signal_trace_oldact;

int block_anr_signal_trace_register(void (*handler)(int, siginfo_t *, void *)){
    int              r;
    sigset_t         set;
    struct sigaction act;

    //un-block the SIGQUIT mask for current thread, hope this is the main thread
    sigemptyset(&set);
    sigaddset(&set, SIGQUIT);
    if(0 != (r = pthread_sigmask(SIG_UNBLOCK, &set, &xcc_signal_trace_oldset))) return r;

    //register new signal handler for SIGQUIT
    memset(&act, 0, sizeof(act));
    sigfillset(&act.sa_mask);
    act.sa_sigaction = handler;
    act.sa_flags = SA_RESTART | SA_SIGINFO;
    if(0 != sigaction(SIGQUIT, &act, &xcc_signal_trace_oldact))
    {
        pthread_sigmask(SIG_SETMASK, &xcc_signal_trace_oldset, NULL);
        return -1;
//        return XCC_ERRNO_SYS;
    }

    return 0;
}
void block_anr_signal_trace_unregister(void){
    pthread_sigmask(SIG_SETMASK, &xcc_signal_trace_oldset, NULL);
    sigaction(SIGQUIT, &xcc_signal_trace_oldact, NULL);
}