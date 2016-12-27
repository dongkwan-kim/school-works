#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/socket.h>
#include <sys/types.h>
#include <unistd.h>
#include <netinet/in.h>
#include <stdint.h>

#define BUF_SIZE 10*1000*1000 - 8
#define S_BUF_SIZE 10*1000*1000
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
typedef struct __attribute__((packed)) protocol {
    uint8_t op;        // 1
    uint8_t shift;     // 1
    uint16_t checksum; // 2
    uint32_t length;     // 4
} header;

/* function prototype */
int check_checksum(uint16_t checksum_from_client, header *hdptr, const char *buf, unsigned bsize);
int check_socket(int sock);
uint16_t checksum(header *hdptr, const char *buf, unsigned bsize);
void check_error(char *msg);
void check_protocol_violence(header *hdptr, char *buf, int client_sock);
void print_bit_of_header(header *hdptr);

int main(int argc, char * argv[]) {
    
    /* socekt variable */
    int sock;
    struct sockaddr_in server_addr;

    /* buffer variable */
    char *buf;
    char *server_buf;
    int str_length, rcv_all_len;

    /* parsing variable */
    int flag;
    char o_host[20];
    int o_port;
    uint8_t o_opt, o_shift;
    
    /* header variable */
    header *hdptr;
    header *server_hdptr;

    if(argc != 9) {
        // fprintf(stderr, "stderr: inappropriate arguments to run %s\n", argv[0]);
        exit(1);
    }
    /* command parsing */
    while((flag = getopt(argc, argv, "h:p:o:s:")) != -1){
        switch(flag){
            case 'h':
                strcpy(o_host, optarg);
                break;
            case 'p':
                o_port = atoi(optarg);
                break;
            case 'o':
                o_opt = atoi(optarg);
                break;
            case 's':
                o_shift = atoi(optarg) % 26;
                break;
        }       
    }
    
    sock = socket(PF_INET, SOCK_STREAM, 0); 
    check_socket(sock);

    memset(&server_addr, 0, sizeof(server_addr));
    
    server_addr.sin_family = AF_INET;
    server_addr.sin_addr.s_addr = inet_addr(o_host);
    server_addr.sin_port = htons(o_port);
    
    if(connect(sock, (SA *)&server_addr, sizeof(server_addr)) < 0){
        check_error("connect");
    }

    // fprintf(stderr, "stderr: Successfully Connected\n");
   
    buf = (char *)malloc(BUF_SIZE);
    server_buf = (char *)malloc(S_BUF_SIZE);        
    
    while(1){
        
        /* buffer initialize */
        memset(buf, 0, BUF_SIZE);
        memset(server_buf, 0, S_BUF_SIZE);

        fgets(buf, BUF_SIZE, stdin);
        if(feof(stdin)){
            /* if string left, keep going */
            if(strlen(buf) == 0){
                // fprintf(stderr, "stderr: EOF: %d\n", feof(stdin));
                break;
            }
        }
        hdptr = (header *)malloc(sizeof(header));
    
        /* set header: op, shift, length, checksum */
        hdptr->op = (uint8_t)(o_opt);
        hdptr->shift = (uint8_t)(o_shift);
        hdptr->length = (uint32_t)htonl(sizeof(header)+strlen(buf));
        hdptr->checksum = checksum(hdptr, buf, strlen(buf));
        
        write(sock, hdptr, 8);
        write(sock, buf, strlen(buf));

        rcv_all_len = 0;
        while(rcv_all_len < (8+strlen(buf))){
            str_length = read(sock, server_buf + rcv_all_len, S_BUF_SIZE);
            if(str_length < 0){
                check_error("read");   
            }
            rcv_all_len += str_length;
        } 
        
        /* set server header to check violence */
        server_hdptr = (header *)malloc(sizeof(header));
        server_hdptr->op = *(uint8_t *) &server_buf[0];
        server_hdptr->shift = *(uint8_t *) &server_buf[1];
        server_hdptr->checksum = *(uint16_t *) &server_buf[2];
        server_hdptr->length = *(uint32_t *) &server_buf[4];
        
        /* protocol check */
        check_protocol_violence(server_hdptr, server_buf, sock);

        // fprintf(stderr, "stderr: from server(%dByte): %s", rcv_all_len, server_buf+8);
        fprintf(stdout, "%s", server_buf+8);
        
    }
    close(sock);
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

int check_socket(int sock){
    if (sock == -1){
        check_error("Error: socket");
    }
}

uint16_t checksum(header *hdptr, const char *buf, unsigned bsize) {
    uint64_t sum = 0;
    int i;
    
    /* add header bits to checksum */
    sum += ((uint16_t)hdptr->op);
    sum += ((uint16_t)hdptr->shift)<<8;
    sum += (hdptr->length) & 0xFFFF;
    sum += ((hdptr->length)>>16) & 0xFFFF; 

    /* Accumulate checksum */
    for (i = 0; i < bsize - 1; i += 2) {
        unsigned short word16 = *(unsigned short *) &buf[i];
        sum += word16;
    }

    /* Handle odd-sized case */
    if (bsize & 1) {
        unsigned short word16 = (unsigned char) buf[i];
        sum += word16;
    }

    /* Fold to get the ones-complement result */
    while (sum >> 16) {
        sum = (sum & 0xFFFF) + (sum >> 16);
    }
    /* Invert to get the negative in ones-complement arithmetic */
    return ~sum;
}

void check_error(char *msg){
    perror(msg);
    exit(1);
}

void check_protocol_violence(header *hdptr, char *buf, int client_sock){
    uint16_t checksum_from_client = hdptr->checksum;
    if(check_checksum(checksum_from_client,
                        hdptr, buf+8, strlen(buf+8)) != 1){
        // fprintf(stderr, "stderr: checksum error\n");
        close(client_sock);
        exit(1);
    }
    
    if(ntohl(hdptr->length) != (strlen(buf + 8) + 8)){
        // fprintf(stderr, "stderr: length error\n");
        close(client_sock);
        exit(1);
    }
    
    if(hdptr->op != 0 && hdptr->op != 1){
        // fprintf(stderr, "stderr: op error\n");
        close(client_sock);
        exit(1);
    }
    
    if(hdptr->shift < 0){
        fprintf(stderr, "stderr: shift error\n");
        close(client_sock);
        exit(1);
    }
}

void print_bit_of_header(header *hdptr){
    int i;
    unsigned long long val;
    val = *((unsigned long long*)hdptr);
    unsigned long long one = 1;
    for(i=0; i < 64; i++){
        fprintf(stderr, "%d ", val >> i & 1);
        if(i == 7 || i == 15 || i == 31){
            fprintf(stderr, "\n");
        }
    }
    fprintf(stderr, "\n");
}
