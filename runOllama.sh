#docker run -d --gpus=all -v ollama:/root/.ollama -p 11434:11434 --name ollama ollama/ollama
docker run -d -v ollama:/root/.ollama -p 11434:11434 --name ollama ollama/ollama
docker start ollama
docker stop ollama
#docker exec -it ollama ollama run orca2:13b
docker exec -it ollama ollama run stable-beluga:13bc
#docker exec -it ollama ollama run falcon:40b
#docker exec -it ollama ollama run sqlcoder:15b
#docker exec -it ollama bash