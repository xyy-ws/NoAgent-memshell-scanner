#include "pch.h"
#include "detect.h"
#include "NoAgent_detect.h"
#include "jvmti.h"

JNIEXPORT jlong JNICALL Java_NoAgent_detect_caloffset
(JNIEnv*, jobject) {
    struct JavaVM_* vm;
    jsize count;
    typedef jint(JNICALL* GetCreatedJavaVMs)(JavaVM**, jsize, jsize*);
    GetCreatedJavaVMs jni_GetCreatedJavaVMs;
    // ...
    jni_GetCreatedJavaVMs = (GetCreatedJavaVMs)GetProcAddress(GetModuleHandle(
        TEXT("jvm.dll")), "JNI_GetCreatedJavaVMs");
    jni_GetCreatedJavaVMs(&vm, 1, &count);
    struct jvmtiEnv_* _jvmti_env;
    HMODULE jvm = GetModuleHandle(L"jvm.dll");
    vm->functions->GetEnv(vm, (void**)&_jvmti_env, JVMTI_VERSION_1_2);
    printf(" hModule jvm = 0x%llx\n", jvm);
    printf(" struct JavaVM_* vm = 0x%llx\n", vm);
    printf(" _jvmti_env = 0x%llx\n", _jvmti_env);
    return (long long)_jvmti_env;
}
