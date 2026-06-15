FILESEXTRAPATHS:append := ":${THISDIR}/cuda-crt"
SRC_URI:append:x86-64:class-target = " file://0001-fix-NVCC-compilation-error-exception-specification-i.patch"
