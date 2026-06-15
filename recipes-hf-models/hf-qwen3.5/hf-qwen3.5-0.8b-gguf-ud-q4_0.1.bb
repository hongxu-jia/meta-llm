require hf-qwen3.5.inc

MODEL_REPO_ID ??= "unsloth/Qwen3.5-0.8B-GGUF"
MODEL_NAME ??= "Qwen3.5-0.8B-UD-Q4_K_XL.gguf"
MODEL_SERVICE_CMD += " \
    --alias Qwen3.5-0.8B \
    --port 8083 \
    --ctx-size 16384 \
    --temp 0.7 \
    --top-p 0.8 \
    --top-k 20 \
    --min-p 0.00 \
    --reasoning off \
    --reasoning-budget 0 \
"

SRC_URI[model.sha256sum] = "3177ebd67afe4438374da19e690bc1b98756f7e0fea9240e1be404336156a7b5"

