require hf-gemma3.inc

MODEL_NAME ??= "gemma-3-1b-it-Q4_K_M.gguf"
SRC_URI[model.sha256sum] = "8ccc5cd1f1b3602548715ae25a66ed73fd5dc68a210412eea643eb20eb75a135"

MODEL_SERVICE_CMD += "--alias gemma-3-1b-it-Q4_K_M  --port 8081"
