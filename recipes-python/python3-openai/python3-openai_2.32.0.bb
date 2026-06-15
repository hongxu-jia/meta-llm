DESCRIPTION = "The OpenAI Python library provides convenient access to the OpenAI REST API from any Python 3.9+ application."
HOMEPAGE = "https://github.com/openai/openai-python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=0ea6c924de5bc3823bdb7280ae3096c1"

SRC_URI = "git://github.com/openai/openai-python.git;protocol=https;branch=main \
           file://0001-unversion-hatchling.patch \
"
SRCREV = "e507a4ebeea4c3f93cd48986014a3e2ca79230c2"

inherit python_hatchling

DEPENDS += "python3-hatch-fancy-pypi-readme-native"

RDEPENDS:${PN} += " \
    python3-json \
    python3-httpx \
    python3-pydantic \
    python3-typing-extensions \
    python3-anyio \
    python3-distro \
    python3-sniffio \
    python3-tqdm \
    python3-jiter \
"
