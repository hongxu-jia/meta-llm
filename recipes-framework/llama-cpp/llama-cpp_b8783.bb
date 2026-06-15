HOMEPAGE = "https://github.com/ggml-org/llama.cpp"
SUMMARY = "LLM inference in C/C++"
DESCRIPTION = "The main goal of llama.cpp is to enable LLM inference with \
minimal setup and state-of-the-art performance on a wide range of hardware - \
locally and in the cloud."
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${S}/LICENSE;md5=223b26b3c1143120c87e2b13111d3e99"

SRC_URI = "\
    git://github.com/ggml-org/llama.cpp.git;protocol=https;branch=master \
"
SRCREV = "e21cdc11a0461d8b0cbd28cc356d993bf6be7282"

DEPENDS += "openssl"

RDEPENDS:${PN} += " \
    ca-certificates \
"

inherit cmake

EXTRA_OECMAKE = "\
    -DCMAKE_BUILD_TYPE=Release \
    -DLLAMA_BUILD_TESTS=OFF \
"

# Workaround compile failure on qemuarm
# |recipe-sysroot-native/usr/lib/arm-wrs-linux-gnueabi/gcc/arm-wrs-linux-gnueabi/15.2.0/include/arm_neon.h:7540:1:
# error: inlining failed in call to 'always_inline' 'float32x4_t vcvt_f32_f16(float16x4_t)': target specific option mismatch
TARGET_CC_ARCH:append:arm = " ${@bb.utils.contains("TUNE_FEATURES", "neon","-mfpu=neon-fp16 -mfp16-format=ieee","",d)}"

# Workaround compile failure while DEBUG_BUILD = "1"
# examples/gguf-hash/deps/xxhash/xxhash.h:4820:1: error: inlining failed in call to
# 'always_inline' 'XXH3_accumulate_sse2': function not considered for inlining
CFLAGS:append = " ${@oe.utils.vartrue('DEBUG_BUILD', '-DXXH_NO_INLINE_HINTS=1', '', d)}"
