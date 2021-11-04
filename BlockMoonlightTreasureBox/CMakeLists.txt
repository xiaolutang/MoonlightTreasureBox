cmake_minimum_required(VERSION 3.4.1)

add_library( # Sets the name of the library.
             block_signal

             # Sets the library as a shared library.
             SHARED

             # Provides a relative path to your source file(s).
        src/main/cpp/signal/anr_signal.cpp
        src/main/cpp/signal/jni_block_signal.cpp
        src/main/cpp/cpoy/xcrash/common/xcc_util.c
        src/main/cpp/cpoy/xcrash/common/xcc_libc_support.c
        src/main/cpp/cpoy/xcrash/common/xcc_fmt.c
        )

find_library( # Sets the name of the path variable.
              log-lib

              # Specifies the name of the NDK library that
              # you want CMake to locate.
              log )

include_directories(src/main/cpp/cpoy/xcrash/common)

target_link_libraries( # Specifies the target library.
                        block_signal

                       # Links the target library to the log library
                       # included in the NDK.
                       ${log-lib} )