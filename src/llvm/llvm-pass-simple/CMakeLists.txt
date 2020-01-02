cmake_minimum_required(VERSION 3.4.3)
project(MyPass)

set(CMAKE_CXX_STANDARD 14)

if(NOT DEFINED ENV{LLVM_DIR})
    message(FATAL_ERROR "$LLVM_DIR is not defined")
endif()

find_package(LLVM REQUIRED CONFIG)
add_definitions(${LLVM_DEFINITIONS})
include_directories(${LLVM_INCLUDE_DIRS})
link_directories(${LLVM_LIBRARY_DIRS})

add_subdirectory(src)
