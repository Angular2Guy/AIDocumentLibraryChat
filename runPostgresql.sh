#!/bin/sh
docker pull ankane/pgvector:latest
docker run --name aidoclibchat-postgres -e POSTGRES_PASSWORD=sven1 -e POSTGRES_USER=sven1 -e POSTGRES_DB=aidoclibchat -p 5432:5432 -d ankane/pgvector
docker run --name aidoclibchat-postgres-ollama -e POSTGRES_PASSWORD=sven1 -e POSTGRES_USER=sven1 -e POSTGRES_DB=aidoclibchat -p 5432:5432 -d ankane/pgvector
docker run --name aidoclibchat-postgres-ollama2 -e POSTGRES_PASSWORD=sven1 -e POSTGRES_USER=sven1 -e POSTGRES_DB=aidoclibchat -p 5432:5432 -d ankane/pgvector
# docker start aidoclibchat-postgres
# docker stop aidoclibchat-postgres
# docker start aidoclibchat-postgres-ollama
# docker stop aidoclibchat-postgres-ollama
# docker start aidoclibchat-postgres-ollama2
# docker stop aidoclibchat-postgres-ollama2