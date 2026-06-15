HOMEPAGE = "https://unsloth.ai/docs/models/qwen3.5"
SUMMARY = "Qwen3.5 represents a significant leap forward, integrating breakthroughs in \
multimodal learning, architectural efficiency, reinforcement learning scale, and global \
accessibility to empower developers and enterprises with unprecedented capability and \
efficiency."
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE.qwen3.5;md5=df00629ae3bad0176ff298bfe6bc4339"
SRC_URI += "${MODEL_ENDPOINT}Qwen/Qwen3.5-0.8B/${RESOLVE}/LICENSE;name=license;downloadfilename=LICENSE.qwen3.5"
SRC_URI[license.sha256sum] = "bbedc3fda3305820b977265f01b8619d87570a6739de3a5582c3464840f1e57a"

inherit hf-model
