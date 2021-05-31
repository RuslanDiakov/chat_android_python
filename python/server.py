from socket import AF_INET, socket, SOCK_STREAM
from threading import Thread
import json

with open('config.json') as json_file:
    config = json.load(json_file)

template_reset = ''
with open('template.txt') as file:
    template_reset = file.read()

with open('chat.txt', 'w') as file:
    file.write(template_reset)


def get_new_connections():
    while True:
        client, client_addresses = SERVER.accept()
        print('%s:%s has connected.' % client_addresses)
        #client.send(bytes(str(client_addresses)+' has connected', 'utf8'))

        addresses[client] = client_addresses
        Thread(target=handle_client, args=(client,)).start()


def handle_client(client):
    global users_count
    users_count += 1
    name = 'User ' + str(users_count)
    welcome = 'Welcome %s!' % name

    client.send(bytes(welcome, 'utf8'))
    msg = '%s has joined the chat!' % name
    broadcast(bytes(msg, 'utf8'))
    clients[client] = name

    with open('chat.txt', 'a', encoding='utf8') as file:
        file.write(msg + '\n')

    while True:
        msg = client.recv(BUFSIZ)
        if msg != bytes('{quit}', 'utf8'):
            broadcast(msg, name+': ')

            with open('chat.txt', 'a', encoding='utf8') as file:
                file.write(name + ': ' + msg.decode("utf-8") + '\n')
        else:
            client.close()
            del clients[client]
            broadcast(bytes('%s has left the chat.' % name, 'utf8'))
            with open('chat.txt', 'a') as file:
                file.write(name + ' has left the chat.' + '\n')
            break


def broadcast(msg, prefix=''):
    for sock in clients:
        sock.send(bytes(prefix, 'utf8')+msg)


clients = {}
addresses = {}
users_count = 0

HOST = config['ip']
PORT = config['port']
BUFSIZ = 1024
ADDR = (HOST, PORT)

# Create a TCP/HOST socket
SERVER = socket(AF_INET, SOCK_STREAM)
SERVER.bind(ADDR)

if __name__ == '__main__':
    SERVER.listen(5)
    print('Waiting for connection...')
    ACCEPT_THREAD = Thread(target=get_new_connections)
    ACCEPT_THREAD.start()
    ACCEPT_THREAD.join()
    SERVER.close()
