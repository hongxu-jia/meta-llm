# Build and Run
 
## 1. Setup and build
### 1.1 Supported BSP: 32 bit arm, 64 bit arm, 32 bit x86, 64 bit x86
```
$ mkdir <project>
$ cd <project>
$ git clone --branch main https://github.com/hongxu-jia/meta-llm.git
$ git clone --branch master git://git.openembedded.org/meta-openembedded
$ git clone --branch master git://git.openembedded.org/openembedded-core oe-core
$ git clone --branch master git://git.openembedded.org/bitbake oe-core/bitbake
$ git clone --branch master git://git.yoctoproject.org/meta-yocto
$ git clone --branch master https://github.com/OE4T/meta-tegra.git
```
 
### 1.2 Prepare build
```
    $ . ./oe-init-build-env <build>

    # Required by python3-openai and python3-ollama
    $ bitbake-layers add-layer <project>/meta-openembedded/meta-oe
    $ bitbake-layers add-layer <project>/meta-openembedded/meta-python

    # Add layer meta-llm to build
    $ bitbake-layers add-layer <project>/meta-llm
    
    # Add layer meta-yocto to build
    $ bitbake-layers add-layer <project>/meta-yocto/meta-yocto-bsp
    
    # Add layer meta-tegra to build
    $ bitbake-layers add-layer <project>/meta-tegra
 
```
 
### 1.3 Fragments
```
    OE_FRAGMENTS += "meta-llm/llama-cpp"
```

### 1.4 Optional, supported BSP with Nvidia GPU (CUDA): it requires layer meta-tegra is available
```
    jetson-orin-nano-devkit-nvme:

        Support CUDA by default
        $ echo 'MACHINE = "jetson-orin-nano-devkit-nvme"' >> conf/local.conf

    genericx86-64:

        $ echo 'MACHINE = "genericx86-64"' >> conf/local.conf

        Add the following line to local.conf to enable CUDA for genericx86-64
        It downloads NVIDIA commercial softwares from network
        $ echo 'require conf/distro/include/cuda-x86-64.inc' >> conf/local.conf
```

### 1.5 Generate wic.zst image 
``` 
    genericx86-64:

        $ echo 'IMAGE_FSTYPES += "wic.zst"' >> conf/local.conf
```

### 1.6 Install Models via recipes
```
Provide Gemma3 and Qwen3.5 models as recipes:

    # https://hf-mirror.com/ggml-org/gemma-3-1b-it-GGUF
    # https://hf-mirror.com/ggml-org/gemma-3-1b-it-GGUF/blob/main/gemma-3-1b-it-Q4_K_M.gguf
    $ echo 'IMAGE_INSTALL:append = " hf-gemma-3-1b-it-gguf-q4"' >> conf/local.conf

    # https://hf-mirror.com/ggml-org/gemma-3-1b-it-GGUF
    # https://hf-mirror.com/ggml-org/gemma-3-1b-it-GGUF/blob/main/gemma-3-1b-it-Q8_0.gguf
    $ echo 'IMAGE_INSTALL:append = " hf-gemma-3-1b-it-gguf-q8"' >> conf/local.conf

    # https://unsloth.ai/docs/models/qwen3.5#qwen3.5-small-0.8b-2b-4b-9b#qwen3.5-small-0.8b-2b-4b-9b
    # https://hf-mirror.com/unsloth/Qwen3.5-0.8B-GGUF
    # https://hf-mirror.com/unsloth/Qwen3.5-0.8B-GGUF/blob/main/Qwen3.5-0.8B-UD-Q4_K_XL.gguf
    $ echo 'IMAGE_INSTALL:append = " hf-qwen3.5-0.8b-gguf-ud-q4"' >> conf/local.conf

    # https://unsloth.ai/docs/models/qwen3.5#qwen3.5-small-0.8b-2b-4b-9b#qwen3.5-small-0.8b-2b-4b-9b
    # https://hf-mirror.com/unsloth/Qwen3.5-2B-GGUF
    # https://hf-mirror.com/unsloth/Qwen3.5-2B-GGUF/blob/main/Qwen3.5-2B-UD-Q4_K_XL.gguf
    $ echo 'IMAGE_INSTALL:append = " hf-qwen3.5-2b-gguf-ud-q4"' >> conf/local.conf
```

