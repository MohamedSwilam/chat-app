## Description

Chat application using springboot

## Setup Using Docker

### Install image
```bash
$ docker build -t chat-app .
```

### Run container on port 8080
```bash
$ docker run -dp 8080:8080 chat-app
```

## Running the app
Open [http://localhost:8080](http://localhost:8080/) in your browser
```bash
Username: mohamed - Password: pass123
Or
Username: ehab - Password: 123
Or
Username: swilam - Password: 123
```

## Show chat statistics
```bash
Stats could be found in db/{username}-statistics.txt
or
GET: http://localhost:8080/statistics/{username}
```

