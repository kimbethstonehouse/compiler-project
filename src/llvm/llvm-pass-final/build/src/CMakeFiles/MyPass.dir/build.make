# CMAKE generated file: DO NOT EDIT!
# Generated by "Unix Makefiles" Generator, CMake Version 3.16

# Delete rule output on recipe failure.
.DELETE_ON_ERROR:


#=============================================================================
# Special targets provided by cmake.

# Disable implicit rules so canonical targets will work.
.SUFFIXES:


# Remove some rules from gmake that .SUFFIXES does not remove.
SUFFIXES =

.SUFFIXES: .hpux_make_needs_suffix_list


# Suppress display of executed commands.
$(VERBOSE).SILENT:


# A target that is always out of date.
cmake_force:

.PHONY : cmake_force

#=============================================================================
# Set environment variables for the build.

# The shell in which to execute make rules.
SHELL = /bin/sh

# The CMake executable.
CMAKE_COMMAND = /usr/local/bin/cmake

# The command to remove a file.
RM = /usr/local/bin/cmake -E remove -f

# Escaping for special characters.
EQUALS = =

# The top-level source directory on which CMake was run.
CMAKE_SOURCE_DIR = /home/kim/ug3-ct/ct-19-20/src/llvm/llvm-pass-final

# The top-level build directory on which CMake was run.
CMAKE_BINARY_DIR = /home/kim/ug3-ct/ct-19-20/src/llvm/llvm-pass-final/build

# Include any dependencies generated for this target.
include src/CMakeFiles/MyPass.dir/depend.make

# Include the progress variables for this target.
include src/CMakeFiles/MyPass.dir/progress.make

# Include the compile flags for this target's objects.
include src/CMakeFiles/MyPass.dir/flags.make

src/CMakeFiles/MyPass.dir/MyPass.cpp.o: src/CMakeFiles/MyPass.dir/flags.make
src/CMakeFiles/MyPass.dir/MyPass.cpp.o: ../src/MyPass.cpp
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green --progress-dir=/home/kim/ug3-ct/ct-19-20/src/llvm/llvm-pass-final/build/CMakeFiles --progress-num=$(CMAKE_PROGRESS_1) "Building CXX object src/CMakeFiles/MyPass.dir/MyPass.cpp.o"
	cd /home/kim/ug3-ct/ct-19-20/src/llvm/llvm-pass-final/build/src && /usr/bin/c++  $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -o CMakeFiles/MyPass.dir/MyPass.cpp.o -c /home/kim/ug3-ct/ct-19-20/src/llvm/llvm-pass-final/src/MyPass.cpp

src/CMakeFiles/MyPass.dir/MyPass.cpp.i: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Preprocessing CXX source to CMakeFiles/MyPass.dir/MyPass.cpp.i"
	cd /home/kim/ug3-ct/ct-19-20/src/llvm/llvm-pass-final/build/src && /usr/bin/c++ $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -E /home/kim/ug3-ct/ct-19-20/src/llvm/llvm-pass-final/src/MyPass.cpp > CMakeFiles/MyPass.dir/MyPass.cpp.i

src/CMakeFiles/MyPass.dir/MyPass.cpp.s: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Compiling CXX source to assembly CMakeFiles/MyPass.dir/MyPass.cpp.s"
	cd /home/kim/ug3-ct/ct-19-20/src/llvm/llvm-pass-final/build/src && /usr/bin/c++ $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -S /home/kim/ug3-ct/ct-19-20/src/llvm/llvm-pass-final/src/MyPass.cpp -o CMakeFiles/MyPass.dir/MyPass.cpp.s

# Object files for target MyPass
MyPass_OBJECTS = \
"CMakeFiles/MyPass.dir/MyPass.cpp.o"

# External object files for target MyPass
MyPass_EXTERNAL_OBJECTS =

src/libMyPass.so: src/CMakeFiles/MyPass.dir/MyPass.cpp.o
src/libMyPass.so: src/CMakeFiles/MyPass.dir/build.make
src/libMyPass.so: src/CMakeFiles/MyPass.dir/link.txt
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green --bold --progress-dir=/home/kim/ug3-ct/ct-19-20/src/llvm/llvm-pass-final/build/CMakeFiles --progress-num=$(CMAKE_PROGRESS_2) "Linking CXX shared module libMyPass.so"
	cd /home/kim/ug3-ct/ct-19-20/src/llvm/llvm-pass-final/build/src && $(CMAKE_COMMAND) -E cmake_link_script CMakeFiles/MyPass.dir/link.txt --verbose=$(VERBOSE)

# Rule to build all files generated by this target.
src/CMakeFiles/MyPass.dir/build: src/libMyPass.so

.PHONY : src/CMakeFiles/MyPass.dir/build

src/CMakeFiles/MyPass.dir/clean:
	cd /home/kim/ug3-ct/ct-19-20/src/llvm/llvm-pass-final/build/src && $(CMAKE_COMMAND) -P CMakeFiles/MyPass.dir/cmake_clean.cmake
.PHONY : src/CMakeFiles/MyPass.dir/clean

src/CMakeFiles/MyPass.dir/depend:
	cd /home/kim/ug3-ct/ct-19-20/src/llvm/llvm-pass-final/build && $(CMAKE_COMMAND) -E cmake_depends "Unix Makefiles" /home/kim/ug3-ct/ct-19-20/src/llvm/llvm-pass-final /home/kim/ug3-ct/ct-19-20/src/llvm/llvm-pass-final/src /home/kim/ug3-ct/ct-19-20/src/llvm/llvm-pass-final/build /home/kim/ug3-ct/ct-19-20/src/llvm/llvm-pass-final/build/src /home/kim/ug3-ct/ct-19-20/src/llvm/llvm-pass-final/build/src/CMakeFiles/MyPass.dir/DependInfo.cmake --color=$(COLOR)
.PHONY : src/CMakeFiles/MyPass.dir/depend
