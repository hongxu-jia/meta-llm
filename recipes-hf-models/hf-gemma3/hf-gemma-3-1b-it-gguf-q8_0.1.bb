require hf-gemma3.inc

MODEL_NAME ??= "gemma-3-1b-it-Q8_0.gguf"
SRC_URI[model.sha256sum] = "b205840c5dcef55078e37d344677869a714ffd42a4ae448c48dcfb52e4bb10d5"

MODEL_SERVICE_CMD += "--alias gemma-3-1b-it-Q8_0  --port 8082"
