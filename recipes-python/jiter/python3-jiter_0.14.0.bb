SUMMARY = "Fast iterable JSON parser."
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=aa97bb3778992892e226b4504b83b60c"

PYPI_PACKAGE = "jiter"

require ${BPN}-crates.inc

inherit pypi cargo-update-recipe-crates python3native rust python_maturin

SRC_URI[sha256sum] = "e8a39e66dac7153cf3f964a12aad515afa8d74938ec5cc0018adcdae5367c79e"
