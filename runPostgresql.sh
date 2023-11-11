#!/bin/sh
docker pull ankane/pgvector:latest
docker run --name aidoclibchat-postgres -e POSTGRES_PASSWORD=sven1 -e POSTGRES_USER=sven1 -e POSTGRES_DB=aidoclibchat -p 5432:5432 -d ankane/pgvector
# docker start aidoclibchat-postgres
# docker stop aidoclibchat-postgres