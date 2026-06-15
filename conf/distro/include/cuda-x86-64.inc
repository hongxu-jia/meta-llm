TEGRA_PKGARCH:x86-64 = "${TUNE_PKGARCH}"
SOC_FAMILY_PKGARCH:x86-64 = "${TUNE_PKGARCH}"
OVERRIDES:prepend = "cuda:"

COMPATIBLE_MACHINE:pn-cuda-compatibility-workarounds:class-target = "(genericx86-64|qemux86-64|intel-x86-64)"
COMPATIBLE_MACHINE:pn-cuda-target-environment:class-target = "(genericx86-64|qemux86-64|intel-x86-64)"
COMPATIBLE_MACHINE:pn-cuda-libraries:class-target = "(genericx86-64|qemux86-64|intel-x86-64)"
COMPATIBLE_MACHINE:pn-libcublas:class-target = "(genericx86-64|qemux86-64|intel-x86-64)"
COMPATIBLE_MACHINE:pn-libcurand:class-target = "(genericx86-64|qemux86-64|intel-x86-64)"
COMPATIBLE_MACHINE:pn-libcufile:class-target = "(genericx86-64|qemux86-64|intel-x86-64)"
COMPATIBLE_MACHINE:pn-cuda-nvcc-headers:class-target = "(genericx86-64|qemux86-64|intel-x86-64)"
COMPATIBLE_MACHINE:pn-libcusolver:class-target = "(genericx86-64|qemux86-64|intel-x86-64)"
COMPATIBLE_MACHINE:pn-libcusparse:class-target = "(genericx86-64|qemux86-64|intel-x86-64)"
COMPATIBLE_MACHINE:pn-libnpp:class-target = "(genericx86-64|qemux86-64|intel-x86-64)"
COMPATIBLE_MACHINE:pn-libnvjitlink:class-target = "(genericx86-64|qemux86-64|intel-x86-64)"
COMPATIBLE_MACHINE:pn-libnvfatbin:class-target = "(genericx86-64|qemux86-64|intel-x86-64)"
COMPATIBLE_MACHINE:pn-cuda-cudart:class-target = "(genericx86-64|qemux86-64|intel-x86-64)"
COMPATIBLE_MACHINE:pn-cuda-nvrtc:class-target = "(genericx86-64|qemux86-64|intel-x86-64)"
COMPATIBLE_MACHINE:pn-cuda-cccl:class-target = "(genericx86-64|qemux86-64|intel-x86-64)"
COMPATIBLE_MACHINE:pn-cuda-crt:class-target = "(genericx86-64|qemux86-64|intel-x86-64)"
COMPATIBLE_MACHINE:pn-cuda-nvml:class-target = "(genericx86-64|qemux86-64|intel-x86-64)"
COMPATIBLE_MACHINE:pn-libcufft:class-target = "(genericx86-64|qemux86-64|intel-x86-64)"
COMPATIBLE_MACHINE:pn-cuda-driver:class-target = "(genericx86-64|qemux86-64|intel-x86-64)"

CUDA_COMPONENTS:remove:x86-64 = "libcudla"

CUSTOMER_RECIPES:tegra += " \
    libgcc-for-nvcc \
    tar-l4t-workaround-native \
    cuda-compatibility-workarounds \
    cuda-target-environment \
    cuda-libraries \
    libcublas \
    libcurand \
    libcufile \
    cuda-nvcc-headers \
    libcusolver \
    libcusparse \
    libnpp \
    libnvjitlink \
    libnvfatbin \
    cuda-cudart \
    cuda-nvrtc \
    cuda-cccl \
    cuda-crt \
    cuda-nvml \
    libcufft \
    cuda-driver \
    gcc-source-for-nvcc-13.2.0 \
    gcc-for-nvcc-cross-x86_64 \
    gcc-for-nvcc-runtime \
    tegra-cmake-overrides \
    cuda-compiler-native \
    cuda-cuobjdump-native \
    cuda-cuxxfilt-native \
    cuda-nvcc-native \
    cuda-nvprune-native \
    cuda-nvvm-native \
"

CUSTOMER_RECIPES:meta-llm += " \
    nvidia-driver-x86-64 \
    nvidia-open-gpu-kernel-module \
"

MULTILIBS = ""

BSP_NVIDIA_DERIVED += "${MACHINE}"

BB_NO_NETWORK = "0"

# Do not support live image, becuase of rootfs.img rootfs
# size is greather than or equal to 4GB
IMAGE_FSTYPES:remove = "live"
