# Build and Run

## 1. Setup and build
### 1.1 Supported BSP: 32 bit arm, 64 bit arm, 32 bit x86, 64 bit x86
```
$ mkdir <project>
$ cd <project>
$ git clone --branch main https://github.com/hongxu-jia/meta-llm.git
$ git clone --branch master git://git.openembedded.org/openembedded-core oe-core
$ git clone --branch master git://git.openembedded.org/bitbake oe-core/bitbake
$ git clone --branch master git://git.openembedded.org/meta-openembedded
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
    OE_FRAGMENTS += "meta-llm/ollama"
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

    # https://ollama.com/library/gemma3:1b
    $ echo 'IMAGE_INSTALL:append = " ol-gemma3-1b"' >> conf/local.conf

    # https://ollama.com/library/gemma3:4b
    $ echo 'IMAGE_INSTALL:append = " ol-gemma3-4b"' >> conf/local.conf

    # https://ollama.com/library/qwen3.5:0.8b
    $ echo 'IMAGE_INSTALL:append = " ol-qwen3.5-0.8b"' >> conf/local.conf

    # https://ollama.com/library/qwen3.5:2b
    $ echo 'IMAGE_INSTALL:append = " ol-qwen3.5-2b"' >> conf/local.conf

    # https://ollama.com/library/qwen3.5:4b
    $ echo 'IMAGE_INSTALL:append = " ol-qwen3.5-4b"' >> conf/local.conf
```

### 1.7 Install Ollama Python Library via recipe
```
The Ollama Python library provides a high-level, easy-to-use SDK for integrating
large language models (LLMs) into Python applications. It allows developers to
interact with the Ollama local server to generate text, manage models, and
use advanced features like tool calling and structured outputs. 

    $ echo 'IMAGE_INSTALL:append = " python3-ollama"' >> conf/local.conf
```
### 1.8 Install OpenAI Python API library via recipe
```
The OpenAI Python API library (commonly referred to as the OpenAI Python SDK)
provides a streamlined way for Python developers to interact with OpenAI's REST API.

    $ echo 'IMAGE_INSTALL:append = " python3-openai"' >> conf/local.conf
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
root@qemux86-64:~# ollama list
NAME            ID              SIZE      MODIFIED     
qwen3.5:0.8b    f3817196d142    1.0 GB    15 years ago    
qwen3.5:2b      324d162be6ca    2.7 GB    15 years ago    
qwen3.5:4b      2a654d98e6fb    3.4 GB    15 years ago    
gemma3:1b       8648f39daa8f    815 MB    15 years ago    
gemma3:4b       a2af6cc3eb7f    3.3 GB    15 years ago
```

### 2.3 Get status of Ollama server
```
root@qemux86-64:~# systemctl status ollama
* ollama.service - Ollama Service
     Loaded: loaded (/usr/lib/systemd/system/ollama.service; enabled; preset: enabled)
     Active: active (running) since Mon 2026-04-20 07:42:16 UTC; 2min 49s ago
 Invocation: f9c4b0710a2a4019a3737cd8ad4e32ff
   Main PID: 246 (ollama)
      Tasks: 8 (limit: 12018)
     Memory: 58.1M (peak: 68.6M)
        CPU: 277ms
     CGroup: /system.slice/ollama.service
             `-246 /usr/bin/ollama serve

Apr 20 07:42:17 qemux86-64 ollama[246]: time=2026-04-20T07:42:17.329Z level=INFO source=routes.go:1746 msg="Ollama cloud disabled: false"
Apr 20 07:42:17 qemux86-64 ollama[246]: time=2026-04-20T07:42:17.361Z level=INFO source=images.go:499 msg="total blobs: 15"
Apr 20 07:42:17 qemux86-64 ollama[246]: time=2026-04-20T07:42:17.361Z level=INFO source=images.go:506 msg="total unused blobs removed: 0"
Apr 20 07:42:17 qemux86-64 ollama[246]: time=2026-04-20T07:42:17.361Z level=INFO source=routes.go:1802 msg="Listening on [::]:11434 (version 0.20.3)"
Apr 20 07:42:17 qemux86-64 ollama[246]: time=2026-04-20T07:42:17.362Z level=INFO source=runner.go:67 msg="discovering available GPUs..."
Apr 20 07:42:17 qemux86-64 ollama[246]: time=2026-04-20T07:42:17.364Z level=INFO source=server.go:432 msg="starting runner" cmd="/usr/bin/ollama runner --ollama-engine --port 36641"
Apr 20 07:42:17 qemux86-64 ollama[246]: time=2026-04-20T07:42:17.575Z level=INFO source=types.go:60 msg="inference compute" id=cpu library=cpu compute="" name=cpu description=cpu libdirs=ollama driver="" pci_id="" type="" total="9.8 GiB" available
="9.6 GiB"
Apr 20 07:42:17 qemux86-64 ollama[246]: time=2026-04-20T07:42:17.576Z level=INFO source=routes.go:1852 msg="vram-based default context" total_vram="0 B" default_num_ctx=4096
Apr 20 07:43:28 qemux86-64 ollama[246]: [GIN] 2026/04/20 - 07:43:28 | 200 |    1.136913ms |       127.0.0.1 | HEAD     "/"
Apr 20 07:43:28 qemux86-64 ollama[246]: [GIN] 2026/04/20 - 07:43:28 | 200 |   42.277819ms |       127.0.0.1 | GET      "/api/tags"
```

### 2.4 Run gemma3:1b model
```
root@qemux86-64:~# ollama run gemma3:1b
>>> hi
Hi there! How's your day going so far? 😊 

