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
#include <jni.h>
#include <unistd.h>
#include <pthread.h>
#include <dirent.h>
#include <sys/eventfd.h>
#include <sys/syscall.h>
#include <android/log.h>
#include "xcc_util.h"
#include "anr_signal.h"

#define XC_TRACE_CALLBACK_METHOD_NAME         "traceCallback"
#define XC_TRACE_CALLBACK_METHOD_SIGNATURE    "(Ljava/lang/String;Ljava/lang/String;)V"

#define XC_TRACE_SIGNAL_CATCHER_TID_UNLOAD    (-2)
#define XC_TRACE_SIGNAL_CATCHER_TID_UNKNOWN   (-1)
#define XC_TRACE_SIGNAL_CATCHER_THREAD_NAME   "Signal Catcher"
#define XC_TRACE_SIGNAL_CATCHER_THREAD_SIGBLK 0x1000

static int                              xc_trace_is_lollipop = 0;
static pid_t                            xc_trace_signal_catcher_tid = XC_TRACE_SIGNAL_CATCHER_TID_UNLOAD;

static sigset_t         xcc_signal_trace_oldset;
static struct sigaction xcc_signal_trace_oldact;
//static pid_t         xc_common_process_id;


static void xc_trace_load_signal_catcher_tid()
{
    char           buf[256];
    DIR           *dir;
    struct dirent *ent;
    FILE          *f;
    pid_t          tid;
    uint64_t       sigblk;

    xc_trace_signal_catcher_tid = XC_TRACE_SIGNAL_CATCHER_TID_UNKNOWN;

    snprintf(buf, sizeof(buf), "/proc/%d/task", xc_common_process_id);
    if(NULL == (dir = opendir(buf))) return;
    while(NULL != (ent = readdir(dir)))
    {
        //get and check thread id
        if(0 != xcc_util_atoi(ent->d_name, &tid)) continue;
        if(tid < 0) continue;

        //check thread name
        xcc_util_get_thread_name(tid, buf, sizeof(buf));
        if(0 != strcmp(buf, XC_TRACE_SIGNAL_CATCHER_THREAD_NAME)) continue;

        //check signal block masks
        sigblk = 0;
        snprintf(buf, sizeof(buf), "/proc/%d/status", tid);
        if(NULL == (f = fopen(buf, "r"))) break;
        while(fgets(buf, sizeof(buf), f))
        {
            if(1 == sscanf(buf, "SigBlk: %" SCNx64, &sigblk)) break;
        }
        fclose(f);
        if(XC_TRACE_SIGNAL_CATCHER_THREAD_SIGBLK != sigblk) continue;

        //found it
        xc_trace_signal_catcher_tid = tid;
        break;
    }
    closedir(dir);
}

void xc_trace_send_sigquit()
{
    if(XC_TRACE_SIGNAL_CATCHER_TID_UNLOAD == xc_trace_signal_catcher_tid)
        xc_trace_load_signal_catcher_tid();

    if(xc_trace_signal_catcher_tid >= 0)
        syscall(SYS_tgkill, xc_common_process_id, xc_trace_signal_catcher_tid, SIGQUIT);
}

int block_anr_signal_trace_register(void (*handler)(int, siginfo_t *, void *)){

    int              r;
    sigset_t         set;
    struct sigaction act;

    xc_common_process_id = getpid();
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





