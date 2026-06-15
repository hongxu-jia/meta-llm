HOMEPAGE = "https://ollama.com"
SUMMARY = "Get up and running with large language models."
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://src/import/LICENSE;md5=a8abe7311c869aba169d640cf367a4af"

# Specify the first two important SRCREVs as the format
SRCREV_FORMAT = "ollama_cgroups"
SRCREV_ollama = "8c8f8f3450d39735355fc6cd7f2e436c8aa42ab1"

SRC_URI = " \
    git://github.com/ollama/ollama.git;name=ollama;branch=main;protocol=https;destsuffix=${GO_SRCURI_DESTSUFFIX} \
    file://ollama.service \
    file://modules.txt \
"
SRC_URI:append:arm = " \
    file://0001-x-mlxrunner-mlx-memory.go-fix-overflows-int.patch;patchdir=./src/import \
    file://0001-support-32bit-arm.patch;patchdir=./src/import \
"
SRC_URI:append:x86 = " \
    file://0001-x-mlxrunner-mlx-memory.go-fix-overflows-int.patch;patchdir=./src/import \
    file://0001-fix-compile-failure-on-32bit-x86.patch;patchdir=./src/import \
    file://0001-support-32bit-x86.patch;patchdir=./src/import \
"

include src_uri.inc
inherit go goarch
inherit systemd cmake useradd

GO_IMPORT = "import"

do_fetch[depends] += "git-lfs-native:do_populate_sysroot"

DEPENDS += " \
    rsync-native \
"

RDEPENDS:${PN} += " \
    ca-certificates \
"

# Workaround compile failure on qemuarm
# |recipe-sysroot-native/usr/lib/arm-wrs-linux-gnueabi/gcc/arm-wrs-linux-gnueabi/15.2.0/include/arm_neon.h:7540:1:
# error: inlining failed in call to 'always_inline' 'float32x4_t vcvt_f32_f16(float16x4_t)': target specific option mismatch
TARGET_CC_ARCH:append:arm = " ${@bb.utils.contains("TUNE_FEATURES", "neon","-mfpu=neon-fp16 -mfp16-format=ieee","",d)}"

OECMAKE_SOURCEPATH = "${S}/src/import"

export OECMAKE_FORCE_CROSSCOMPILING = '1'

PIEFLAG = "${@bb.utils.contains('GOBUILDFLAGS', '-buildmode=pie', '-buildmode=pie', '', d)}"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'openclaw', d)}"
PACKAGECONFIG[openclaw] = ",,,nodejs-npm nodejs git"

do_compile:append() {

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

    ${GO} build -x -v ${GOBUILDFLAGS} -o ${B}/bin/ollama -ldflags="-X=github.com/ollama/ollama/version.Version=${PV} -X=github.com/ollama/ollama/server.mode=release"
}

do_install:append() {
    install -d ${D}${systemd_unitdir}/system
    install -m 0644 ${UNPACKDIR}/ollama.service ${D}${systemd_unitdir}/system

    install -d ${D}/${bindir}
    install ${B}/bin/ollama ${D}/${bindir}
}
do_install:append:tegra() {
    sed -i -e '/User=ollama/d' -e '/Group=ollama/d' ${D}${systemd_unitdir}/system/ollama.service
}

FILES:${PN} += "${systemd_unitdir} ${nonarch_libdir}/ollama"

USERADD_PACKAGES = "${PN}"
USERADD_PARAM:${PN} = " \
    --system --shell /bin/false \
    --user-group --groups video,render \
    --create-home --home-dir /usr/share/ollama \
    ollama"
GROUPADD_PARAM:${PN} = "-r render"

INSANE_SKIP:${PN} += "libdir dev-so file-rdeps"
INSANE_SKIP:${PN}-dbg += "libdir dev-so file-rdeps"

SYSTEMD_SERVICE:${PN} = "ollama.service"

include relocation.inc

python __anonymous() {
    if 'ollama' not in (d.getVar('DISTRO_FEATURES_NATIVE') or "").split():
        raise bb.parse.SkipRecipe("Recipe ollama requires 'ollama' in DISTRO_FEATURES_NATIVE")
}

COMPATIBLE_HOST = "(x86_64.*|i.86.*|arm.*|aarch64).*-linux"