### 1.7 Install OpenAI Python API library via recipe
```
The OpenAI Python API library (commonly referred to as the OpenAI Python SDK)
provides a streamlined way for Python developers to interact with OpenAI's REST API.

    $ echo 'IMAGE_INSTALL:append = " python3-openai"' >> conf/local.conf
```

### 1.8 Install llama-swap
```
Run multiple generative AI models on your machine and hot-swap between them on demand.
llama-swap works with any OpenAI and Anthropic API compatible server and is used by
thousands of people to power their local AI workflows.

    $ echo 'IMAGE_INSTALL:append = " llama-swap"' >> conf/local.conf

Set supported hugging face models to llama-swap, the models are provided by above recipes
The llama-swap manages these models as web service on port 8080

    $ echo 'SUPPORT_HF_MODELS = "gemma-3-1b-it-Q4_K_M.gguf gemma-3-1b-it-Q8_0.gguf Qwen3.5-0.8B-UD-Q4_K_XL.gguf Qwen3.5-2B-UD-Q4_K_XL.gguf"' >> conf/local.conf
```


### 1.9 Build image
```
    $ bitbake core-image-minimal
```

## 2. Run Models on QEMU

### 2.1 Start qemu with kvm + 10GB memory:
```
$ runqemu tmp/deploy/images/qemux86-64/core-image-minimal-qemux86-64.rootfs.qemuboot.conf  nographic kvm qemuparams="-m 10240" snapshot
```

### 2.2 List installed models
```
root@qemux86-64:~# ls /usr/share/llama.cpp/gguf_model/ -sh
total 3.6G
 533M Qwen3.5-0.8B-UD-Q4_K_XL.gguf   1.3G Qwen3.5-2B-UD-Q4_K_XL.gguf   769M gemma-3-1b-it-Q4_K_M.gguf  1020M gemma-3-1b-it-Q8_0.gguf
```

### 2.3 Run gemma-3-1b-it-Q4_K_M.gguf model (provided by recipe hf-gemma-3-1b-it-gguf-q4)
```
root@qemux86-64:~# llama-cli --model /usr/share/llama.cpp/gguf_model/gemma-3-1b-it-Q4_K_M.gguf
load_backend: loaded CPU backend from /usr/bin/libggml-cpu-haswell.so

Loading model...  


▄▄ ▄▄
██ ██
██ ██  ▀▀█▄ ███▄███▄  ▀▀█▄    ▄████ ████▄ ████▄
██ ██ ▄█▀██ ██ ██ ██ ▄█▀██    ██    ██ ██ ██ ██
██ ██ ▀█▄██ ██ ██ ██ ▀█▄██ ██ ▀████ ████▀ ████▀
                                    ██    ██
                                    ▀▀    ▀▀

build      : b1-e21cdc1
model      : gemma-3-1b-it-Q4_K_M.gguf
modalities : text

available commands:
  /exit or Ctrl+C     stop or exit
  /regen              regenerate the last response
  /clear              clear the chat history
  /read <file>        add a text file
  /glob <pattern>     add text files using globbing pattern


> hi

Hi there! How’s your day going so far? 😊 

Is there anything you’d like to chat about or any help I can offer?

[ Prompt: 47.0 t/s | Generation: 28.3 t/s ]

> 
```

