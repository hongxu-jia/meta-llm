DESCRIPTION = "The Ollama Python library provides the easiest way to integrate Python 3.8+ projects with Ollama."
HOMEPAGE = "https://github.com/ollama/ollama-python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a8abe7311c869aba169d640cf367a4af"

SRC_URI = "git://github.com/ollama/ollama-python.git;protocol=https;branch=main \
"
SRCREV = "0008226fda83c7b0c6722844313a5adfae30001c"

inherit python_hatchling

DEPENDS += "python3-hatch-vcs-native"

RDEPENDS:${PN} += " \
    python3-json \
    python3-httpx \
    python3-pydantic \
"
