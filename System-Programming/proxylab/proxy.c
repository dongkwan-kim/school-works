/*
* proxy.c - CS:APP Web proxy
*
* TEAM MEMBERS:
*     todokaist
*
*	Part1. Sequential proxy only
*   Part2. Multiple requests concurrently
*
*/

#include "csapp.h"

/*
* Function prototypes
*/
int parse_uri(char *uri, char *target_addr, char *path, int  *port);
void format_log_entry(char *logstring, struct sockaddr_in *sockaddr, char *uri, int size);
void *doit(void *ciptr_arg);
void save_log(char *logstring);

ssize_t Rio_readn_w(int fd, void *usrbuf, size_t n);
ssize_t Rio_readlineb_w(rio_t *rp, void *usrbuf, size_t maxlen);
ssize_t Rio_writen_w(int fd, void *usrbuf, size_t n);

int open_clientfd_ts(char *hostname, int port);
int Open_clientfd_ts(char *hostname, int port);

/* structure for client address and file descriptor */
struct client_info {
	struct sockaddr_in clientaddr;
	int connfd;
};

/* semaphore */
sem_t sem_log;
sem_t sem_clientfd;

/*
* main - Main routine for the proxy program
*/
int main(int argc, char **argv)
{
	int listenfd, port, clientlen;
	struct client_info *ciptr;
	pthread_t tid;

	/* Ignore SIGPIPE signals */
	Signal(SIGPIPE, SIG_IGN);

	/* Init semaphores */
	Sem_init(&sem_clientfd, 0, 1);
	Sem_init(&sem_log, 0, 1);

	/* Check arguments */
	if (argc != 2) {
		fprintf(stderr, "Usage: %s <port number>\n", argv[0]);
		exit(0);
	}
	port = atoi(argv[1]);

	/* Opening a listening socket */
	listenfd = Open_listenfd(port);

	ciptr = (struct client_info *)malloc(sizeof(struct client_info));
	while (1){ /* Typical infinite server loop */
		clientlen = sizeof(ciptr->clientaddr);

		/* repeatedly accepting a connection request */
		ciptr->connfd = Accept(listenfd, (SA *)&(ciptr->clientaddr), &clientlen);

		/* do proxy's job */
		Pthread_create(&tid, NULL, doit, (void *)ciptr);
		/* In part1. doit((void *)ciptr); */
	}
	Close(listenfd);
	exit(0);
}

/*
* doit: do proxy's job.
*   - read the entire HTTP request into a buffer
*   - get the hostname, pathname, and port
*   - connect to the end server and forward the request to it
*   - receive reply form server and forward on to client
* */
void *doit(void *ciptr_arg) {
	Pthread_detach(Pthread_self());

	struct client_info* ciptr = (struct client_info*)ciptr_arg;
	char logstring[MAXLINE];
	char buf[MAXLINE], method[MAXLINE], version[MAXLINE];
	char uri[MAXLINE], host_name[MAXLINE], path_name[MAXLINE];
	int port, n_byte, t_byte;
	int svrfd;
	rio_t client_rio, server_rio;

	int connfd = ciptr->connfd;
	struct sockaddr_in clientaddr = ciptr->clientaddr;

	/* Read request line and headers */
	Rio_readinitb(&client_rio, connfd);
	n_byte = Rio_readlineb_w(&client_rio, buf, MAXLINE);
	if (n_byte <= 0){
		Close(connfd);
		return (void*)0;
	}

	/* Read and parse the request line */
	sscanf(buf, "%s %s %s", method, uri, version);
	/* Read only GET method */
	if (strcasecmp(method, "GET")){
		Close(connfd);
		return (void*)0;
	}
	if (parse_uri(uri, host_name, path_name, &port) < 0){
		Close(connfd);
		return (void*)0;
	}

	/* Connect to the end server */
	svrfd = Open_clientfd(host_name, port);
	if (svrfd < 0){
		Close(connfd);
		return (void*)0;
	}

	/* forward the request to the end server */
	Rio_readinitb(&server_rio, svrfd);
	Rio_writen_w(svrfd, buf, strlen(buf));
	while ((n_byte = Rio_readlineb_w(&client_rio, buf, MAXLINE)) > 0){
		if (strcmp(buf, "\r\n")){
			Rio_writen_w(svrfd, buf, strlen(buf));
		}
		else {
			break;
		}
	}
	Rio_writen_w(svrfd, "\r\n", 2);

	/* receive reply form server and forward on to client */
	while ((n_byte = Rio_readn_w(svrfd, buf, MAXLINE)) > 0){
		Rio_writen_w(connfd, buf, n_byte);
		t_byte += n_byte;
	}

	/* Close server, client and save logs */
	Close(connfd);
	Close(svrfd);
	format_log_entry(logstring, &(clientaddr), uri, t_byte);
	save_log(logstring);
	return (void*)0;
}

