FILESEXTRAPATHS:prepend := "${LAYER_PATH_meta-llm}/recipes-hf-models/files/:"

MODEL_ENDPOINT ??= "https://huggingface.co/"
RESOLVE ??= "resolve/main"

HF_MODEL_GGUF_URL ??= "${MODEL_ENDPOINT}${MODEL_REPO_ID}/${RESOLVE}/${MODEL_NAME};name=model"

SRC_URI += " \
    ${HF_MODEL_GGUF_URL} \
    file://model.service.in \
"

RDEPENDS:${PN} = " \
    llama-cpp \
"

S = "${UNPACKDIR}"

do_configure[noexec] = "1"
do_compile[noexec] = "1"

GGUF_MODEL_DIR ??= "${datadir}/llama.cpp/gguf_model/"

MODEL_SERVICE_CMD ?= "/usr/bin/llama-server --model ${GGUF_MODEL_DIR}${MODEL_NAME} --host 0.0.0.0"

do_install() {
    install -d ${D}${GGUF_MODEL_DIR}
    install -m 0644 ${S}/${MODEL_NAME} ${D}${GGUF_MODEL_DIR}

    install -d ${D}${systemd_unitdir}/system
    install -m 0644 ${UNPACKDIR}/model.service.in ${D}${systemd_unitdir}/system/${PN}.service
    sed -i -e "s/#MODEL_NAME#/${MODEL_NAME}/g" \
        -e  "s|#MODEL_SERVICE_CMD#|${MODEL_SERVICE_CMD}|g" \
        ${D}${systemd_unitdir}/system/${PN}.service
}

FILES:${PN} += "${datadir} ${systemd_unitdir}"

EXCLUDE_FROM_WORLD = "1"

PACKAGE_ARCH = "all"

SYSTEMD_SERVICE:${PN} = "${PN}.service"
SYSTEMD_AUTO_ENABLE:${PN} ?= "disable"

inherit allarch systemd features_check

REQUIRED_DISTRO_FEATURES = "llama-cpp"
