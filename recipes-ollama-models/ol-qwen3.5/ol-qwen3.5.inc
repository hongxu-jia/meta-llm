HOMEPAGE = "https://ollama.com/library/qwen3.5"
SUMMARY = "Qwen 3.5 is a family of open-source multimodal models that delivers \
exceptional utility and performance."
LICENSE = "Apache-2.0"

BPN = "qwen3.5"

PARAMS_SHA256 = "9371364b27a52acac9d87f88bd93c9db1174d8d6ec57f6888925cdc1788871ff"

inherit ollama-model

# No template for qwen3.5
SRC_URI_TEMPLATE = ""

FILES:${PN}-common = " \
    ${datadir}/ollama/models/blobs/sha256-${PARAMS_SHA256} \
"
