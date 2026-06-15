HOMEPAGE = "github.com/NVIDIA/open-gpu-kernel-modules"
DESCRIPTION = "NVIDIA Linux Open GPU Kernel Module Source"
LICENSE = "MIT & GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=1d5fa2a493e937d5a4b96e5e03b90f7c"

inherit module

SRC_URI = "git://github.com/NVIDIA/open-gpu-kernel-modules.git;protocol=https;branch=main"
SRCREV = "2ccbad25e1af6a6ee6f38cf569f89f8b65d658ab"

MAKE_TARGETS = "modules"
MODULES_MODULE_SYMVERS_LOCATION = "kernel-open"
EXTRA_OEMAKE += " \
    KERNEL_OUTPUT=${STAGING_KERNEL_BUILDDIR} \
    KERNEL_SOURCES=${STAGING_KERNEL_DIR} \
    V=1 \
    TARGET_ARCH=${TARGET_ARCH} \
    ARCH=${TARGET_ARCH} \
 "

COMPATIBLE_MACHINE = "(genericx86-64|qemux86-64|intel-x86-64)"

