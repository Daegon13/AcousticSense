#include <jni.h>
#include <memory>
#include "DuplexEngine.h"
static jlong create(JNIEnv*,jobject){return reinterpret_cast<jlong>(new DuplexEngine());}
static void destroy(JNIEnv*,jobject,jlong h){auto* e=reinterpret_cast<DuplexEngine*>(h);if(e){e->stop();delete e;}}
static jboolean start(JNIEnv*,jobject,jlong h){return reinterpret_cast<DuplexEngine*>(h)->start();}
static void stop(JNIEnv*,jobject,jlong h){reinterpret_cast<DuplexEngine*>(h)->stop();}
static jboolean pulse(JNIEnv*,jobject,jlong h){return reinterpret_cast<DuplexEngine*>(h)->pulse();}
static jstring snapshot(JNIEnv* env,jobject,jlong h){auto s=reinterpret_cast<DuplexEngine*>(h)->snapshotJson();return env->NewStringUTF(s.c_str());}
static JNINativeMethod methods[]={{"nativeCreate","()J",reinterpret_cast<void*>(create)},{"nativeDestroy","(J)V",reinterpret_cast<void*>(destroy)},{"nativeStart","(J)Z",reinterpret_cast<void*>(start)},{"nativeStop","(J)V",reinterpret_cast<void*>(stop)},{"nativePulse","(J)Z",reinterpret_cast<void*>(pulse)},{"nativeSnapshot","(J)Ljava/lang/String;",reinterpret_cast<void*>(snapshot)}};
JNIEXPORT jint JNI_OnLoad(JavaVM* vm,void*){JNIEnv* env=nullptr;if(vm->GetEnv(reinterpret_cast<void**>(&env),JNI_VERSION_1_6)!=JNI_OK)return JNI_ERR;auto c=env->FindClass("com/example/acousticsense/duplex/NativeDuplexEngine");if(!c||env->RegisterNatives(c,methods,sizeof(methods)/sizeof(methods[0]))<0)return JNI_ERR;return JNI_VERSION_1_6;}
