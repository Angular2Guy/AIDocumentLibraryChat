#docker run -d --gpus=all -v ollama:/root/.ollama -p 11434:11434 --name ollama ollama/ollama
docker run -d -v ollama:/root/.ollama -p 11434:11434 --name ollama ollama/ollama
docker start ollama
docker stop ollama
#docker exec -it ollama ollama run llava:34b-v1.6-q6_K
#docker exec -it ollama ollama run codestral:22b
#docker exec -it ollama ollama run llama3.2-vision:11b
#docker exec -it ollama ollama run devstral:24b
#docker exec -it ollama ollama run qwen3.8:27b
#docker exec -it ollama bash