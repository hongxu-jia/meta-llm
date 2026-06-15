do_install:append:x86-64 () {
    rm -f ${D}${libdir}/${TARGET_SYS}/${baselib}/libgcc_s.so*
}
