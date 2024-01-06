CC = gcc
CFLAGS = -fPIC -g

JAVA_HOME = $(shell readlink -f /usr/bin/javac | sed "s:bin/javac::")
JAVA_INCLUDE = $(JAVA_HOME)include
JAVA_LINUX_INCLUDE = $(JAVA_INCLUDE)/linux
JNI_INCLUDE = -I$(JAVA_INCLUDE) -I$(JAVA_LINUX_INCLUDE)

SOURCES = $(subst .c,.o,$(wildcard eflect_rapl/src/main/c/*.c)) $(wildcard eflect_rapl/src/main/headers/*.h)
HEADERS = $(wildcard eflect_rapl/src/main/headers/*.h)

BUILD_DIR = .

.DEFAULT_GOAL = eflect

%.o: %.c
	$(CC) -c -o $@ -Ieflect_rapl/src/main/headers $< $(CFLAGS) $(JNI_INCLUDE)

libCPUScaler.so: $(SOURCES)
	$(CC) -shared -Wl,-soname,$@ -o $@ $^ $(JNI_INCLUDE) -lc
