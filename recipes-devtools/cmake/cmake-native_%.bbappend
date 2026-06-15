FILESEXTRAPATHS:prepend := "${THISDIR}/files:"
SRC_URI:append = " \
    ${@bb.utils.contains('DISTRO_FEATURES', 'ollama', 'file://0001-support-RUNTIME_DEPENDENCIES-for-oe-cross-compiling.patch', '', d)}  \
"
