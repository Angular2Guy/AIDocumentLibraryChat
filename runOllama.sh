#docker run -d --gpus=all -v ollama:/root/.ollama -p 11434:11434 --name ollama ollama/ollama
docker run -d -v ollama:/root/.ollama -p 11434:11434 --name ollama ollama/ollama
docker start ollama
docker stop ollama
#docker exec -it ollama ollama run orca2:13b
docker exec -it ollama ollama run stable-beluga:13b
#docker exec -it ollama ollama run falcon:40b
#docker exec -it ollama ollama run sqlcoder:15b
#docker exec -it ollama ollama run mixtral:8x7b-text-v0.1-q6_K
#docker exec -it ollama ollama run llava:34b-v1.6-q6_K
#docker exec -it ollama ollama run granite-code:20b
#docker exec -it ollama ollama run deepseek-coder-v2:16b
#docker exec -it ollama ollama run llama3.1:70b
#docker exec -it ollama ollama run qwen2.5:32b
#docker exec -it ollama ollama run codestral:22b
#docker exec -it ollama ollama run llama3.1:8b
#docker exec -it ollama ollama run qwen2.5:14b
#docker exec -it ollama ollama run llama3.2-vision:11b
#docker exec -it ollama ollama run devstral:24b
#docker exec -it ollama bash