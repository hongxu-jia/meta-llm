require hf-qwen3.5.inc

MODEL_REPO_ID ??= "unsloth/Qwen3.5-2B-GGUF"
MODEL_NAME ??= "Qwen3.5-2B-UD-Q4_K_XL.gguf"
MODEL_SERVICE_CMD += " \
    --alias Qwen3.5-2B \
    --port 8084 \
    --ctx-size 16384 \
    --temp 0.7 \
    --top-p 0.8 \
    --top-k 20 \
    --min-p 0.00 \
    --reasoning off \
    --reasoning-budget 0 \
"

SRC_URI[model.sha256sum] = "0af96165ea615bea39a04118d63f0b6d35908aea850ee4a51aa6151d851b8b35"
