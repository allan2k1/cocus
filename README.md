The files were loaded via curl at a command prompt in the directory where the desired file is located with the command below:

curl -X POST -F "file=@file.txt" http://localhost:8080/api/files/upload

To download a file, use the command below:

curl -OJL http://localhost:8080/api/files/download/file.txt -v


The rest of the tests were done via Postman.