### 2.4 Run gemma-3-1b-it-Q4_K_M.gguf model as service (provided by recipe hf-gemma-3-1b-it-gguf-q4)
```
# Run web server on port 8081
root@qemux86-64:~# systemctl start hf-gemma-3-1b-it-gguf-q4
root@qemux86-64:~# systemctl status hf-gemma-3-1b-it-gguf-q4
* hf-gemma-3-1b-it-gguf-q4.service - gemma-3-1b-it-Q4_K_M.gguf Service
     Loaded: loaded (/usr/lib/systemd/system/hf-gemma-3-1b-it-gguf-q4.service; disabled; preset: disabled)
     Active: active (running) since Tue 2026-04-21 03:15:25 UTC; 10s ago
 Invocation: e166ada579cf418f8c0728b08b6ccc92
   Main PID: 3266 (llama-server)
      Tasks: 14 (limit: 12018)
     Memory: 325.2M (peak: 325.3M)
        CPU: 1.313s
     CGroup: /system.slice/hf-gemma-3-1b-it-gguf-q4.service
             `-3266 /usr/bin/llama-server --model /usr/share/llama.cpp/gguf_model/gemma-3-1b-it-Q4_K_M.gguf --host 0.0.0.0 --alias gemma-3-1b-it-Q4_K_M --port 8081

Apr 21 03:15:26 qemux86-64 llama-server[3266]: Hi there<end_of_turn>
Apr 21 03:15:26 qemux86-64 llama-server[3266]: <start_of_turn>user
Apr 21 03:15:26 qemux86-64 llama-server[3266]: How are you?<end_of_turn>
Apr 21 03:15:26 qemux86-64 llama-server[3266]: <start_of_turn>model
Apr 21 03:15:26 qemux86-64 llama-server[3266]: '
Apr 21 03:15:26 qemux86-64 llama-server[3266]: srv          init: init: chat template, thinking = 0
Apr 21 03:15:26 qemux86-64 llama-server[3266]: main: model loaded
Apr 21 03:15:26 qemux86-64 llama-server[3266]: main: server is listening on http://0.0.0.0:8081
Apr 21 03:15:26 qemux86-64 llama-server[3266]: main: starting the main loop...
Apr 21 03:15:26 qemux86-64 llama-server[3266]: srv  update_slots: all slots are idle
```

### 2.5 Start web browser to access gemma-3-1b-it-Q4_K_M.gguf model service
```
    http://target-ip-address:8081
```

### 2.6 OpenAI Python API on llama-server
```
# Access gemma-3-1b-it-Q4_K_M.gguf model service (8081 port) to 'Explain quantum physics in one sentence.'

root@qemux86-64:~# cat > ./rest-api.py <<ENDOF
from openai import OpenAI

client = OpenAI(
    base_url='http://localhost:8081/v1/',
    api_key='llama.cpp', # Required but ignored by Ollama
)

response = client.chat.completions.create(
  model="gemma3:1b", # Specify the local model name pulled in Ollama
  messages=[
    {"role": "system", "content": "You are a helpful assistant."},
    {"role": "user", "content": "Explain quantum physics in one sentence."},
  ]
)

print(response.choices[0].message.content)
ENDOF

root@qemux86-64:~# python3 ./rest-api.py 
Quantum physics explores the incredibly strange behavior of matter and energy at the smallest scales, revealing that particles can exist in multiple states simultaneously and are fundamentally uncertain.
```

## 3. Run Models on x86-64 Board
### 3.1 Burn image to USB stick
```
$ sudo umount /dev/sdX
$ unzst tmp/deploy/images/genericx86-64/core-image-minimal-genericx86-64.rootfs.wic.zst
$ sudo dd if=tmp/deploy/images/genericx86-64/core-image-minimal-genericx86-64.rootfs.wic of=/dev/sdX
```

### 3.2 Boot x86-64 board from USB stick
```
Last login: Wed Jan 28 05:02:27 2026 from 128.224.34.164
root@genericx86-64:~#
```

### 3.3 Run Qwen3.5-0.8B-UD-Q4_K_XL.gguf model (provided by recipe hf-qwen3.5-0.8b-gguf-ud-q4)
```
root@genericx86-64:~# llama-cli \
     --model /usr/share/llama.cpp/gguf_model/Qwen3.5-0.8B-UD-Q4_K_XL.gguf \
     --ctx-size 16384 \
     --temp 0.7 \
     --top-p 0.8 \
     --top-k 20 \
     --min-p 0.00 \
     --reasoning off \
     --reasoning-budget 0 \
     --chat-template-kwargs '{"enable_thinking":false}'
