HOMEPAGE = "https://ollama.com/library/gemma3"
SUMMARY = "Gemma is a lightweight, family of models from Google built on Gemini technology. "
LICENSE = "Gemma2"
NO_GENERIC_LICENSE[Gemma2] = "sha256-${LICENSE_SHA256}"
LIC_FILES_CHKSUM = "file://sha256-${LICENSE_SHA256};md5=787cd1b0a0021f2d9135d2a960e29989"
BPN = "gemma3"

TEMPLATE_SHA256 = "e0a42594d802e5d31cdc786deb4823edb8adff66094d49de8fffe976d753e348"
LICENSE_SHA256 = "dd084c7d92a3c1c14cc09ae77153b903fd2024b64a100a0cc8ec9316063d2dbc"
PARAMS_SHA256 = "3116c52250752e00dd06b16382e952bd33c34fd79fc4fe3a5d2c77cf7de1b14b"

inherit ollama-model

FILES:${PN}-common += " \
    ${datadir}/ollama/models/blobs/sha256-${PARAMS_SHA256} \
    ${datadir}/ollama/models/blobs/sha256-${TEMPLATE_SHA256} \
"

