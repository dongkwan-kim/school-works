#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <arpa/inet.h>
#include <signal.h>
#include <ctype.h>

#include <sys/time.h>
#include <sys/select.h>

#define BUF_SIZE 10*1000*1000
typedef struct sockaddr SA;

/* protocol structure */
/*
* op field       |  8 bits
* shift field    |  8 bits
* checksum field | 16 bits
* length field   | 32 bits
* data
*
* */
typedef struct __attribute__((packed)) protocol{
    uint8_t op;        // 1
    uint8_t shift;     // 1
    uint16_t checksum; // 2
    uint32_t length;     // 4
} header;

/* function prototype */
int check_checksum(uint16_t checksum_from_client, header *hdptr, const char *buf, unsigned bsize);
int open_server_sock(int o_port);
int shift_msg(char *buf, uint8_t op, uint8_t shift);
uint16_t checksum(header *hdptr, const char *buf, unsigned bsize);
void change_checksum(char *buf, uint16_t checksum);
void check_error(char *msg);
void check_protocol_violence(header *hdptr, char *buf, int client_sock, int server_sock);
void read_header(int client_sock, char *buf);

int main(int argc, char *argv[]) {

    /* socket variable */
    int server_sock, client_sock;
    struct sockaddr_in client_addr;
    socklen_t client_len;

    /* select variable */
    fd_set frs, frs_twin;
    int max_fd, num_fd, fidx;

    /* buffer variable */
    char *buf;
    int str_length, rcv_all_len;

    /* header variable */
    header *hdptr;
    hdptr = (header *)malloc(sizeof(header));

    /* parsing variable */
    int flag, o_port;
    if (argc != 3) {
        // fprintf(stderr, "stderr: inappropriate arguments to run %s\n", argv[0]);
        exit(1);
    }
    while ((flag = getopt(argc, argv, "p:")) != -1){
        switch (flag){
        case 'p':
            o_port = atoi(optarg);
            break;
        }
    }

    /* socekt, bind, listen */
    server_sock = open_server_sock(o_port);
    // fprintf(stderr, "stderr: wait for client\n");
    
    /* file descriptor settings */
    FD_ZERO(&frs);
    FD_SET(server_sock, &frs);
    max_fd = server_sock;

    buf = (char *)malloc(BUF_SIZE);
    while(1) {
        frs_twin = frs;
        
        /* select */
        num_fd = select(max_fd + 1, &frs_twin, 0, 0, NULL);
        if (num_fd == 0){
            continue;
        } else if (num_fd < 0) {
            break;
        }
        
        /* searching fd */
        for (fidx = 0; fidx <= max_fd; fidx++) {

            if (FD_ISSET(fidx, &frs_twin)) {
                
                /* connect */
                if(fidx == server_sock){
                    client_len = sizeof(client_addr);
                    client_sock = accept(server_sock, (SA *)&client_addr, &client_len);
                    FD_SET(client_sock, &frs);
                    if (client_sock > max_fd){
                        max_fd = client_sock;
                    }
                    // fprintf(stderr, "stderr: connected: %d\n", client_sock);
                    continue;
                }
                
                /* buffer initialize */
                memset(buf, 0, BUF_SIZE);

                /* header reading */
                read_header(fidx, buf);

                /* set header */
                hdptr->op = *(uint8_t *) &buf[0];
                hdptr->shift = *(uint8_t *) &buf[1];
                hdptr->checksum = *(uint16_t *) &buf[2];
                hdptr->length = *(uint32_t *) &buf[4];
                
                /* msg reading start with 8 */
                rcv_all_len = 8;
                while(rcv_all_len < ntohl(hdptr->length)){
                    str_length = read(fidx, buf + rcv_all_len, BUF_SIZE);
                    rcv_all_len += str_length;
                }

                if (rcv_all_len != 8) {
                    
                    /* protocol check */
                    check_protocol_violence(hdptr, buf, fidx, server_sock);
              
                    /* make Caesar cipher */
                    shift_msg(buf+8, hdptr->op, hdptr->shift);
                
                    /* set new checksum */
                    hdptr->checksum = checksum(hdptr, buf+8, strlen(buf+8));
                    change_checksum(buf, hdptr->checksum);
                
                    write(fidx, buf, ntohl(hdptr->length));

                } else {
                    FD_CLR(fidx, &frs);
                    close(fidx);
                    // fprintf(stderr, "stderr: disconnected: %d\n", fidx);
                }
            }
        }
    }
    close(server_sock);
    // fprintf(stderr, "stderr: Successfully Finished\n");
    return 0;
}