ggml_cuda_init: found 1 CUDA devices (Total VRAM: 3768 MiB):
  Device 0: NVIDIA RTX A400, compute capability 8.6, VMM: yes, VRAM: 3768 MiB
load_backend: loaded CPU backend from /usr/bin/libggml-cpu-haswell.so

Loading model...  


▄▄ ▄▄
██ ██
██ ██  ▀▀█▄ ███▄███▄  ▀▀█▄    ▄████ ████▄ ████▄
██ ██ ▄█▀██ ██ ██ ██ ▄█▀██    ██    ██ ██ ██ ██
██ ██ ▀█▄██ ██ ██ ██ ▀█▄██ ██ ▀████ ████▀ ████▀
                                    ██    ██
                                    ▀▀    ▀▀

build      : b1-e21cdc1
model      : Qwen3.5-0.8B-UD-Q4_K_XL.gguf
modalities : text

available commands:
  /exit or Ctrl+C     stop or exit
  /regen              regenerate the last response
  /clear              clear the chat history
  /read <file>        add a text file
  /glob <pattern>     add text files using globbing pattern


> hi

Hello! How can I assist you today?

[ Prompt: 43.7 t/s | Generation: 68.4 t/s ]
```

### 3.4 Run Qwen3.5-0.8B-UD-Q4_K_XL.gguf model as service (provided by recipe hf-qwen3.5-0.8b-gguf-ud-q4)
```
# Run web server on port 8083
root@genericx86-64:~# systemctl start hf-qwen3.5-0.8b-gguf-ud-q4
root@genericx86-64:~# systemctl status hf-qwen3.5-0.8b-gguf-ud-q4
* hf-qwen3.5-0.8b-gguf-ud-q4.service - Qwen3.5-0.8B-UD-Q4_K_XL.gguf Service
     Loaded: loaded (/usr/lib/systemd/system/hf-qwen3.5-0.8b-gguf-ud-q4.service; disabled; preset: disabled)
     Active: active (running) since Tue 2026-04-21 05:08:14 UTC; 7s ago
 Invocation: 060cd73d655a432fbecf20337a708458
   Main PID: 2307 (llama-server)
      Tasks: 15 (limit: 76906)
     Memory: 359.6M (peak: 360.4M)
        CPU: 2.562s
     CGroup: /system.slice/hf-qwen3.5-0.8b-gguf-ud-q4.service
             `-2307 /usr/bin/llama-server --model /usr/share/llama.cpp/gguf_model/Qwen3.5-0.8B-UD-Q4_K_XL.gguf --host 0.0.0.0 --alias Qwen3.5-0.8B --port 8083 --ctx-size 16384 --temp 0.7 --top-p 0.8 --top-k 20 --min-p 0.00

Apr 21 05:08:16 genericx86-64 llama-server[2307]: <|im_start|>user
Apr 21 05:08:16 genericx86-64 llama-server[2307]: How are you?<|im_end|>
Apr 21 05:08:16 genericx86-64 llama-server[2307]: <|im_start|>assistant
Apr 21 05:08:16 genericx86-64 llama-server[2307]: <think>
Apr 21 05:08:16 genericx86-64 llama-server[2307]: '
Apr 21 05:08:16 genericx86-64 llama-server[2307]: srv          init: init: chat template, thinking = 1
Apr 21 05:08:16 genericx86-64 llama-server[2307]: main: model loaded
Apr 21 05:08:16 genericx86-64 llama-server[2307]: main: server is listening on http://0.0.0.0:8083
Apr 21 05:08:16 genericx86-64 llama-server[2307]: main: starting the main loop...
Apr 21 05:08:16 genericx86-64 llama-server[2307]: srv  update_slots: all slots are idle
```

