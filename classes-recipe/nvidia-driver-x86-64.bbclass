SRCNAME ??= "${BPN}"
SRC_URI = "https://developer.download.nvidia.com/compute/nvidia-driver/redist/nvidia_driver/linux-x86_64/${SRCNAME}-linux-x86_64-${PV}-archive.tar.xz;"
S = "${UNPACKDIR}/${SRCNAME}-linux-x86_64-${PV}-archive"
SRC_SUBDIR ?= "lib"
do_configure[noexec] = "1"
do_compile[noexec] = "1"
do_install () {
    rm -rf ${D}/*
    mkdir -p ${D}${root_prefix}
    for subdir in ${SRC_SUBDIR}; do
        cp -rf ${S}/$subdir ${D}${root_prefix}
    done
}

INSANE_SKIP:${PN} = "already-stripped file-rdeps dev-so ldflags arch"
