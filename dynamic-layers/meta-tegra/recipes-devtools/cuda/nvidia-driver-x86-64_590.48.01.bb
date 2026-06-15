DESCRIPTION = "NVIDIA Linux Driver"
SECTION = "nvidia/drivers"
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://LICENSE;md5=92aa2e2af6aa0bcba1c3fe49da021937"

inherit nvidia-driver-x86-64 systemd

SRC_URI[sha256sum] = "7e66ff6c8318f3f981d442d7451c791fb1e9cf05f45648a3f081242d056dde4f"

SRC_URI += " \
    file://nvidia-device.service \
    file://disable-nouveau.conf \
"

SRC_SUBDIR += "bin sbin"

SRCNAME = "nvidia_driver"

RDEPENDS:${PN} = " \
    pciutils \
"

do_install:append () {
    install -d ${D}${base_libdir}/firmware/nvidia/${PV}
    cp ${S}/firmware/* ${D}${base_libdir}/firmware/nvidia/${PV}
    for cuda_lib in $(ls ${D}${base_libdir}/lib*.so.${PV}); do
        # Create libXXX.so.1 -> libXXX.so.${PV}
        sym_link=$(echo $cuda_lib | sed 's/\(.*\.so\)\.\(.*\)/\1.1/')
        ln -snf ${cuda_lib##*/} $sym_link
    done
    ln -snf /lib ${D}/lib64

    install -d ${D}${systemd_unitdir}/system
    install -m 0644 ${UNPACKDIR}/nvidia-device.service ${D}${systemd_unitdir}/system

    install -d ${D}${sysconfdir}/modprobe.d
    install -m 0644 ${UNPACKDIR}/disable-nouveau.conf ${D}${sysconfdir}/modprobe.d

}

PACKAGES =+ "${PN}-compute"

FILES:${PN}-compute += " \
    ${base_libdir}/libcuda.so* \
    ${base_libdir}/libcudadebugger.so* \
    ${base_libdir}/libnvidia-ml.so* \
    ${base_libdir}/libnvidia-nvvm.so* \
    ${base_libdir}/libnvidia-nvvm70.so* \
    ${base_libdir}/libnvidia-opencl.so* \
    ${base_libdir}/libnvidia-ptxjitcompiler.so* \
    ${base_libdir}/nvidia_drv.so \
    ${base_libdir}/firmware \
    ${sbindir}/nvidia-smi \
    /lib64 \
    ${systemd_unitdir} \
    ${sysconfdir}/modprobe.d \
"

INSANE_SKIP:${PN}-compute = "already-stripped file-rdeps dev-so ldflags arch"
SYSTEMD_PACKAGES = "${PN}-compute"
SYSTEMD_SERVICE:${PN}-compute = "nvidia-device.service"

COMPATIBLE_MACHINE = "(genericx86-64|qemux86-64|intel-x86-64)"