### 3.5 Start web browser to access qwen3.5-0.8b-ud-q4_k_xl.gguf model service
```
    http://target-ip-address:8083
```

### 3.6 Verify GPU
```
root@genericx86-64:~# nvidia-smi 
Tue Apr 21 05:09:16 2026       
+-----------------------------------------------------------------------------------------+
| NVIDIA-SMI 590.48.01              Driver Version: 590.48.01      CUDA Version: 13.1     |
+-----------------------------------------+------------------------+----------------------+
| GPU  Name                 Persistence-M | Bus-Id          Disp.A | Volatile Uncorr. ECC |
| Fan  Temp   Perf          Pwr:Usage/Cap |           Memory-Usage | GPU-Util  Compute M. |
|                                         |                        |               MIG M. |
|=========================================+========================+======================|
|   0  NVIDIA RTX A400                Off |   00000000:01:00.0 Off |                  N/A |
| 30%   41C    P0            N/A  /   50W |    2652MiB /   4094MiB |      0%      Default |
|                                         |                        |                  N/A |
+-----------------------------------------+------------------------+----------------------+

+-----------------------------------------------------------------------------------------+
| Processes:                                                                              |
|  GPU   GI   CI              PID   Type   Process name                        GPU Memory |
|        ID   ID                                                               Usage      |
|=========================================================================================|
|    0   N/A  N/A            2307      C   /usr/bin/llama-server                  1350MiB |
|    0   N/A  N/A            2360      C   llama-cli                              1292MiB |
+-----------------------------------------------------------------------------------------+
```

## 4. Run Models on orin Board
### 4.1 Burn image to orin Board
```
https://github.com/OE4T/meta-tegra/blob/master/docs/Flashing-the-Jetson-Dev-Kit.md
```

### 4.2 Boot orin Board
```
root@nvidia-orin-nx:~#
```

### 4.3 llama-swap
```
root@nvidia-orin-nx:~# systemctl status llama-swap
* llama-swap.service - llama-swap Service
     Loaded: loaded (/usr/lib/systemd/system/llama-swap.service; enabled; preset: enabled)
     Active: active (running) since Thu 1970-01-01 00:00:29 UTC; 18h ago
 Invocation: 64c311e6d5424b928efecc1c7ec7e106
   Main PID: 830 (llama-swap)
      Tasks: 11 (limit: 18598)
     Memory: 16.4M (peak: 17.3M)
        CPU: 2.062s
     CGroup: /system.slice/llama-swap.service
             `-830 /usr/bin/llama-swap -config /usr/share/llama-swap/config.yaml -listen 0.0.0.0:8080

Notice: journal has been rotated since unit was started, output may be incomplete.
```

### 4.4 Start web browser to access llama-swap service
```
    http://target-ip-address:8080
```

### 4.5 OpenAI Python API on llama-swap
```
# Access llama-swap service (8080) to use model gemma-3-1b-it-Q8_0.gguf to 'Explain quantum physics in one sentence.'
root@nvidia-orin-nx:~# cat > ./rest-api.py <<ENDOF
from openai import OpenAI

client = OpenAI(
    base_url='http://localhost:8080/v1/',
    api_key='llama.cpp', # Required but ignored by Ollama
)

response = client.chat.completions.create(
  model="gemma-3-1b-it-Q8_0.gguf", # Specify the local model name pulled in Ollama
  messages=[
    {"role": "system", "content": "You are a helpful assistant."},
    {"role": "user", "content": "Explain quantum physics in one sentence."},
  ]
)

print(response.choices[0].message.content)
ENDOF
root@nvidia-orin-nx:~# 
root@nvidia-orin-nx:~# python3 ./rest-api.py 
Quantum physics describes the bizarre and counterintuitive behavior of matter and energy at the atomic and subatomic levels, where things can exist in multiple states simultaneously until observed.
```
