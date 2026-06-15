HOMEPAGE = "https://github.com/mostlygeek/llama-swap"
SUMMARY = "Run multiple generative AI models on your machine and hot-swap between them on demand."
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://src/import/LICENSE.md;md5=1ee2ee9408fd04e9cf7f852aa9069155"

# Specify the first two important SRCREVs as the format
SRCREV_FORMAT = "llama-swap_cgroups"
SRCREV_llama-swap = "17233e927895b059a793c47f1e7317b481ab5f5d"
SRC_URI[tarball.sha256sum] = "b6e070307ee4ca2e935daac7d32bdf22bfaa479bf994ba57bd5f5e3d6bcce01a"

SRC_URI = " \
    git://github.com/mostlygeek/llama-swap.git;name=llama-swap;branch=main;protocol=https;destsuffix=${GO_SRCURI_DESTSUFFIX} \
    file://ui_dist.tar;name=tarball;subdir=${UNPACKDIR}/${GO_SRCURI_DESTSUFFIX} \
    file://llama-swap.service.in \
    file://modules.txt \
"

include src_uri.inc
inherit go goarch systemd

GO_IMPORT = "import"

PIEFLAG = "${@bb.utils.contains('GOBUILDFLAGS', '-buildmode=pie', '-buildmode=pie', '', d)}"

DEPENDS += " \
    rsync-native \
"

# External config.yaml for llama-swap
EXTERNAL_LLAMA_SWAP_CONFIG = ""

# Support hugging face models
# such as "gemma-3-1b-it-Q4_K_M.gguf gemma-3-1b-it-Q8_0.gguf"
# Assure the model in /usr/share/llama.cpp/gguf_model/
SUPPORT_HF_MODELS ??= ""

do_compile() {

    cd ${S}/src/import

    # Pass the needed cflags/ldflags so that cgo
    # can find the needed headers files and libraries
    export GOARCH=${TARGET_GOARCH}
    export CGO_ENABLED="1"
    export CGO_CFLAGS="${CFLAGS} --sysroot=${STAGING_DIR_TARGET}"
    export CGO_LDFLAGS="${LDFLAGS} --sysroot=${STAGING_DIR_TARGET}"

    export GOFLAGS="-mod=vendor -trimpath ${PIEFLAG}"

    # our copied .go files are to be used for the build
    ln -sf vendor.copy vendor
    # inform go that we know what we are doing
    cp ${UNPACKDIR}/modules.txt vendor/

    ${GO} build -x -v ${GOBUILDFLAGS} -o ${B}/bin/llama-swap \
        -ldflags="-X main.commit=${SRCREV_llama-swap} -X main.version=${PV} -X main.date=$(date +"%Y-%m-%dT%H:%M:%SZ" -d @${SOURCE_DATE_EPOCH})"
}

do_install[vardeps] += "EXTERNAL_LLAMA_SWAP_CONFIG SUPPORT_HF_MODELS"

LLAMA_SWAP_OPTION ?= "-config /usr/share/llama-swap/config.yaml -listen 0.0.0.0:8080"

do_install() {
    install -d ${D}${systemd_unitdir}/system
    install -m 0644 ${UNPACKDIR}/llama-swap.service.in ${D}${systemd_unitdir}/system/llama-swap.service
    sed -i -e  "s|#LLAMA_SWAP_OPTION#|${LLAMA_SWAP_OPTION}|g" \
        ${D}${systemd_unitdir}/system/llama-swap.service

    install -d ${D}/${bindir}
    install ${B}/bin/llama-swap ${D}/${bindir}

    install -d ${D}${datadir}/llama-swap
    if [ -n "${EXTERNAL_LLAMA_SWAP_CONFIG}" -a -e "${EXTERNAL_LLAMA_SWAP_CONFIG}" ]; then
        install -m 0644 ${EXTERNAL_LLAMA_SWAP_CONFIG} ${D}${datadir}/llama-swap/config.yaml
    elif [ -n "${SUPPORT_HF_MODELS}" ]; then
        echo "models:" > ${D}${datadir}/llama-swap/config.yaml
        for model in ${SUPPORT_HF_MODELS}; do
            echo "  $model:" >> ${D}${datadir}/llama-swap/config.yaml
            echo "    cmd: llama-server --port \${PORT} --model ${datadir}/llama.cpp/gguf_model/$model" >> ${D}${datadir}/llama-swap/config.yaml
        done
    else
        touch ${D}${datadir}/llama-swap/config.yaml
    fi
}

FILES:${PN} += "${systemd_unitdir} ${datadir}/llama-swap"

SYSTEMD_SERVICE:${PN} = "llama-swap.service"

include relocation.inc