Is there anything you’d like to chat about, or anything I can help you with today?

>>> Send a message (/? for help)
```

### 2.5 Verify CPU
```
root@qemux86-64:~# ollama ps  
NAME         ID              SIZE      PROCESSOR    CONTEXT    UNTIL              
gemma3:1b    8648f39daa8f    1.2 GB    100% CPU     4096       3 minutes from now
```

### 2.6 Ollama Python API
```
# Run gemma3:1b to ask 'Why is the sky blue?'

root@qemux86-64:~# cat > ./rest-api.py <<ENDOF
import ollama
response = ollama.chat(model='gemma3:1b', messages=[
  {'role': 'user', 'content': 'Why is the sky blue?'},
])
print(response['message']['content'])
ENDOF

root@qemux86-64:~# python3 ./rest-api.py 
The sky is blue due to a phenomenon called **Rayleigh scattering**. Here's a breakdown of why it happens:

* **Sunlight is made up of all the colors of the rainbow.** Think of a prism splitting sunlight into its components.

* **Sunlight enters the Earth's atmosphere.** The atmosphere is full of tiny particles like nitrogen and oxygen molecules.

* **Rayleigh scattering occurs when sunlight hits these particles.**  This scattering happens more readily with light waves with shorter wavelengths – blue and violet light. 

* **Blue light is scattered more than other colors.**  It bounces around in all directions.  Because our eyes are more sensitive to blue than violet, we see a blue sky!

**Think of it like this:** Imagine throwing small marbles (blue light) and large marbles (red light) at a bumpy surface. The small marbles will bounce around more and scatter everywhere, while the larger marbles will roll straight through.

**Why not violet?** Violet light is scattered even more than blue, but:
    * The sun emits less violet light than blue.
    * Our eyes are more sensitive to blue than violet. 

**In short, the sky is blue because the shorter wavelengths of blue light are scattered more by the air molecules in the atmosphere.**

**Want to learn more? Here's a quick visual:**

[https://www.youtube.com/watch?v=L7s87G1GzQ8](https://www.youtube.com/watch?v=L7s87G1GzQ8) (A short animated explanation)

Do you want to know anything else about why the sky is blue, like how it changes during sunrise/sunset?

```

### 2.7 OpenAI Python API
```
# Run gemma3:1b to 'Explain quantum physics in one sentence.'
root@qemux86-64:~# cat > ./rest-api.py <<ENDOF
from openai import OpenAI

client = OpenAI(
    base_url='http://localhost:11434/v1/',
    api_key='ollama', # Required but ignored by Ollama
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

root@qemux86-64:~# python3 rest-api.py 
Quantum physics describes the behavior of matter and energy at the smallest scales, where things can exist in multiple states simultaneously and defy our classical understanding of position and time. 

---

Would you like me to elaborate on any specific aspect of this, like its implications or a simpler explanation?

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
root@genericx86-64:~#:
```

### 3.3 Run gemma3:1b model
```
root@genericx86-64:~# ollama run gemma3:1b
>>> hi
Hi there! How’s your day going so far? 😊 

Is there anything you’d like to chat about or tell me?

>>> Send a message (/? for help)
```

### 3.4 Verify GPU
```
root@genericx86-64:~# ollama ps
NAME         ID              SIZE      PROCESSOR    CONTEXT    UNTIL              
gemma3:1b    8648f39daa8f    1.2 GB    100% GPU     4096       4 minutes from now

root@genericx86-64:~# nvidia-smi 
Mon Apr 20 09:12:24 2026       
+-----------------------------------------------------------------------------------------+
| NVIDIA-SMI 590.48.01              Driver Version: 590.48.01      CUDA Version: 13.1     |
+-----------------------------------------+------------------------+----------------------+
| GPU  Name                 Persistence-M | Bus-Id          Disp.A | Volatile Uncorr. ECC |
| Fan  Temp   Perf          Pwr:Usage/Cap |           Memory-Usage | GPU-Util  Compute M. |
|                                         |                        |               MIG M. |
|=========================================+========================+======================|
|   0  NVIDIA RTX A400                Off |   00000000:01:00.0 Off |                  N/A |
| 30%   41C    P8            N/A  /   50W |     950MiB /   4094MiB |      0%      Default |
|                                         |                        |                  N/A |
+-----------------------------------------+------------------------+----------------------+

+-----------------------------------------------------------------------------------------+
| Processes:                                                                              |
|  GPU   GI   CI              PID   Type   Process name                        GPU Memory |
|        ID   ID                                                               Usage      |
|=========================================================================================|
|    0   N/A  N/A             754      C   /usr/bin/ollama                         944MiB |
+-----------------------------------------------------------------------------------------+
```

## 4. Run Models on orin Board
### 4.1 Burn image to orin Board
```
Refer https://github.com/OE4T/meta-tegra/blob/master/docs/Flashing-the-Jetson-Dev-Kit.md
```

### 4.2 Boot orin Board
```
root@nvidia-orin-nx:~#
```

### 4.3 Run gemma3:1b model
```
root@nvidia-orin-nx:~# ollama run gemma3:1b
>>> hi
Hi there! How's your day going? 😊

Is there anything you'd like to chat about or tell me?

>>> Send a message (/? for help)
```

### 4.4 Verify GPU
```
root@nvidia-orin-nx:~# ollama ps
NAME         ID              SIZE      PROCESSOR    CONTEXT    UNTIL
gemma3:1b    8648f39daa8f    1.2 GB    100% GPU     4096       4 minutes from now
```
