FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI += " \
    file://0001-support-cross-compiling.patch;patchdir=./src/import \
"
SRC_URI:append:x86-64 = " \
    file://ollama.path \
"

# Follow meta-tegra
CUDA_VERSION:x86-64 = "12.6"

# native == GPUs available at build time
# 50     == Maxwell, lowest CUDA 12 standard
# 60     == P100, FP16 CUDA intrinsics
# 61     == Pascal, __dp4a instruction (per-byte integer dot product)
# 70     == V100, FP16 tensor cores
# 75     == Turing, int8 tensor cores
# 80     == Ampere, asynchronous data loading, faster tensor core instructions
# 86     == RTX 3000, needs CUDA v11.1
# 89     == RTX 4000, needs CUDA v11.8
#
# XX-virtual == compile CUDA code as PTX, do JIT compilation to binary code on first run
# XX-real    == compile CUDA code as device code for this specific architecture
# no suffix  == compile as both PTX and device code
#
# The default behavior for a non-native is to build virtual architectures as needed to cover all features needed
# or best performance and to also build real architectures for the most commonly used GPUs.
TEGRA_CUDA_ARCHITECTURE:x86-64 = "86"
CUDA_ARCHITECTURES:x86-64 = "${TEGRA_CUDA_ARCHITECTURE}"
CUDA_NVCC_ARCH_FLAGS:x86-64 = "--gpu-architecture=compute_${TEGRA_CUDA_ARCHITECTURE} --gpu-code=sm_${TEGRA_CUDA_ARCHITECTURE}"

inherit cuda

PRIVATE_LIBS:${PN} = "libcublas.so.12 libcublasLt.so.12 libcudart.so.12"
INSANE_SKIP:${PN} += "already-stripped buildpaths"
NVDIA_CUDA ?= ""
NVDIA_CUDA:x86-64 = " \
    nvidia-open-gpu-kernel-module \
    nvidia-driver-x86-64-compute \
"
RDEPENDS:${PN} += " \
    ${NVDIA_CUDA} \
"

do_install:append:x86-64() {
    if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
        install -d ${D}${systemd_unitdir}/system
        install -m 0644 ${UNPACKDIR}/ollama.path ${D}${systemd_unitdir}/system
        sed -i "s|#CUDA#|After=nvidia-device.service\nBindsTo=ollama.path|" ${D}${systemd_unitdir}/system/ollama.service
    fi
}