int check_checksum(uint16_t checksum_from_client, header *hdptr, const char *buf, unsigned bsize) {
    uint16_t psum = checksum(hdptr, buf, bsize);
    psum = (psum & 0xFFFF) + (psum >> 16);
    psum = ~psum;
    psum += checksum_from_client;
    if (psum == 0xFFFF){
        return 1;
    }
    else {
        return 0;
    }
}

int open_server_sock(int o_port){
    int server_sock, optval = 1;
    struct sockaddr_in server_addr;

    if ((server_sock = socket(AF_INET, SOCK_STREAM, 0)) < 0){
        check_error("socket");
    }

    if (setsockopt(server_sock, SOL_SOCKET,
        SO_REUSEADDR, (const void *)&optval, sizeof(int)) < 0){
        check_error("setsockopt");
    }

    memset(&server_addr, 0, sizeof(server_addr));
    server_addr.sin_family = AF_INET;
    server_addr.sin_addr.s_addr = htonl(INADDR_ANY);
    server_addr.sin_port = htons((unsigned short)o_port);

    if (bind(server_sock, (SA *)&server_addr, sizeof(server_addr)) < 0){
        check_error("bind");
    }
    if (listen(server_sock, 1024) < 0){
        check_error("listen");
    }

    return server_sock;
}

int shift_msg(char *buf, uint8_t op, uint8_t shift){
    int idx = 0;
    while (buf[idx]){
        buf[idx] = tolower(buf[idx]);
        if (isalpha(buf[idx])){
            if (op == 0){
                buf[idx] = ((buf[idx] - 97) + shift) % 26 + 97;
            }
            else {
                buf[idx] = ((buf[idx] - 97) - shift + 26*((shift/26)+1)) % 26 + 97;
            }
        }
        idx++;
    }
}

uint16_t checksum(header *hdptr, const char *buf, unsigned bsize) {
    uint64_t sum = 0;
    int i;

    /* add header bits to checksum */
    sum += ((uint16_t)hdptr->op);
    sum += ((uint16_t)hdptr->shift) << 8;
    sum += (hdptr->length) & 0xFFFF;
    sum += ((hdptr->length) >> 16) & 0xFFFF;

    /* Accumulate checksum */
    for (i = 0; i < bsize - 1; i += 2) {
        unsigned short word16 = *(unsigned short *)&buf[i];
        sum += word16;
    }

    /* Handle odd-sized case */
    if (bsize & 1) {
        unsigned short word16 = (unsigned char)buf[i];
        sum += word16;
    }

    /* Fold to get the ones-complement result */
    while (sum >> 16) {
        sum = (sum & 0xFFFF) + (sum >> 16);
    }
    /* Invert to get the negative in ones-complement arithmetic */
    return ~sum;
}

void change_checksum(char *buf, uint16_t checksum){
    buf[2] = checksum;
    buf[3] = (checksum) >> 8;
}

void check_error(char *msg){
    perror(msg);
    exit(1);
}

void check_protocol_violence(header *hdptr, char *buf, int client_sock, int server_sock){
    uint16_t checksum_from_client = hdptr->checksum;
    if(check_checksum(checksum_from_client,
                        hdptr, buf+8, strlen(buf+8)) != 1){
        // fprintf(stderr, "stderr: checksum error\n");
        close(client_sock);
        close(server_sock);
        exit(1);
    }
    
    if(ntohl(hdptr->length) != (strlen(buf + 8) + 8)){
        // fprintf(stderr, "stderr: length error\n");
        close(client_sock);
        close(server_sock);
        exit(1);
    }
    
    if(hdptr->op != 0 && hdptr->op != 1){
        // fprintf(stderr, "stderr: op error\n");
        close(client_sock);
        close(server_sock);
        exit(1);
    }
    
    if(hdptr->shift < 0){
        // fprintf(stderr, "stderr: shift error\n");
        close(client_sock);
        close(server_sock);
        exit(1);
    }
}

void read_header(int client_sock, char *buf){
    int hidx;
    char tempc;
    for (hidx = 0; hidx < 8; hidx++){
        if (read(client_sock, &tempc, 1) > 0){
            buf[hidx] = tempc;
        }
    }
}
