# cs341

## proj1 Socket Programming
- Implement a string encryption/decryption service
- A client sends a string and then server encrypts/decrypts the string and returns it to the client
- Maximum size of each message is limited to 10M
- server.c is multiprocess server using fork()
- server_select.c is non-blocking server using select()
```
./client -h 143.248.111.222 -p 1234 -o 0 -s 5
./server -p 1234
./server_select -p 1234
```

## proj 2-4 KENSv3
- [KAIST Educational Network System](https://github.com/ANLAB-KAIST/KENSv3)

## proj 5 Disruption-tolerant Application
- Implement disruption-tolerant application with Android
- Implement a string encryption/decryption requester similar to proj1
