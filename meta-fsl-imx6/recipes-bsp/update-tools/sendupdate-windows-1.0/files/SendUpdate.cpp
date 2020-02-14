/*
    Create a TCP socket
*/

#include <iostream>
#include <stdio.h>
#include <winsock2.h>

using namespace std;

#define PACKAGE_SIZE_SEND    (512*1024)
#define PACKAGE_SIZE_RECV    (1*1024)
#define RECEIVE_LIMIT        (1*1024*1024)



// binary image that gets linked
// add underscore in front of the variable names when linking 64 bit file
extern unsigned char binary_payload_start;
extern unsigned char binary_payload_end;
extern unsigned char binary_payload_size;


//#pragma comment(lib,"ws2_32.lib") //Winsock Library

void endWithPause(int retcode)
{
    do {
        cout << "Press the Enter key to continue.";
    } while (cin.get() != '\n');

    exit(retcode);
}

int sendDataToSocket(SOCKET *sDeviceSocket, char* pData, int iSize)
{
    int iPackageSize = PACKAGE_SIZE_SEND;
    int iDataCounter=0;
    int iResult = 0;

    printf("Sending data");

    // send all full packages
    for(iDataCounter=0; iDataCounter<(iSize-(iSize % iPackageSize)); iDataCounter+=iPackageSize)
    {
        // send one package of PACKAGE_SIZE_SEND to the socket
        iResult = send(*sDeviceSocket, pData+iDataCounter, iPackageSize, 0);

        if( iResult < 0)
        {
            int iErrorCode = 0;
            iErrorCode = WSAGetLastError();

            printf("send() failed with: %d, WSAGetLastError: %d\n", iResult, iErrorCode);
            return 1;
        }
        else
        {
            printf(".");
        }
    }

    // check for last package with package size smaller than PACKAGE_SIZE_SEND
    if((iSize % iPackageSize) != 0)
    {
        // send the last package < PACKAGE_SIZE_SEND to the socket
        if( send(*sDeviceSocket, pData+iDataCounter, (iSize % iPackageSize), 0) < 0)
        {
            puts("send() failed");
            return 1;
        }
    }

    puts("\nDone");

    return 0;
}

char* receiveDataFromSocket(SOCKET *sDeviceSocket, int &iReceiveSize)
{

    int iReceiveCounter = 1;
    int iResult = 0;
    char * buffer = NULL;
    buffer = (char*) malloc (sizeof(char)*PACKAGE_SIZE_RECV);

    iReceiveSize = 0;


    // Receive until the peer closes the connection
    do {
        iResult = recv(*sDeviceSocket, buffer+iReceiveSize, PACKAGE_SIZE_RECV, 0);

        if ( iResult > 0 )
        {
            puts("Received data");

            // bytes received
            char* newBuffer = NULL;
            iReceiveCounter++;
            iReceiveSize += iResult;

            if(iReceiveSize >= RECEIVE_LIMIT)
            {
                printf("Reached receive limit of 0x%x byte, stop.", RECEIVE_LIMIT);
                // realloc failed, stop here and return bytes receceived until now
                return buffer;
            }

            // resize buffer to receive more data
            newBuffer = (char*) realloc(buffer, PACKAGE_SIZE_RECV*iReceiveCounter);

            // realloc successfull?
            if(newBuffer)
            {
                buffer = newBuffer;
            }
            else
            {
                puts("Could not resize buffer for receiving data");
                // realloc failed, stop here and return bytes receceived until now
                return buffer;
            }
        }
        else if ( iResult == 0 )
        {
            printf("Connection closed\n");
            return buffer;
        }
        else
        {
            int iErrorCode = 0;
            iErrorCode = WSAGetLastError();
            printf("recv failed with: %d, WSAGetLastError: %d\n", iResult, iErrorCode);
            return buffer;
        }
    } while( iResult > 0 );

    return buffer;
}

int main(int argc , char *argv[])
{
    WSADATA wsa;
    SOCKET s;
    char *pBinaryContent = NULL;
    char *pReceivedData = NULL;
    int iFileSize = 0;
    const char* pIpAddress = NULL;
    int iPort = 0;
    int iReceiveSize = 0;

    sockaddr_in server = {0};

    // do we have args to parse?
    if(argc > 1)
    {
        for(int i=1; i<argc; i++)
        {
            if(strcmp(argv[i], "-a") == 0)
            {
                // store the arg after "-a" as IP address
                pIpAddress = argv[i+1];
                i+=1;
            }
            else if(strcmp(argv[i], "-p") == 0)
            {
                // store the arg after "-p" as IP address
                iPort = atoi(argv[i+1]);
                i+=1;
            }
        }
    }

    // check if we had an IP address as argument
    if(pIpAddress == NULL)
    {
        // set IP address to default
        pIpAddress = "192.168.11.100";
    }

    // check if we had a port as argument
    if(iPort == 0)
    {
        iPort = 5001;
    }

    // print parameters to connect
    printf("Connecting to %s, port %d\n", pIpAddress, iPort);

    // get pointer to the file
    pBinaryContent = (char*)&binary_payload_start;

    iFileSize = (int)&binary_payload_size;

    printf("Initialising Winsock...");
    if (WSAStartup(MAKEWORD(2,2),&wsa) != 0)
    {
        printf("Failed. Error Code : %d",WSAGetLastError());
        pBinaryContent = NULL;
        endWithPause(1);
    }

    printf("done.\n");

    // create socket
    if((s = socket(AF_INET , SOCK_STREAM , 0 )) == INVALID_SOCKET)
    {
        printf("Could not create socket : %d" , WSAGetLastError());
        pBinaryContent = NULL;
        endWithPause(1);
    }

    printf("Socket created\n");

    // create server structure with IP address and port
    server.sin_addr.s_addr = inet_addr(pIpAddress);   // smartpick IP?
    server.sin_family = AF_INET;
    server.sin_port = htons( iPort );


    //Connect to remote server
    if (connect(s , (struct sockaddr *)&server , sizeof(server)) < 0)
    {
        puts("connect error");
        pBinaryContent = NULL;
        closesocket(s);
        WSACleanup();
        endWithPause(1);
    }

    puts("Connected");


    // send data to socket
    if(sendDataToSocket(&s, pBinaryContent, iFileSize))
    {
        puts("Could not send data");
        endWithPause(1);
    }

    pBinaryContent = NULL;


    pReceivedData = receiveDataFromSocket(&s, iReceiveSize);

    if(pReceivedData == NULL)
    {
        puts("Did not receive any data from HPC!");

        closesocket(s);
        WSACleanup();
        endWithPause(1);
    }
    else
    {
        // create \0
        pReceivedData[iReceiveSize] = 0;

        //printf("received %d bytes of data:\n", iReceiveSize);
        puts(pReceivedData);
    }

    free(pReceivedData);

    closesocket(s);
    WSACleanup();

    endWithPause(0);
}
