HOMEPAGE = "https://huggingface.co/ggml-org/gemma-3-1b-it-GGUF"
SUMMARY = "Gemma 3, a multimodal addition to the Gemma family of lightweight open models."

LICENSE = "Gemma"
NO_GENERIC_LICENSE[Gemma] = "LICENSE.gemma"
LIC_FILES_CHKSUM = "file://LICENSE.gemma;md5=787cd1b0a0021f2d9135d2a960e29989"

MODEL_REPO_ID ??= "ggml-org/gemma-3-1b-it-GGUF"

SRC_URI += "file://LICENSE.gemma"

inherit hf-model
