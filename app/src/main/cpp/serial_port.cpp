// Allows: Java ↔ C++ communication.
#include <jni.h>

// contains Linux file control functions like: open()
#include <fcntl.h>

// contains UART configuration APIs. VERY important for serial communication
#include <termios.h>

// contains Linux system functions.
#include <unistd.h>

// expose function in C-compatible form. Needed for JNI naming compatibility
extern "C"

// JNI function exposed to Java.
JNIEXPORT jint JNICALL

// JNI naming format:
// Java_
// package_name_
// class_name_
// method_name

// THIS MATCHES YOUR JAVA METHOD
// public native int openSerialPort(...)
Java_com_linkitsoft_serialcommunication_MainActivity_openSerialPort(

        // JNI environment. interacting with Java objects/strings.
        JNIEnv *env,

        // current Java object instance (MainActivity object)
        jobject thiz,

        // Java String passed from Java side
        jstring path,

        // integer baudrate from Java side
        jint baudrate) {

    // convert Java String into normal C string
    const char *portPath =
            env->GetStringUTFChars(path, nullptr);

    // Linux open() call
    // opens tty device in read + write mode
    int fd =
            open(portPath, O_RDWR);

    // if fd < 0 means open failed
    if (fd < 0) {

        return -1;
    }

    // UART configuration structure
    struct termios tty;

    // read current UART settings from tty device
    tcgetattr(fd, &tty);

    // set UART input baudrate
    cfsetispeed(&tty, B9600);

    // set UART output baudrate
    cfsetospeed(&tty, B9600);

    // enable receiver
    // ignore modem control lines
    tty.c_cflag |=
            (CLOCAL | CREAD);

    // clear current data bit settings
    tty.c_cflag &= ~CSIZE;

    // set 8 data bits
    tty.c_cflag |= CS8;

    // disable parity bit
    tty.c_cflag &= ~PARENB;

    // use 1 stop bit
    tty.c_cflag &= ~CSTOPB;

    // apply UART settings immediately
    tcsetattr(fd,
              TCSANOW,
              &tty);

    // release Java string memory
    env->ReleaseStringUTFChars(
            path,
            portPath
    );

    // return Linux file descriptor back to Java
    return fd;
}

/****************************************************************** Write Functions ***************************************************************/

extern "C"
JNIEXPORT jint JNICALL
Java_com_linkitsoft_serialcommunication_MainActivity_writeSerialPort(

        JNIEnv *env,

        jobject thiz,

        jint fd,

        jbyteArray data) {

    // convert Java byte[] into native byte array
    jbyte *buffer =
            env->GetByteArrayElements(
                    data,
                    nullptr
            );

    // get array size
    jsize length =
            env->GetArrayLength(data);

    // Linux write() call
    int result =
            write(
                    fd,
                    buffer,
                    length
            );

    // release Java byte array memory
    env->ReleaseByteArrayElements(
            data,
            buffer,
            0
    );

    // return number of written bytes
    return result;
}

/****************************************************************** Read Functions ***************************************************************/
extern "C"
JNIEXPORT jbyteArray JNICALL
Java_com_linkitsoft_serialcommunication_MainActivity_readSerialPort(

        JNIEnv *env,

        jobject thiz,

        jint fd) {

    // temporary native buffer
    char buffer[1024];

    // Linux read() call
    int size =
            read(
                    fd,
                    buffer,
                    sizeof(buffer)
            );

    // if nothing received
    if (size <= 0) {

        return nullptr;
    }

    // create Java byte[]
    jbyteArray result =
            env->NewByteArray(size);

    // copy native bytes into Java array
    env->SetByteArrayRegion(
            result,
            0,
            size,
            (jbyte *) buffer
    );

    return result;
}