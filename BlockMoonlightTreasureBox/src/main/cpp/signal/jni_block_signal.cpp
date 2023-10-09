//
// Created by tangxiaolu on 2021/11/2.
//

#include <jni.h>
#include <string>
#include <jni.h>
#include "anr_signal.h"
//#include "anr_signal.cpp"

bool is_registered;
jobject system_anr_observed;

JNIEnv *glb_env;

static void notify_system_anr(){
    if(glb_env == nullptr || system_anr_observed == nullptr){
        return;
    }
    jclass obj_class = glb_env->GetObjectClass(system_anr_observed);

    jmethodID getName_method = glb_env->GetMethodID(obj_class, "onSystemAnr", "()V");

    glb_env->CallVoidMethod(system_anr_observed,getName_method);
}

static void xc_trace_handler(int sig, siginfo_t *si, void *uc)
{
    uint64_t data;

    (void)sig;
    (void)si;
    (void)uc;

    notify_system_anr();

    xc_trace_send_sigquit();
}


extern "C"
JNIEXPORT void JNICALL
Java_com_txl_blockmoonlighttreasurebox_block_SystemAnrMonitor_hookSignalCatcher(JNIEnv *env,
                                                                                jobject thiz,
                                                                                jobject observed) {
    if(!is_registered){
        glb_env = env;
        is_registered = true;
        system_anr_observed = env->NewGlobalRef(observed);
        block_anr_signal_trace_register(xc_trace_handler);
    }
}


extern "C"
JNIEXPORT void JNICALL
Java_com_txl_blockmoonlighttreasurebox_block_SystemAnrMonitor_unHookSignalCatcher(JNIEnv *env,
                                                                                  jobject thiz) {
    if(system_anr_observed != nullptr){
        env->DeleteGlobalRef(system_anr_observed);
        is_registered = false;
        block_anr_signal_trace_unregister();
    }
}