void save_log(char *logstring){
	/* protecting all updates of the log file */
	P(&sem_log);
	FILE *log_file;
	log_file = fopen("proxy.log", "a");
	fprintf(log_file, "%s", logstring);
	fclose(log_file);
	V(&sem_log);
}

/*
* open_clientfd_ts and its wraper
*
* */
int open_clientfd_ts(char *hostname, int port)
{
	int clientfd;
	struct hostent *hp;
	struct sockaddr_in serveraddr;

	if ((clientfd = socket(AF_INET, SOCK_STREAM, 0)) < 0)
		return -1; /* check errno for cause of error */

	/* protecting calls to any thread unsafe functions */
	P(&sem_clientfd);
	/* Fill in the server's IP address and port */
	if ((hp = gethostbyname(hostname)) == NULL)
		return -2; /* check h_errno for cause of error */
	V(&sem_clientfd);

	bzero((char *)&serveraddr, sizeof(serveraddr));
	serveraddr.sin_family = AF_INET;
	bcopy((char *)hp->h_addr_list[0],
		(char *)&serveraddr.sin_addr.s_addr, hp->h_length);
	serveraddr.sin_port = htons(port);

	/* Establish a connection with the server */
	if (connect(clientfd, (SA *)&serveraddr, sizeof(serveraddr)) < 0)
		return -1;
	return clientfd;
}

int Open_clientfd_ts(char *hostname, int port)
{
	int rc;
	if ((rc = open_clientfd_ts(hostname, port)) < 0) {
		if (rc == -1)
			unix_error("Open_clientfd Unix error");
		else
			dns_error("Open_clientfd DNS error");
	}
	return rc;
}

/*
* new wrappers
* */
ssize_t Rio_readn_w(int fd, void *usrbuf, size_t n){
	ssize_t org_ret;
	if ((org_ret = rio_readn(fd, usrbuf, n)) < 0){
		return 0;
	}
	return org_ret;
}

ssize_t Rio_readlineb_w(rio_t *rp, void *usrbuf, size_t maxlen){
	ssize_t org_ret;
	if ((org_ret = rio_readlineb(rp, usrbuf, maxlen)) < 0){
		return 0;
	}
	return org_ret;
}

ssize_t Rio_writen_w(int fd, void *usrbuf, size_t n){
	ssize_t org_ret;
	if ((org_ret = rio_writen(fd, usrbuf, n)) != n){
		return 0;
	}
	return org_ret;
}

/*
* parse_uri - URI parser
*
* Given a URI from an HTTP proxy GET request (i.e., a URL), extract
* the host name, path name, and port.  The memory for hostname and
* pathname must already be allocated and should be at least MAXLINE
* bytes. Return -1 if there are any problems.
*/
int parse_uri(char *uri, char *hostname, char *pathname, int *port)
{
	char *hostbegin;
	char *hostend;
	char *pathbegin;
	int len;

	if (strncasecmp(uri, "http://", 7) != 0) {
		hostname[0] = '\0';
		return -1;
	}

	/* Extract the host name */
	hostbegin = uri + 7;
	hostend = strpbrk(hostbegin, " :/\r\n\0");
	len = hostend - hostbegin;
	strncpy(hostname, hostbegin, len);
	hostname[len] = '\0';

	/* Extract the port number */
	*port = 80; /* default */
	if (*hostend == ':')
		*port = atoi(hostend + 1);

	/* Extract the path */
	pathbegin = strchr(hostbegin, '/');
	if (pathbegin == NULL) {
		pathname[0] = '\0';
	}
	else {
		pathbegin++;
		strcpy(pathname, pathbegin);
	}

	return 0;
}

/*
* format_log_entry - Create a formatted log entry in logstring.
*
* The inputs are the socket address of the requesting client
* (sockaddr), the URI from the request (uri), and the size in bytes
* of the response from the server (size).
*/
void format_log_entry(char *logstring, struct sockaddr_in *sockaddr,
	char *uri, int size)
{
	time_t now;
	char time_str[MAXLINE];
	unsigned long host;
	unsigned char a, b, c, d;

	/* Get a formatted time string */
	now = time(NULL);
	strftime(time_str, MAXLINE, "%a %d %b %Y %H:%M:%S %Z", localtime(&now));

	/*
	* Convert the IP address in network byte order to dotted decimal
	* form. Note that we could have used inet_ntoa, but chose not to
	* because inet_ntoa is a Class 3 thread unsafe function that
	* returns a pointer to a static variable (Ch 13, CS:APP).
	*/
	host = ntohl(sockaddr->sin_addr.s_addr);
	a = host >> 24;
	b = (host >> 16) & 0xff;
	c = (host >> 8) & 0xff;
	d = host & 0xff;


	/* Return the formatted log entry string */
	//Fix : added URI and size to logstring..
	sprintf(logstring, "%s: %d.%d.%d.%d %s %d\n", time_str, a, b, c, d, uri, size);
}
